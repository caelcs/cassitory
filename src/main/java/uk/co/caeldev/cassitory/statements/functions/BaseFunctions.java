package uk.co.caeldev.cassitory.statements.functions;

import org.apache.commons.lang3.StringUtils;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;
import uk.co.caeldev.cassitory.statements.generators.MappingDescriptor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static uk.co.caeldev.cassitory.base.BaseFunctions.className;

public class BaseFunctions {

    public static final Function<TypeElement, String> parameterCreatorClassName = (element) -> String.format("%sParametersCreator", className.apply(element).toString());

    public static final Function<TypeElement, String> queryCreatorClassName = (element) -> String.format("%sQueriesCreator", className.apply(element).toString());

    public static final Function<TypeElement, String> repositoryClassName = (element) -> String.format("%sBaseStatementRepository", className.apply(element).toString());

    public static final BiFunction<TypeElement, Elements, String> destinationPackage = (TypeElement classAnnotated, Elements elements) ->
            classAnnotated.getAnnotation(CassitoryEntity.class).destinationPackage().isEmpty()?
                    elements.getPackageOf(classAnnotated).getQualifiedName().toString():
                    classAnnotated.getAnnotation(CassitoryEntity.class).destinationPackage();

    public static Function<String, String> valueBetweenParenthesis = source -> StringUtils.substringBetween(source, "(", ")");

    public static Function<String, MappingDescriptor> getField() {
        return itr -> {

            String annotationValues = valueBetweenParenthesis.apply(itr);
            List<String> values = newArrayList(StringUtils.split(annotationValues, ","));

            MappingDescriptor mappingDescriptor = new MappingDescriptor();
            values.stream()
                    .forEach(pair -> {
                        String pairNoSpacesAtEnd = pair.trim();
                        String[] pairsplitted = pairNoSpacesAtEnd.split("=");
                        String fieldName = pairsplitted[0].trim();
                        String fieldValue = StringUtils.replace(pairsplitted[1], "\"", "");

                        switch (fieldName) {
                            case "table":
                                mappingDescriptor.setTable(fieldValue);
                            case "field":
                                mappingDescriptor.setField(fieldValue);
                            case "pk":
                                mappingDescriptor.setPk(Boolean.valueOf(fieldValue));
                        }
                    });
            return mappingDescriptor;
        };
    }
}
