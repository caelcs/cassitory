package uk.co.caeldev.cassitory.statements.functions;

import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import org.apache.commons.lang3.StringUtils;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;
import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.Mappings;
import uk.co.caeldev.cassitory.statements.generators.MappingDescriptor;
import uk.co.caeldev.cassitory.statements.generators.TableMappingDescriptor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static uk.co.caeldev.cassitory.statements.functions.BaseFunctions.getField;

public class CreatorsFunctions {

    private static Function<String, String> extractTableName = (annotationString) -> StringUtils.substringBetween(annotationString, "\"");

    private static BiPredicate<String, String> doesContainsTableName = (itr, tableName) -> {
        String extractedTableName = extractTableName.apply(itr);
        return tableName.equals(extractedTableName.trim());
    };

    private static Consumer<List<String>> validateTablesAreNotBlank = (annotationsMetadata) -> {
        List<String> nonEmptyTableFields = annotationsMetadata.stream()
                .map(itr -> extractTableName.apply(itr.split("=")[1]))
                .filter(itr -> StringUtils.isNotBlank(itr))
                .collect(toList());
        if (nonEmptyTableFields.size() != annotationsMetadata.size()) {
            throw new IllegalArgumentException("table field cannot by null or empty");
        }
    };

    private static BiFunction<Element, String, MappingDescriptor> valueOfMappings = (Element it, String tableName) -> {
        List<Object> annotationMirrors = it.getAnnotationMirrors().get(0).getElementValues().values().stream()
                .map(itr -> itr.getValue())
                .collect(toList());

        List<String> annotationMetadata = (List<String>) ((List) annotationMirrors.get(0)).stream()
                .map(itr -> itr.toString())
                .collect(toList());


        validateTablesAreNotBlank.accept(annotationMetadata);

        List<MappingDescriptor> fieldsMapped = annotationMetadata.stream()
                .filter(itr -> doesContainsTableName.test(itr, tableName))
                .map(getField()).collect(toList());

        if (fieldsMapped.size() > 1) {
            throw new IllegalArgumentException("Table contains duplicated classes");
        }

        MappingDescriptor mappingDescriptor = fieldsMapped.get(0);
        String foundValue = mappingDescriptor.getField();

        if (foundValue.isEmpty()) {
            mappingDescriptor.setField(it.getSimpleName().toString());
        }

        return mappingDescriptor;
    };

    private static Function<Element, List<String>> tableNameForMappings = (Element classAnnotated) -> {
        Optional<? extends AnnotationMirror> opAnnotation = classAnnotated.getAnnotationMirrors().stream()
                .filter(it -> it.getAnnotationType().toString().equals(ClassName.get(Mappings.class).toString()))
                .findFirst();

        if (!opAnnotation.isPresent()) {
            return Lists.newArrayList();
        }
        return ((List<?>) opAnnotation.get().getElementValues().values().stream().findFirst().get().getValue())
                .stream().map(it -> it.toString()).collect(toList());
    };

    private static BiConsumer<List<String>, String> validateBlankTableNames = (List<String> tableNames, String subject) -> {
        boolean isTableNameBlank = tableNames.stream().anyMatch(tableNameIt -> tableNameIt.trim().isEmpty());
        if (isTableNameBlank) {
            throw new IllegalArgumentException(String.format("%s cannot be null or empty", subject));
        }
    };

    private static BiFunction<String, Element, Boolean> containsTableNameInMappings = (String tableName, Element element) -> {
        List<String> tableNames = tableNameForMappings.apply(element);
        return tableNames.stream().anyMatch(itr -> doesContainsTableName.test(itr, tableName));
    };

    private static BiFunction<TypeElement, String, Map<String, MappingDescriptor>> fieldMappings = (TypeElement entityClassElement, String tableName) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotationsByType(Mappings.class)))
                    .filter(it -> it.getAnnotationsByType(Mappings.class).length != 0)
                    .filter(it -> containsTableNameInMappings.apply(tableName, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOfMappings.apply(iter, tableName)));

    private static Consumer<List<String>> validateDuplicateTableNames = (List<String> tableNames) -> {
        Set<String> uniqueTablesNames = tableNames.stream().collect(Collectors.toSet());
        if (uniqueTablesNames.size() != tableNames.size()) {
            throw new IllegalArgumentException("tables contains duplicated table names");
        }
    };


    private static BiFunction<Element, Class<? extends Annotation>, List<String>> tableNames = (Element classAnnotated, Class<? extends Annotation> annotation) -> {
        Optional<? extends AnnotationMirror> opAnnotation = classAnnotated.getAnnotationMirrors().stream().filter(it -> it.getAnnotationType().toString().equals(annotation.getName())).findFirst();

        opAnnotation.orElseThrow(() -> new IllegalArgumentException(String.format("%s not present.", annotation.getName())));

        return ((List<String>) opAnnotation.get().getElementValues().values().stream()
                .flatMap(it -> Lists.newArrayList(it.getValue().toString().replace("\"", "").split(",")).stream())
                .map(it -> it.trim())
                .collect(toList()));
    };

    private static BiFunction<String, Element, Boolean> containsTableNameInMapping = (String tableName, Element element) -> {
        List<String> tableNames = CreatorsFunctions.tableNames.apply(element, Mapping.class);
        validateDuplicateTableNames.accept(tableNames);
        validateBlankTableNames.accept(tableNames, "Table in Mapping annotation");
        return tableNames.stream().anyMatch(itr -> itr.equals(tableName));
    };

    private static Function<Element, MappingDescriptor> valueOf = (Element it) -> {
        Mapping annotation = it.getAnnotation(Mapping.class);
        return new MappingDescriptor(annotation.table(), annotation.field(), annotation.pk());
    };

    private static BiFunction<TypeElement, String, Map<String, MappingDescriptor>> fieldMapping = (TypeElement entityClassElement, String tableName) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotation(Mapping.class)))
                    .filter(it -> containsTableNameInMapping.apply(tableName, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOf.apply(iter)));

    private static Function<TypeElement, List<String>> getTablesNames = (classAnnotated) -> {
        List<String> tables = tableNames.apply(classAnnotated, CassitoryEntity.class);
        validateBlankTableNames.accept(tables, "Tables in CassitoryEntity");
        return tables;
    };

    private static Function<TypeElement, List<TableMappingDescriptor>> buildTableMappingDescriptors = (classAnnotated) -> {
        List<String> tableNames = getTablesNames.apply(classAnnotated);
        return tableNames.stream().map(tableName -> {
            Map<String, MappingDescriptor> result = fieldMappings.apply(classAnnotated, tableName);
            result.putAll(fieldMapping.apply(classAnnotated, tableName));

            return new TableMappingDescriptor(tableName, result);
        }).collect(Collectors.toList());
    };


    private static Consumer<List<TableMappingDescriptor>> validatePkPerTable = (List<TableMappingDescriptor> tableMappingDescriptors) ->
            tableMappingDescriptors.stream().forEach(tableMappingDescriptor -> {
                List<Map.Entry<String, MappingDescriptor>> pkFields = tableMappingDescriptor.getMappingDescriptors().entrySet().stream().filter(it -> it.getValue().isPk()).collect(toList());

                if (pkFields.size() == 0) {
                    throw new IllegalArgumentException("Every table must have pk attribute set to true");
                }
            });

    public static Function<TypeElement, List<TableMappingDescriptor>> getTableMappingDescriptors = (classAnnotated) -> {
        List<TableMappingDescriptor> tableMappingDescriptors = buildTableMappingDescriptors.apply(classAnnotated);
        validatePkPerTable.accept(tableMappingDescriptors);
        return tableMappingDescriptors;
    };

    public static Predicate<Map.Entry<String, MappingDescriptor>> byPkIfIsNeeded(boolean filterByPk) {
        return iter -> {
            if (filterByPk) {
                return iter.getValue().isPk();
            }

            return true;
        };
    }



}
