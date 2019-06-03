package uk.co.caeldev.cassitory.generators;

import com.google.common.collect.Maps;
import com.squareup.javapoet.*;
import org.apache.commons.text.WordUtils;
import uk.co.caeldev.cassitory.CassitoryEntity;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.co.caeldev.cassitory.CommonFunctions.fieldNameClassName;
import static uk.co.caeldev.cassitory.generators.CreatorFunctions.*;

public class CreatorGenerator implements Generator {

    private Messager messager;
    private Elements elements;

    public CreatorGenerator(Messager messager, Elements elements) {
        this.messager = messager;
        this.elements = elements;
    }

    @Override
    public List<JavaFile> generate(List<TypeElement> elementsAnnotated) {
        return elementsAnnotated.stream().map(classAnnotated -> {
            TypeSpec.Builder type = TypeSpec
                    .classBuilder(creatorClassName.apply(classAnnotated))
                    .addField(entityField.apply(classAnnotated))
                    .addMethod(constructor.apply(classAnnotated))
                    .addModifiers(Modifier.PUBLIC);

            List<String> targetClasses = CreatorFunctions.targetClasses.apply(classAnnotated, CassitoryEntity.class);

            validateTargetClasses(targetClasses);

            generateCreatorFields(classAnnotated, type, targetClasses);
            generateCreatorListField(type, targetClasses);

            return JavaFile.builder(this.elements.getPackageOf(classAnnotated).getQualifiedName().toString(), type.build())
                    .addStaticImport(ClassName.get("com.google.common.collect", "Lists"), "newArrayList").build();
        }).collect(toList());
    }

    private void validateTargetClasses(List<String> targetClasses) {
        try {
            validateDuplicateTargetClasses.accept(targetClasses);
            validateEmptyTargetClasses.accept(targetClasses);
        } catch (IllegalArgumentException ex) {
            messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
        }
    }

    private void generateCreatorListField(TypeSpec.Builder type, List<String> targetClasses) {
        ParameterizedTypeName collectionTypeName = ParameterizedTypeName.get(List.class, Supplier.class);

        String creatorArguments = targetClasses.stream()
                .map(targetCassandraEntityClass -> {
                    ClassName className = ClassName.bestGuess(targetCassandraEntityClass);
                    return creatorFieldNameClassName.apply(className);
                })
                .collect(Collectors.joining(", "));

        FieldSpec creators = FieldSpec.builder(collectionTypeName, "creators")
                .addModifiers(Modifier.PUBLIC)
                .initializer("$N($N)", "newArrayList", creatorArguments)
                .build();

        type.addField(creators);
    }

    private void generateCreatorFields(TypeElement classAnnotated, TypeSpec.Builder type, List<String> targetClasses) {
        targetClasses.stream()
                .map(it -> ClassName.bestGuess(it))
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
        Map<String, String> fieldMapping = getFieldMappings(classAnnotated, targetCassandraEntityClass);
        fieldMapping.entrySet().stream().forEach(it ->
                methodBody.addStatement("$N.set$N($N.get$N())", fieldNameClassName.apply(targetCassandraEntityClass), WordUtils.capitalize(it.getValue()), fieldNameClassName.apply(ClassName.get(classAnnotated)), WordUtils.capitalize(it.getKey()))
        );
        methodBody.addStatement("return $N", fieldNameClassName.apply(targetCassandraEntityClass));
        return methodBody.build();
    }

    private Map<String, String> getFieldMappings(TypeElement classAnnotated, ClassName targetCassandraEntityClass) {
        Map<String, String> result = getFieldsFromMapping(classAnnotated, targetCassandraEntityClass, fieldMappings);
        result.putAll(getFieldsFromMapping(classAnnotated, targetCassandraEntityClass, fieldMapping));
        return result;
    }

    private Map<String, String> getFieldsFromMapping(TypeElement classAnnotated, ClassName targetCassandraEntityClass, BiFunction<TypeElement, ClassName, Map<String, String>> fieldsSource) {
        try {
            return fieldsSource.apply(classAnnotated, targetCassandraEntityClass);
        } catch (IllegalArgumentException ex) {
            messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
            return Maps.newHashMap();
        }
    }

    private CodeBlock creatorInit(ClassName targetCassandraEntityClass) {
        return CodeBlock.of("() -> $N()", createTargetEntityMethodName.apply(targetCassandraEntityClass));
    }
}
