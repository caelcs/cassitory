package uk.co.caeldev.cassitory;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


class CassitoryEntityProcessorTest {


    @Test
    @DisplayName("Should create a creator class when there is an annotated class")
    public void shouldGenerateCreatorClass() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/Foo.java"));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("entities.FooCreators")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("entities/FooCreators.java"));
    }


}