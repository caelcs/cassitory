package uk.co.caeldev.cassitory;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.squareup.javapoet.JavaFile;
import uk.co.caeldev.cassitory.generators.CreatorGenerator;
import uk.co.caeldev.cassitory.generators.Generator;
import uk.co.caeldev.cassitory.generators.RepositoryGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@SupportedAnnotationTypes("uk.co.caeldev.cassitory.CassitoryEntity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class CassitoryEntityProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elements;

    private List<Generator> generators;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        generators = Lists.newArrayList(new CreatorGenerator(messager, elements),
                new RepositoryGenerator(messager, elements));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<TypeElement> elements = findElements(roundEnv);

        try {
            generators.stream().flatMap(it -> it.generate(elements).stream())
                    .forEach(saveClassGenerated());
        } catch (IllegalStateException ex) {
            return false;
        }

        return true;
    }

    private Consumer<JavaFile> saveClassGenerated() {
        return it -> {
            try {
                it.writeTo(filer);
            } catch (IOException e) {
                throw new IllegalStateException("Fail to generate class");
            }

        };
    }

    private List<TypeElement> findElements(RoundEnvironment roundEnv) {
        return roundEnv.getElementsAnnotatedWith(CassitoryEntity.class).stream()
                .map(element -> (TypeElement) element).collect(toList());
    }

}
