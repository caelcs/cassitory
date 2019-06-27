package uk.co.caeldev.cassitory.entities.functions;

import uk.co.caeldev.cassitory.entities.CassitoryEntity;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.function.BiFunction;

public class BaseFunctions {

    public static final BiFunction<TypeElement, Elements, String> destinationPackage = (TypeElement classAnnotated, Elements elements) ->
            classAnnotated.getAnnotation(CassitoryEntity.class).destinationPackage().isEmpty()?
                    elements.getPackageOf(classAnnotated).getQualifiedName().toString():
                    classAnnotated.getAnnotation(CassitoryEntity.class).destinationPackage();

}
