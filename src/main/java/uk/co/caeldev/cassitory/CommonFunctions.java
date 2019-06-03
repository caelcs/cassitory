package uk.co.caeldev.cassitory;

import com.squareup.javapoet.ClassName;
import org.apache.commons.text.WordUtils;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class CommonFunctions {

    public static final Function<TypeElement, Name> className = TypeElement::getSimpleName;
    public static final Function<TypeElement, String> fieldName = (element) -> WordUtils.uncapitalize(className.apply(element).toString());
    public static final Function<ClassName, String> fieldNameClassName = (element) -> WordUtils.uncapitalize(element.simpleName());
    public static final Function<String, String> fullClassName = (classname) -> String.format("%s.class", classname);
    public static final BiFunction<TypeElement, Elements, String> destinationPackage = (TypeElement classAnnotated, Elements elements) ->
            classAnnotated.getAnnotation(CassitoryEntity.class).destinationPackage().isEmpty()?
                    elements.getPackageOf(classAnnotated).getQualifiedName().toString():
                    classAnnotated.getAnnotation(CassitoryEntity.class).destinationPackage();

}
