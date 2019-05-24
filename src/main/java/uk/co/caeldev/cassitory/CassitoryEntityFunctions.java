package uk.co.caeldev.cassitory;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

final class CassitoryEntityFunctions {

    static final Function<TypeElement, Name> className = TypeElement::getSimpleName;
    static final Function<TypeElement, String> creatorClassName = (element) -> String.format("%sCreators", className.apply(element).toString());
    static final Function<TypeElement, String> fieldName = (element) -> WordUtils.uncapitalize(className.apply(element).toString());
    static final Function<ClassName, String> fieldNameClassName = (element) -> WordUtils.uncapitalize(element.simpleName());
    static final Function<ClassName, String> creatorFieldNameClassName = (element) -> String.format("%sCreator", fieldNameClassName.apply(element));


    static final Function<TypeElement, FieldSpec> entityField = (element) -> FieldSpec.builder(ClassName.get(element), fieldName.apply(element))
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    static final Function<TypeElement, MethodSpec> constructor = (element) -> MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(element), fieldName.apply(element))
            .addStatement("this.$N = $N", fieldName.apply(element), fieldName.apply(element))
            .build();

    static Function<CassitoryEntity, List<? extends TypeMirror>> targetCassandraEntityFrom = (annotation) -> {
        try {
            annotation.target();
        } catch( MirroredTypesException mte ) {
            return mte.getTypeMirrors();
        }
        return null;
    };

    static Function<Mapping, List<? extends TypeMirror>> targetMappingFrom = (annotation) -> {
        try {
            annotation.target();
        } catch( MirroredTypesException mte ) {
            return mte.getTypeMirrors();
        }
        return null;
    };

    static Function<Element, String> valueOf = (Element it) -> it.getAnnotation(Mapping.class).field();

    static BiFunction<ClassName, Element, Boolean> containsTargetEntityClass = (ClassName targetCassandraEntity, Element it) -> targetMappingFrom.apply(it.getAnnotation(Mapping.class)).stream().anyMatch(itr -> itr.toString().equals(targetCassandraEntity.toString()));

    static BiFunction<TypeElement, ClassName, Map<String, String>> fieldMapping = (TypeElement entityClassElement, ClassName targetCassandraEntity) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotation(Mapping.class)))
                    .filter(it -> containsTargetEntityClass.apply(targetCassandraEntity, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOf.apply(iter)));
}
