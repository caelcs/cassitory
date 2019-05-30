package uk.co.caeldev.cassitory.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import uk.co.caeldev.cassitory.Mapping;

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
import static uk.co.caeldev.cassitory.CommonFunctions.*;

public final class CreatorFunctions {

    public static final Function<TypeElement, String> creatorClassName = (element) -> String.format("%sCreators", className.apply(element).toString());
    public static final Function<ClassName, String> creatorFieldNameClassName = (element) -> String.format("%sCreator", fieldNameClassName.apply(element));
    public static final Function<ClassName, String> createTargetEntityMethodName = (element) -> String.format("create%s", element.simpleName());

    public static final Function<TypeElement, FieldSpec> entityField = (element) -> FieldSpec.builder(ClassName.get(element), fieldName.apply(element))
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    public static final Function<TypeElement, MethodSpec> constructor = (element) -> MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(element), fieldName.apply(element))
            .addStatement("this.$N = $N", fieldName.apply(element), fieldName.apply(element))
            .build();

    public static BiFunction<Element, Class<? extends Annotation>, List<String>> targetClasses = (Element classAnnotated, Class<? extends Annotation> annotation) -> {
        Optional<? extends AnnotationMirror> opAnnotation = classAnnotated.getAnnotationMirrors().stream().filter(it -> it.getAnnotationType().toString().equals(annotation.getName())).findFirst();

        opAnnotation.orElseThrow(() -> new IllegalArgumentException(String.format("%s not present.", annotation.getName())));

        return ((List<?>)opAnnotation.get().getElementValues().values().stream().findFirst().get().getValue())
                .stream().map(it -> it.toString().split(".class")[0]).collect(toList());
    };

    public static Function<Element, String> valueOf = (Element it) -> it.getAnnotation(Mapping.class).field();

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

    public static BiFunction<ClassName, Element, Boolean> containsTargetEntityClass = (ClassName targetCassandraEntity, Element element) -> {
        List<String> targets = targetClasses.apply(element, Mapping.class);
        validateDuplicateTargetClasses.accept(targets);
        return targets.stream().anyMatch(itr -> itr.equals(targetCassandraEntity.toString()));
    };

    public static BiFunction<TypeElement, ClassName, Map<String, String>> fieldMapping = (TypeElement entityClassElement, ClassName targetCassandraEntity) ->
            entityClassElement.getEnclosedElements().stream()
                    .filter(it -> it.getKind() == ElementKind.FIELD)
                    .filter(it -> nonNull(it.getAnnotation(Mapping.class)))
                    .filter(it -> containsTargetEntityClass.apply(targetCassandraEntity, it))
                    .collect(Collectors.toMap((iter) -> iter.getSimpleName().toString(), (iter) -> valueOf.apply(iter)));

    public static Function<Map<String, String>, Map<String, String>> fieldValidation = (fieldMappings) -> {
        List<Map.Entry<String, String>> entries = fieldMappings.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()).collect(Collectors.toList());
        if (entries.size() != fieldMappings.entrySet().size()) {
            throw new IllegalArgumentException("Field cannot be empty.");
        }
        return fieldMappings;
    };
}