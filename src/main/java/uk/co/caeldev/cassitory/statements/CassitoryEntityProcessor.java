package uk.co.caeldev.cassitory.statements;

import com.google.common.collect.Lists;
import uk.co.caeldev.cassitory.base.AbstractCassitoryProcessor;
import uk.co.caeldev.cassitory.base.Generator;
import uk.co.caeldev.cassitory.statements.generators.ParameterCreatorsGenerator;
import uk.co.caeldev.cassitory.statements.generators.QueryCreatorsGenerator;
import uk.co.caeldev.cassitory.statements.generators.RepositoryGenerator;

import javax.annotation.processing.Messager;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.List;

@SupportedAnnotationTypes("uk.co.caeldev.cassitory.statements.CassitoryEntity")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CassitoryEntityProcessor extends AbstractCassitoryProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return CassitoryEntity.class;
    }

    @Override
    public List<Generator> getGenerators(Messager messager, Elements elements) {
        return Lists.newArrayList(new ParameterCreatorsGenerator(messager, elements),
                new QueryCreatorsGenerator(messager, elements),
                new RepositoryGenerator(elements));
    }

}
