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

    private List<JavaFile> createClasses(List<TypeElement> elements) {
        return elements.stream().map(element -> {
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

    private void generateCreatorListField(TypeElement element, TypeSpec.Builder type) {
        ParameterizedTypeName collectionTypeName = ParameterizedTypeName.get(List.class, Supplier.class);

        String creatorArguments = targetCassandraEntityFrom.apply(element.getAnnotation(CassitoryEntity.class)).stream()
                .map(clazz -> {
                    ClassName className = ClassName.bestGuess(clazz.toString());
                    return creatorFieldNameClassName.apply(className);})
                .collect(Collectors.joining(", "));

        FieldSpec creators = FieldSpec.builder(collectionTypeName, "creators")
                .addModifiers(Modifier.PUBLIC)
                .initializer("$N($N)", "newArrayList", creatorArguments)
                .build();

        type.addField(creators);
    }

    private void generateCreatorFields(TypeElement element, TypeSpec.Builder type) {
        targetCassandraEntityFrom.apply(element.getAnnotation(CassitoryEntity.class)).stream()
                .map(it -> ClassName.bestGuess(it.toString()))
                .peek(it -> type.addMethod(createCreatorMethod(element, it)))
                .forEach(targetCassandraEntity -> {
                    ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(Supplier.class), targetCassandraEntity);
                    FieldSpec fieldSpec = FieldSpec.builder(parameterizedTypeName, creatorFieldNameClassName.apply(targetCassandraEntity))
                            .addModifiers(Modifier.PRIVATE)
                            .initializer(creatorInit(targetCassandraEntity))
                            .build();

                    type.addField(fieldSpec);
                });
    }

    private MethodSpec createCreatorMethod(TypeElement element, ClassName targetCassandraEntity) {
        MethodSpec.Builder method = MethodSpec.methodBuilder("create" + targetCassandraEntity.simpleName()).returns(targetCassandraEntity).addModifiers(Modifier.PRIVATE);
        CodeBlock.Builder methodBody = CodeBlock.builder().addStatement("$T $N = new $T()", targetCassandraEntity, fieldNameClassName.apply(targetCassandraEntity), targetCassandraEntity);
        Map<String, String> fieldMapping = CassitoryEntityFunctions.fieldMapping.apply(element, targetCassandraEntity);
        fieldMapping.entrySet().stream().forEach(it ->
            methodBody.addStatement("$N.set$N($N.get$N())", fieldNameClassName.apply(targetCassandraEntity), WordUtils.capitalize(it.getValue()), fieldNameClassName.apply(ClassName.get(element)), WordUtils.capitalize(it.getKey()))
        );
        methodBody.addStatement("return $N", fieldNameClassName.apply(targetCassandraEntity));
        method.addCode(methodBody.build());
        return method.build();
    }

    private CodeBlock creatorInit(ClassName clazz) {
        return CodeBlock.of("() -> create$N()", clazz.simpleName());
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
