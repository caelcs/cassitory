package uk.co.caeldev.cassitory.base;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

public abstract class AbstractCassitoryProcessor extends AbstractProcessor {

    protected Filer filer;
    protected Messager messager;
    protected Elements elements;

    protected List<Generator> generators;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
        generators = getGenerators(messager, elements);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        List<TypeElement> elements = BaseFunctions.findElements.apply(roundEnv, getAnnotation());

        try {
            generators.stream().flatMap(it -> it.generate(elements).stream())
                    .forEach(it -> BaseFunctions.saveClassGenerated.accept(it, filer));
        } catch (IllegalStateException ex) {
            return false;
        }

        return true;
    }

    public abstract Class<? extends Annotation> getAnnotation();

    public abstract List<Generator> getGenerators(Messager messager, Elements elements);
}
