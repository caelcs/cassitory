package uk.co.caeldev.cassitory.entities.functions;

import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import uk.co.caeldev.cassitory.base.BaseFunctions;
import uk.co.caeldev.cassitory.entities.Mapping;
import uk.co.caeldev.cassitory.entities.Mappings;

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
import static uk.co.caeldev.cassitory.base.BaseFunctions.*;

public final class CreatorFunctions {

    public static final Function<TypeElement, String> creatorClassName = (element) -> String.format("%sCreators", className.apply(element).toString());
    public static final Function<ClassName, String> creatorFieldNameClassName = (element) -> String.format("%sCreator", fieldNameClassName.apply(element));
    public static final Function<ClassName, String> createTargetEntityMethodName = (element) -> String.format("create%s", element.simpleName());

    public static BiFunction<Element, Class<? extends Annotation>, List<String>> targetClasses = (Element classAnnotated, Class<? extends Annotation> annotation) -> {
        Optional<? extends AnnotationMirror> opAnnotation = classAnnotated.getAnnotationMirrors().stream().filter(it -> it.getAnnotationType().toString().equals(annotation.getName())).findFirst();

        opAnnotation.orElseThrow(() -> new IllegalArgumentException(String.format("%s not present.", annotation.getName())));

        return ((List<?>) opAnnotation.get().getElementValues().values().stream().findFirst().get().getValue())
                .stream().map(it -> it.toString().split(".class")[0]).collect(toList());
    };

    public static Function<Element, List<String>> targetClassesForMappings = (Element classAnnotated) -> {
        Optional<? extends AnnotationMirror> opAnnotation = classAnnotated.getAnnotationMirrors().stream()
                .filter(it -> it.getAnnotationType().toString().equals(ClassName.get(Mappings.class).toString()))
                .findFirst();

        if (!opAnnotation.isPresent()) {
            return Lists.newArrayList();
        }
        return ((List<?>) opAnnotation.get().getElementValues().values().stream().findFirst().get().getValue())
                .stream().map(it -> it.toString()).collect(toList());
    };

    public static Function<Element, String> valueOf = (Element it) -> it.getAnnotation(Mapping.class).field().isEmpty()? it.getSimpleName().toString(): it.getAnnotation(Mapping.class).field();

    public static BiFunction<Element, String, String> valueOfMappings = (Element it, String classAnnotated) -> {
        List<Object> annotationMirrors = it.getAnnotationMirrors().get(0).getElementValues().values().stream()
                .map(itr -> itr.getValue())
                .collect(toList());

        List<String> annotationMetadata = (List<String>) ((List) annotationMirrors.get(0)).stream()
                .map(itr -> itr.toString())
                .collect(toList());

        List<String> fieldsMapped = annotationMetadata.stream()
                .filter(itr -> itr.contains(fullClassName.apply(classAnnotated)))
                .map(getField()).collect(toList());

        if (fieldsMapped.size() > 1) {
            throw new IllegalArgumentException("Target contains duplicated classes");
        }


        String foundValue = fieldsMapped.get(0).trim();
        return foundValue.isEmpty()? it.getSimpleName().toString(): foundValue;
    };

    public static Consumer<List<String>> validateDuplicateTargetClasses = (List<String> targets) -> {
        Set<String> uniqueTargets = targets.stream().collect(Collectors.toSet());
        if (uniqueTargets.size() != targets.size()) {
            throw new IllegalArgumentException("Target contains duplicated classes");
        }
    };

    public static Consumer<List<String>> validateEmptyTargetClasses = (List<String> targets) -> {
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("Target cannot by empty");
        }
    };

    public static BiFunction<ClassName, Element, Boolean> containsTargetEntityClassInMapping = (ClassName targetCassandraEntity, Element element) -> {
        List<String> targets = targetClasses.apply(element, Mapping.class);
        validateDuplicateTargetClasses.accept(targets);
        return targets.stream().anyMatch(itr -> itr.equals(targetCassandraEntity.toString()));
    };

    public static BiFunction<ClassName, Element, Boolean> containsTargetEntityClassInMappings = (ClassName targetCassandraEntity, Element element) -> {
        List<String> targets = targetClassesForMappings.apply(element);
        validateDuplicateTargetClasses.accept(targets);
        return targets.stream().anyMatch(itr -> itr.contains(BaseFunctions.fullClassName.apply(targetCassandraEntity.toString())));
    };

    public static BiFunction<TypeElement, ClassName, Map<String, String>> fieldMapping = (TypeElement entityClassElement, ClassName targetCassandraEntity) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotation(Mapping.class)))
                    .filter(it -> containsTargetEntityClassInMapping.apply(targetCassandraEntity, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOf.apply(iter)));

    public static BiFunction<TypeElement, ClassName, Map<String, String>> fieldMappings = (TypeElement entityClassElement, ClassName targetCassandraEntity) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotationsByType(Mappings.class)))
                    .filter(it -> it.getAnnotationsByType(Mappings.class).length != 0)
                    .filter(it -> containsTargetEntityClassInMappings.apply(targetCassandraEntity, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOfMappings.apply(iter, targetCassandraEntity.simpleName())));
}
