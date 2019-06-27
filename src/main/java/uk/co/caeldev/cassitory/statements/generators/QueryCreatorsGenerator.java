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
import static uk.co.caeldev.cassitory.statements.functions.BaseFunctions.destinationPackage;
import static uk.co.caeldev.cassitory.statements.functions.CreatorsFunctions.byPkIfIsNeeded;
import static uk.co.caeldev.cassitory.statements.functions.CreatorsFunctions.getTableMappingDescriptors;

public class QueryCreatorsGenerator implements Generator {

    private Messager messager;
    private Elements elements;

    public QueryCreatorsGenerator(Messager messager, Elements elements) {
        this.messager = messager;
        this.elements = elements;
    }

    @Override
    public List<JavaFile> generate(List<TypeElement> elementsAnnotated) {
        return elementsAnnotated.stream().map(classAnnotated -> {
            TypeSpec.Builder type = TypeSpec
                    .classBuilder(BaseFunctions.queryCreatorClassName.apply(classAnnotated))
                    .addModifiers(Modifier.PUBLIC);

            List<TableMappingDescriptor> tableMappingDescriptors = getTableMappingDescriptors(classAnnotated);

            generateOperationMethods(tableMappingDescriptors, type, classAnnotated);

            return JavaFile.builder(destinationPackage.apply(classAnnotated, this.elements), type.build()).build();
        }).collect(toList());
    }

    private void generateOperationMethods(List<TableMappingDescriptor> tableMappingDescriptors, TypeSpec.Builder type, TypeElement classAnnotated) {
        ClassName stringType = ClassName.get(String.class);
        ParameterizedTypeName fieldType =
                ParameterizedTypeName.get(ClassName.get(Map.class), stringType, stringType);

        //Get all the tables and related fields.
        String saveParams = buildSaveParams(tableMappingDescriptors);
        FieldSpec saveQueries = FieldSpec.builder(fieldType, "saveQueries", Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$T.of($N)", ImmutableMap.class, saveParams).build();
        type.addField(saveQueries);

        //Get All the tables and related fields that are pk.
        String deleteParams = buildDeleteParams(tableMappingDescriptors);
        FieldSpec deleteQueries = FieldSpec.builder(fieldType, "deleteQueries", Modifier.PUBLIC, Modifier.STATIC)
                .initializer("$T.of($N)", ImmutableMap.class, deleteParams).build();
        type.addField(deleteQueries);
    }

    private String buildDeleteParams(List<TableMappingDescriptor> tableMappingDescriptors) {
        return tableMappingDescriptors.stream().map(it -> {
            String criteria = buildCriteria(it.getMappingDescriptors());
            return String.format("\"%s\", \"DELETE FROM %s WHERE %s;\"", it.getTableName(), it.getTableName(), criteria);
        }).collect(Collectors.joining(", "));
    }

    private String buildCriteria(Map<String, MappingDescriptor> mappingDescriptors) {
        return mappingDescriptors.entrySet().stream()
                .map(it -> String.format("%s=?", it.getValue().getField()))
                .collect(Collectors.joining(" AND "));
    }

    private String buildSaveParams(List<TableMappingDescriptor> tableMappingDescriptors) {
        return tableMappingDescriptors.stream().map(it -> {
            String fields = buildFields(it.getMappingDescriptors());
            String paramsSymbols = buildParamSymbols(it.getMappingDescriptors());
            return String.format("\"%s\", \"INSERT INTO %s (%s) VALUES (%s);\"", it.getTableName(), it.getTableName(), fields, paramsSymbols);
        }).collect(Collectors.joining(", "));
    }

    private String buildParamSymbols(Map<String, MappingDescriptor> mappingDescriptors) {
        return mappingDescriptors.entrySet().stream()
                .map(it -> "?")
                .collect(Collectors.joining(", "));
    }

    private String buildFields(Map<String, MappingDescriptor> mappingDescriptors) {
        return mappingDescriptors.entrySet().stream()
                .filter(byPkIfIsNeeded(false))
                .map(it -> it.getValue().getField())
                .collect(Collectors.joining(", "));
    }

    private List<TableMappingDescriptor> getTableMappingDescriptors(TypeElement classAnnotated) {
        try {
            return getTableMappingDescriptors.apply(classAnnotated);
        } catch (IllegalArgumentException ex) {
            this.messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
            return Lists.newArrayList();
        }
    }
}
