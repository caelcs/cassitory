package uk.co.caeldev.cassitory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.apache.commons.text.WordUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.co.caeldev.cassitory.CassitoryEntityFunctions.*;

@SupportedAnnotationTypes("uk.co.caeldev.cassitory.CassitoryEntity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CassitoryEntityProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<TypeElement> elements = findElements(roundEnv);

        List<JavaFile> classes = createClasses(elements);

        for (JavaFile it : classes) {
            try {
                it.writeTo(filer);
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    private List<JavaFile> createClasses(List<TypeElement> elementsAnnotated) {
        return elementsAnnotated.stream().map(element -> {
            TypeSpec.Builder type = TypeSpec
                    .classBuilder(creatorClassName.apply(element))
                    .addField(entityField.apply(element))
                    .addMethod(constructor.apply(element))
                    .addModifiers(Modifier.PUBLIC);

            generateCreatorFields(element, type);
            generateCreatorListField(element, type);

            return JavaFile.builder(this.elements.getPackageOf(element).getQualifiedName().toString(), type.build())
                    .addStaticImport(ClassName.get("com.google.common.collect", "Lists"), "newArrayList").build();
        }).collect(toList());
    }

    private void generateCreatorListField(TypeElement classAnnotated, TypeSpec.Builder type) {
        ParameterizedTypeName collectionTypeName = ParameterizedTypeName.get(List.class, Supplier.class);

        String creatorArguments = targetCassandraEntityFrom.apply(classAnnotated.getAnnotation(CassitoryEntity.class)).stream()
                .map(targetCassandraEntityClass -> {
                    ClassName className = ClassName.bestGuess(targetCassandraEntityClass.toString());
                    return creatorFieldNameClassName.apply(className);})
                .collect(Collectors.joining(", "));

        FieldSpec creators = FieldSpec.builder(collectionTypeName, "creators")
                .addModifiers(Modifier.PUBLIC)
                .initializer("$N($N)", "newArrayList", creatorArguments)
                .build();

        type.addField(creators);
    }

    private void generateCreatorFields(TypeElement classAnnotated, TypeSpec.Builder type) {
        targetCassandraEntityFrom.apply(classAnnotated.getAnnotation(CassitoryEntity.class)).stream()
                .map(it -> ClassName.bestGuess(it.toString()))
                .peek(it -> type.addMethod(createCreatorMethod(classAnnotated, it)))
                .forEach(targetCassandraEntityClass -> {
                    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Supplier.class), targetCassandraEntityClass);
                    FieldSpec fieldSpec = FieldSpec.builder(parameterizedTypeName, creatorFieldNameClassName.apply(targetCassandraEntityClass))
                            .addModifiers(Modifier.PRIVATE)
                            .initializer(creatorInit(targetCassandraEntityClass))
                            .build();

                    type.addField(fieldSpec);
                });
    }

    private MethodSpec createCreatorMethod(TypeElement classAnnotated, ClassName targetCassandraEntityClass) {
        MethodSpec.Builder method = MethodSpec.methodBuilder(createTargetEntityMethodName.apply(targetCassandraEntityClass))
                .returns(targetCassandraEntityClass).addModifiers(Modifier.PRIVATE);
        method.addCode(buildCreateMethodBody(classAnnotated, targetCassandraEntityClass));
        return method.build();
    }

    private CodeBlock buildCreateMethodBody(TypeElement classAnnotated, ClassName targetCassandraEntityClass) {
        CodeBlock.Builder methodBody = CodeBlock.builder().addStatement("$T $N = new $T()", targetCassandraEntityClass, fieldNameClassName.apply(targetCassandraEntityClass), targetCassandraEntityClass);
        Map<String, String> fieldMapping = CassitoryEntityFunctions.fieldMapping.apply(classAnnotated, targetCassandraEntityClass);
        fieldMapping.entrySet().stream().forEach(it ->
            methodBody.addStatement("$N.set$N($N.get$N())", fieldNameClassName.apply(targetCassandraEntityClass), WordUtils.capitalize(it.getValue()), fieldNameClassName.apply(ClassName.get(classAnnotated)), WordUtils.capitalize(it.getKey()))
        );
        methodBody.addStatement("return $N", fieldNameClassName.apply(targetCassandraEntityClass));
        return methodBody.build();
    }

    private CodeBlock creatorInit(ClassName targetCassandraEntityClass) {
        return CodeBlock.of("() -> $N()", createTargetEntityMethodName.apply(targetCassandraEntityClass));
    }

    private List<TypeElement> findElements(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(CassitoryEntity.class).stream()
                .peek(element -> {
                    if (element.getKind() != ElementKind.CLASS) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                    }
                })
                .map(element -> (TypeElement) element).collect(toList());
    }

}
