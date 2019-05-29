package uk.co.caeldev.cassitory.generators;

import com.datastax.driver.mapping.MappingManager;
import com.squareup.javapoet.MethodSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.function.Function;

import static uk.co.caeldev.cassitory.CommonFunctions.className;

public class RepositoryFunctions {

    public static final Function<TypeElement, String> repositoryClassName = (element) -> String.format("%sBaseRepository", className.apply(element).toString());

    public static final Function<TypeElement, MethodSpec> constructor = (element) -> MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(MappingManager.class, "mappingManager")
            .addStatement("super(mappingManager)")
            .build();
}
