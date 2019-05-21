package uk.co.caeldev.cassitory;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("uk.co.caeldev.cassitory.CassitoryEntity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CassitoryEntityProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, String> elements = findElements(roundEnv);

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

    private List<JavaFile> createClasses(Map<String, String> elements) {
        return elements.entrySet().stream().map(element -> {
            TypeSpec.Builder typeSpec = TypeSpec
                    .classBuilder(String.format("%sCreators", element.getKey()))
                    .addModifiers(Modifier.PUBLIC);
            return JavaFile.builder(element.getValue(), typeSpec.build()).build();
        }).collect(Collectors.toList());
    }

    private Map<String, String> findElements(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(CassitoryEntity.class).stream()
                .peek(element -> {
                    if (element.getKind() != ElementKind.CLASS) {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.");
                    }
                })
                .map(element -> (TypeElement) element)
                .collect(Collectors.toMap(type -> type.getSimpleName().toString(), type -> elements.getPackageOf(type).getQualifiedName().toString()));
    }

}
