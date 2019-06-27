package uk.co.caeldev.cassitory.statements.generators;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.squareup.javapoet.*;
import uk.co.caeldev.cassitory.base.Generator;
import uk.co.caeldev.cassitory.statements.functions.BaseFunctions;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.co.caeldev.cassitory.base.BaseFunctions.*;
import static uk.co.caeldev.cassitory.statements.functions.BaseFunctions.destinationPackage;
import static uk.co.caeldev.cassitory.statements.functions.CreatorsFunctions.byPkIfIsNeeded;
import static uk.co.caeldev.cassitory.statements.functions.CreatorsFunctions.getTableMappingDescriptors;

public class ParameterCreatorsGenerator implements Generator {

    private Messager messager;
    private Elements elements;

    public ParameterCreatorsGenerator(Messager messager, Elements elements) {
        this.messager = messager;
        this.elements = elements;
    }

    @Override
    public List<JavaFile> generate(List<TypeElement> elementsAnnotated) {
        return elementsAnnotated.stream().map(classAnnotated -> {
            TypeSpec.Builder type = TypeSpec
                    .classBuilder(BaseFunctions.parameterCreatorClassName.apply(classAnnotated))
                    .addField(entityField.apply(classAnnotated))
                    .addMethod(constructor.apply(classAnnotated))
                    .addModifiers(Modifier.PUBLIC);

            List<TableMappingDescriptor> tableMappingDescriptors = getTableMappingDescriptors(classAnnotated);

            generateOperationMethods(tableMappingDescriptors, type, classAnnotated);

            return JavaFile.builder(destinationPackage.apply(classAnnotated, this.elements), type.build())
                    .addStaticImport(ClassName.get("com.google.common.collect", "Lists"), "newArrayList").build();
        }).collect(toList());
    }

    private List<TableMappingDescriptor> getTableMappingDescriptors(TypeElement classAnnotated) {
        try {
            return getTableMappingDescriptors.apply(classAnnotated);
        } catch (IllegalArgumentException ex) {
            messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
            return Lists.newArrayList();
        }
    }

    private void generateOperationMethods(List<TableMappingDescriptor> descriptors, TypeSpec.Builder type, TypeElement classAnnotated) {
        ArrayTypeName valueType = ArrayTypeName.of(Object.class);
        ClassName keyType = ClassName.get(String.class);
        ParameterizedTypeName returnType =
                ParameterizedTypeName.get(ClassName.get(Map.class), keyType, valueType);

        MethodSpec createSaveParameters = MethodSpec.methodBuilder("createSaveParameters")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement("return $T.of($N)", ImmutableMap.class, buildValuesGroupByTable(descriptors, classAnnotated, false))
                .build();

        type.addMethod(createSaveParameters);

        MethodSpec createDeleteParameters = MethodSpec.methodBuilder("createDeleteParameters")
                .addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addStatement("return $T.of($N)", ImmutableMap.class, buildValuesGroupByTable(descriptors, classAnnotated, true))
                .build();

        type.addMethod(createDeleteParameters);

    }

    private String buildValuesGroupByTable(List<TableMappingDescriptor> tableMappingDescriptors, TypeElement classAnnotated, boolean filterByPk) {
        return tableMappingDescriptors.stream().map(it ->
            String.format("\"%s\", newArrayList(%s).toArray()", it.getTableName(), buildListElements(it.getMappingDescriptors(), classAnnotated, filterByPk)))
                .collect(Collectors.joining(", "));
    }

    private String buildListElements(Map<String, MappingDescriptor> mappingDescriptor, TypeElement classAnnotated, boolean filterByPk) {
        return mappingDescriptor.entrySet().stream()
                .filter(byPkIfIsNeeded(filterByPk))
                .map(it -> String.format("%s.get%s()", unCapitaliseName.apply(classAnnotated.getSimpleName().toString()), capitaliseName.apply(it.getKey())))
                .collect(Collectors.joining(", "));
    }
}
