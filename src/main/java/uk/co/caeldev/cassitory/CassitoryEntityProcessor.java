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

        createClasses(elements);

        return false;
    }

    private void createClasses(Map<String, String> elements) {
        elements.entrySet().stream().forEach(element -> {
                    TypeSpec.Builder typeSpec = TypeSpec
                            .classBuilder(element.getKey()+"Creators")
                            .addModifiers(Modifier.PUBLIC);
            try {
                JavaFile.builder(element.getValue(), typeSpec.build()).build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
