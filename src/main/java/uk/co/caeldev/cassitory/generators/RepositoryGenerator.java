package uk.co.caeldev.cassitory.generators;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.mapping.Mapper.Option;
import com.squareup.javapoet.*;
import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.repository.BaseRepository;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static uk.co.caeldev.cassitory.CommonFunctions.destinationPackage;
import static uk.co.caeldev.cassitory.CommonFunctions.fieldName;
import static uk.co.caeldev.cassitory.generators.CreatorFunctions.creatorClassName;
import static uk.co.caeldev.cassitory.generators.RepositoryFunctions.constructor;
import static uk.co.caeldev.cassitory.generators.RepositoryFunctions.repositoryClassName;

public class RepositoryGenerator implements Generator {

    private final Elements elements;

    public RepositoryGenerator(Elements elements) {
        this.elements = elements;
    }

    @Override
    public List<JavaFile> generate(List<TypeElement> elementsAnnotated) {
        return elementsAnnotated.stream().map(classAnnotated -> {
            ParameterizedTypeName baseRepositoryClass =
                    ParameterizedTypeName.get(ClassName.get(BaseRepository.class), ClassName.get(classAnnotated));

            MethodSpec getCreatorsMethod = buildGetCreatorMethod(classAnnotated);

            MethodSpec getTargetClassesMethod = buildGetTargetClassesMethod(classAnnotated);

            MethodSpec getWriteOptionsMethod = buildGetWriteOptionsMethod(classAnnotated);

            TypeSpec.Builder type = TypeSpec
                    .classBuilder(repositoryClassName.apply(classAnnotated))
                    .superclass(baseRepositoryClass)
                    .addMethod(constructor.apply(classAnnotated))
                    .addMethod(getCreatorsMethod)
                    .addMethod(getTargetClassesMethod)
                    .addMethod(getWriteOptionsMethod)
                    .addModifiers(Modifier.PUBLIC);

            return JavaFile.builder(destinationPackage.apply(classAnnotated, this.elements), type.build())
                    .addStaticImport(ClassName.get("com.google.common.collect", "Lists"), "newArrayList")
                    .build();
        }).collect(toList());
    }

    private MethodSpec buildGetWriteOptionsMethod(TypeElement classAnnotated) {
        ArrayTypeName returnType = ArrayTypeName.of(Option.class);
        ParameterizedTypeName typeList =
                ParameterizedTypeName.get(List.class, Option.class);

        CassitoryEntity annotation = classAnnotated.getAnnotation(CassitoryEntity.class);
        ConsistencyLevel consistencyLevel = annotation.consistencyLevel();
        boolean tracing = annotation.tracing();

        return MethodSpec.methodBuilder("getWriteOptions")
                .addModifiers(Modifier.PROTECTED)
                .addAnnotation(Override.class)
                .returns(returnType)
                .addStatement("$T options = newArrayList($T.consistencyLevel($T.$L), $T.tracing($L))", typeList, Option.class, ConsistencyLevel.class, consistencyLevel.name(), Option.class, tracing)
                .addStatement("return options.stream().toArray($T[]::new)", Option.class)
                .build();
    }

    private MethodSpec buildGetTargetClassesMethod(TypeElement classAnnotated) {
        ParameterizedTypeName listOfTargetClasses =
                ParameterizedTypeName.get(List.class, Class.class);
        List<String> targetClasses = CreatorFunctions.targetClasses.apply(classAnnotated, CassitoryEntity.class);
        String classesArgumentTemplate = Collections.nCopies(targetClasses.size(), "$T.class").stream()
                .collect(Collectors.joining(", "));

        return MethodSpec.methodBuilder("getTargetClasses")
                .addModifiers(Modifier.PROTECTED)
                .returns(listOfTargetClasses)
                .addAnnotation(Override.class)
                .addStatement(String.format("return newArrayList(%s)", classesArgumentTemplate), getTargetClassesArgument(targetClasses))
                .build();
    }

    private Object[] getTargetClassesArgument(List<String> targetClasses) {
        return targetClasses.stream().map(ClassName::bestGuess).toArray();
    }

    private MethodSpec buildGetCreatorMethod(TypeElement classAnnotated) {
        ParameterizedTypeName listOfSupplierType =
                ParameterizedTypeName.get(List.class, Supplier.class);
        return MethodSpec.methodBuilder("getCreators")
                .addModifiers(Modifier.PROTECTED)
                .returns(listOfSupplierType)
                .addAnnotation(Override.class)
                .addParameter(ClassName.get(classAnnotated), fieldName.apply(classAnnotated))
                .addStatement("return new $T($N).creators", ClassName.get(destinationPackage.apply(classAnnotated, this.elements), creatorClassName.apply(classAnnotated)), fieldName.apply(classAnnotated))
                .build();
    }
}
