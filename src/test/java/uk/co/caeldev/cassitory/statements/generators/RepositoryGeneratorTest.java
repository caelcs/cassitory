package uk.co.caeldev.cassitory.statements.generators;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.caeldev.cassitory.statements.CassitoryEntityProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class RepositoryGeneratorTest {

    @Test
    @DisplayName("should generate a repository for a given entity mapped to one table")
    public void shouldGenerateRepositoryForGivenEntity() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("statements/UserDto.java"));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("statements.UserDtoBaseStatementRepository")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDtoBaseStatementRepository.java"));
    }

}