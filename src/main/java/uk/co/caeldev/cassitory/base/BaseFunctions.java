package uk.co.caeldev.cassitory.base;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.text.WordUtils;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public final class BaseFunctions {

    public static final Function<TypeElement, Name> className = TypeElement::getSimpleName;
    public static final Function<TypeElement, String> fieldName = (element) -> WordUtils.uncapitalize(className.apply(element).toString());
    public static final Function<ClassName, String> fieldNameClassName = (element) -> WordUtils.uncapitalize(element.simpleName());
    public static final Function<String, String> fullClassName = (classname) -> String.format("%s.class", classname);
    public static final Function<String, String> capitaliseName = WordUtils::capitalize;
    public static final Function<String, String> unCapitaliseName = WordUtils::uncapitalize;


    public static final BiConsumer<JavaFile, Filer> saveClassGenerated = ((javaFile, filer) -> {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            throw new IllegalStateException("Fail to generate class");
        }
    });

    public static final BiFunction<RoundEnvironment, Class<? extends Annotation>, List<TypeElement>> findElements = (roundEnv, annotation) -> roundEnv.getElementsAnnotatedWith(annotation).stream()
                .map(element -> (TypeElement) element).collect(toList());

    public static final Function<TypeElement, FieldSpec> entityField = (element) -> FieldSpec.builder(ClassName.get(element), fieldName.apply(element))
            .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
            .build();
    public static final Function<TypeElement, MethodSpec> constructor = (element) -> MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(element), fieldName.apply(element))
            .addStatement("this.$N = $N", fieldName.apply(element), fieldName.apply(element))
            .build();

    public static Function<String, String> getField() {
        return itr -> {
            String[] splitByComma = itr.split(",");
            String[] fieldValueSplitted = splitByComma[splitByComma.length - 1].split("=");

            if (!fieldValueSplitted[0].trim().equals("field")) {
                return "";
            }

            return fieldValueSplitted[1].split("\"")[1];
        };
    }
}
