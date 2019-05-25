package uk.co.caeldev.cassitory;

import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

final class CassitoryEntityFunctions {

    static final Function<TypeElement, Name> className = TypeElement::getSimpleName;
    static final Function<TypeElement, String> creatorClassName = (element) -> String.format("%sCreators", className.apply(element).toString());
    static final Function<TypeElement, String> fieldName = (element) -> WordUtils.uncapitalize(className.apply(element).toString());
    static final Function<ClassName, String> fieldNameClassName = (element) -> WordUtils.uncapitalize(element.simpleName());
    static final Function<ClassName, String> creatorFieldNameClassName = (element) -> String.format("%sCreator", fieldNameClassName.apply(element));
    static final Function<ClassName, String> createTargetEntityMethodName = (element) -> String.format("create%s", element.simpleName());


    static final Function<TypeElement, FieldSpec> entityField = (element) -> FieldSpec.builder(ClassName.get(element), fieldName.apply(element))
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    static final Function<TypeElement, MethodSpec> constructor = (element) -> MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(element), fieldName.apply(element))
            .addStatement("this.$N = $N", fieldName.apply(element), fieldName.apply(element))
            .build();

    static BiFunction<Element, Class<? extends Annotation>, List<String>> targetClasses = (Element classAnnotated, Class<? extends Annotation> annotation) -> {
        Optional<? extends AnnotationMirror> opAnnotation = classAnnotated.getAnnotationMirrors().stream().filter(it -> it.getAnnotationType().toString().equals(annotation.getName())).findFirst();
        if (opAnnotation.isPresent()) {
            return ((List<?>)opAnnotation.get().getElementValues().values().stream().findFirst().get().getValue())
                    .stream().map(it -> it.toString().split(".class")[0]).collect(toList());
        }
        return Lists.newArrayList();
    };

    static Function<Element, String> valueOf = (Element it) -> it.getAnnotation(Mapping.class).field();

    static Consumer<List<String>> validateMappingTarget = (List<String> targets) -> {
        Set<String> uniqueTargets = targets.stream().collect(Collectors.toSet());
        if (uniqueTargets.size() != targets.size()) {
            throw new IllegalArgumentException("Target contains duplicated classes");
        }
    };

    static BiFunction<ClassName, Element, Boolean> containsTargetEntityClass = (ClassName targetCassandraEntity, Element element) -> {
        List<String> targets = targetClasses.apply(element, Mapping.class);
        validateMappingTarget.accept(targets);
        return targets.stream().anyMatch(itr -> itr.equals(targetCassandraEntity.toString()));
    };

    static BiFunction<TypeElement, ClassName, Map<String, String>> fieldMapping = (TypeElement entityClassElement, ClassName targetCassandraEntity) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotation(Mapping.class)))
                    .filter(it -> containsTargetEntityClass.apply(targetCassandraEntity, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOf.apply(iter)));

    static Function<Map<String, String>, Map<String, String>> fieldValidation = (fieldMappings) -> {
        List<Map.Entry<String, String>> entries = fieldMappings.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).collect(Collectors.toList());
        if (entries.size() != fieldMappings.entrySet().size()) {
            throw new IllegalArgumentException("Field cannot be empty.");
        }
        return fieldMappings;
    };
}
