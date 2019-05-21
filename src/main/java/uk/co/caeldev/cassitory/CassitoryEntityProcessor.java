package uk.co.caeldev.cassitory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import org.apache.commons.text.WordUtils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@SupportedAnnotationTypes("uk.co.caeldev.cassitory.CassitoryEntity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CassitoryEntityProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;

    private Function<TypeElement, Name> className = TypeElement::getSimpleName;
    private Function<TypeElement, String> creatorClassName = (element) -> String.format("%sCreators", className.apply(element).toString());
    private Function<TypeElement, String> fieldName = (element) -> WordUtils.uncapitalize(className.apply(element).toString());

    private Function<TypeElement, FieldSpec> entityField = (element) -> FieldSpec.builder(ClassName.get(element), fieldName.apply(element))
            .addModifiers(Modifier.PRIVATE)
            .addModifiers(Modifier.FINAL)
            .build();

    private Function<TypeElement, MethodSpec> constructor = (element) -> MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(ClassName.get(element), fieldName.apply(element))
            .addStatement("this.$N = $N", fieldName.apply(element), fieldName.apply(element))
            .build();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<TypeElement> elements = findElements(roundEnv);

        List<JavaFile> classes = createClasses(elements);

        for (JavaFile it: classes) {
            try {
                it.writeTo(filer);
            } catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    private List<JavaFile> createClasses(List<TypeElement> elements) {
        return elements.stream().map(element -> {
            TypeSpec.Builder type = TypeSpec
                    .classBuilder(creatorClassName.apply(element))
                    .addField(entityField.apply(element))
                    .addMethod(constructor.apply(element))
                    .addModifiers(Modifier.PUBLIC);

            return JavaFile.builder(this.elements.getPackageOf(element).getQualifiedName().toString(), type.build()).build();
        }).collect(toList());
    }

    private List<TypeElement> findElements(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(CassitoryEntity.class).stream()
                .peek(element -> {
                    if (element.getKind() != ElementKind.CLASS) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                    }
                })
                .map(element -> (TypeElement) element).collect(toList());
    }

}
