package uk.co.caeldev.cassitory.statements.generators;

import com.datastax.driver.core.Session;
import com.squareup.javapoet.*;
import uk.co.caeldev.cassitory.base.Generator;
import uk.co.caeldev.cassitory.statements.functions.BaseFunctions;
import uk.co.caeldev.cassitory.statements.repositories.BaseRepository;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static uk.co.caeldev.cassitory.base.BaseFunctions.fieldNameClassName;
import static uk.co.caeldev.cassitory.statements.functions.BaseFunctions.*;

public class RepositoryGenerator implements Generator {

    private Elements elements;

    public RepositoryGenerator(Elements elements) {
        this.elements = elements;
    }

    @Override
    public List<JavaFile> generate(List<TypeElement> elementsAnnotated) {
        return elementsAnnotated.stream().map(classAnnotated -> {

            ParameterizedTypeName baseRepositoryType =
                    ParameterizedTypeName.get(ClassName.get(BaseRepository.class),
                            ClassName.get(classAnnotated));

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addStatement("super(session)")
                    .addParameter(Session.class, "session")
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            TypeSpec.Builder type = TypeSpec
                    .classBuilder(BaseFunctions.repositoryClassName.apply(classAnnotated))
                    .superclass(baseRepositoryType)
                    .addMethod(constructor)
                    .addModifiers(Modifier.PUBLIC);

            addGetQueriesMethods(type, classAnnotated);
            addGetParametersMethods(type, classAnnotated);

            return JavaFile.builder(destinationPackage.apply(classAnnotated, this.elements), type.build()).build();
        }).collect(toList());
    }

    private void addGetParametersMethods(TypeSpec.Builder type, TypeElement classAnnotated) {
        ClassName parametersCreatorClassName = ClassName.bestGuess(destinationPackage.apply(classAnnotated, this.elements) + "." + parameterCreatorClassName.apply(classAnnotated));

        ParameterizedTypeName returnParametersType =
                ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ArrayTypeName.of(Object.class));

        ParameterSpec entityType = ParameterSpec.builder(ClassName.get(classAnnotated), "entity").build();

        MethodSpec getDeleteParametersMethod = MethodSpec.methodBuilder("getDeleteParameters")
                .addModifiers(Modifier.PROTECTED)
                .returns(returnParametersType)
                .addAnnotation(Override.class)
                .addParameter(entityType)
                .addStatement("$T $N = new $T($N)", parametersCreatorClassName, fieldNameClassName.apply(parametersCreatorClassName), parametersCreatorClassName, entityType)
                .addStatement("return $N.createDeleteParameters()", fieldNameClassName.apply(parametersCreatorClassName)).build();

        MethodSpec getSaveParametersMethod = MethodSpec.methodBuilder("getSaveParameters")
                .addModifiers(Modifier.PROTECTED)
                .returns(returnParametersType)
                .addAnnotation(Override.class)
                .addParameter(entityType)
                .addStatement("$T $N = new $T($N)", parametersCreatorClassName, fieldNameClassName.apply(parametersCreatorClassName), parametersCreatorClassName, entityType)
                .addStatement("return $N.createSaveParameters()", fieldNameClassName.apply(parametersCreatorClassName)).build();

        type.addMethod(getSaveParametersMethod);
        type.addMethod(getDeleteParametersMethod);
    }

    private void addGetQueriesMethods(TypeSpec.Builder type, TypeElement classAnnotated) {
        ClassName queriesCreatorClassName = ClassName.bestGuess(destinationPackage.apply(classAnnotated, this.elements) + "." + queryCreatorClassName.apply(classAnnotated));


        ParameterizedTypeName returnType =
                ParameterizedTypeName.get(Map.class, String.class, String.class);

        MethodSpec getSaveQueriesMethod = MethodSpec.methodBuilder("getSaveQueries")
                .addModifiers(Modifier.PROTECTED)
                .returns(returnType)
                .addAnnotation(Override.class)
                .addStatement("return $T.saveQueries", queriesCreatorClassName).build();

        MethodSpec getDeleteQueriesMethod = MethodSpec.methodBuilder("getDeleteQueries")
                .addModifiers(Modifier.PROTECTED)
                .returns(returnType)
                .addAnnotation(Override.class)
                .addStatement("return $T.deleteQueries", queriesCreatorClassName).build();

        type.addMethod(getSaveQueriesMethod);
        type.addMethod(getDeleteQueriesMethod);
    }

}
