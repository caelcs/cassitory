package uk.co.caeldev.cassitory.statements.generators;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.caeldev.cassitory.statements.CassitoryEntityProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class QueryCreatorsGeneratorTest {

    @Nested
    @DisplayName("Query Generator")
    class QueryGeneratorSaveTest {

        @Test
        @DisplayName("Should generate the query using one parameter for one table")
        public void shouldGenerateQueries() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDtoQueriesCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDtoQueriesCreator.java"));
        }

        @Test
        @DisplayName("Should generate the query one parameter for multiple table")
        public void shouldGenerateQueries2() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto2.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto2QueriesCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto2QueriesCreator.java"));
        }

        @Test
        @DisplayName("Should generate the query multiple parameters for one table")
        public void shouldGenerateQueries5() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto3.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto3QueriesCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto3QueriesCreator.java"));
        }

        @Test
        @DisplayName("Should generate the query using all the parameters for multiple table")
        public void shouldGenerateQueries3() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto4.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto4QueriesCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto4QueriesCreator.java"));
        }

        @Test
        @DisplayName("Should generate the query using all the parameters for tables declared at CassitoryEntity annotation")
        public void shouldGenerateQueries4() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto5.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto5QueriesCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto5QueriesCreator.java"));
        }
    }
}