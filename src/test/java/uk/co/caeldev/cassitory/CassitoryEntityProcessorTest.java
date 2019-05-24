package uk.co.caeldev.cassitory;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


class CassitoryEntityProcessorTest {

    @Test
    @DisplayName("Should fail when CassitoryEntity annotation is at field level")
    public void shouldFailWhenCassitoryEntityAnnotatedAtFieldLevel() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/FooInvalid.java"));

        assertThat(compilation).hadErrorContaining("annotation type not applicable to this kind of declaration");
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    @DisplayName("Should fail when CassitoryEntity annotation is repeated")
    public void shouldFailWhenCassitoryEntityAnnotatedIsRepeated() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/FooInvalid4.java"));

        assertThat(compilation).hadErrorContaining("uk.co.caeldev.cassitory.CassitoryEntity is not a repeatable annotation type");
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    @DisplayName("Should fail when Mapping annotation is repeated")
    public void shouldFailWhenMappingAnnotatedIsRepeated() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/FooInvalid5.java"));

        assertThat(compilation).hadErrorContaining("uk.co.caeldev.cassitory.Mapping is not a repeatable annotation type");
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    @DisplayName("Should fail when Mapping annotation is at class level")
    public void shouldFailWhenMappingAnnotatedAtClassLevel() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/FooInvalid2.java"));

        assertThat(compilation).hadErrorContaining("annotation type not applicable to this kind of declaration");
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    @DisplayName("Should fail when Mapping annotation is at method level")
    public void shouldFailWhenMappingAnnotatedAtMethodLevel() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/FooInvalid3.java"));

        assertThat(compilation).hadErrorContaining("annotation type not applicable to this kind of declaration");
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
    }

    @Test
    @DisplayName("Should create a creator class with a constructor when there is an annotated class with arguments")
    public void shouldGenerateCreatorClassWithAnnotatedArguments() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/UserDto.java"));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("entities.UserDtoCreators")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("entities/UserDtoCreators.java"));
    }

    @Test
    @DisplayName("Should create a creator class with a constructor when there is an annotated class with more than one argument")
    public void shouldGenerateCreatorClassWithAnnotatedMoreArguments() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/UserMoreDto.java"));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("entities.UserMoreDtoCreators")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("entities/UserMoreDtoCreators.java"));
    }

    @Test
    @DisplayName("Should create a target instance using the only creator available")
    public void shouldCreateOneTargetEntityUsingOneCreator() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/UserDtoWithMap.java"));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("entities.UserDtoWithMapCreators")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("entities/UserDtoWithMapCreators.java"));
    }

    @Test
    @DisplayName("Should create a target instance using the two creators with mapping all fields to target entity")
    public void shouldCreateOneTargetEntityUsingTwoCreator() {
        Compilation compilation =
                javac()
                        .withProcessors(new CassitoryEntityProcessor())
                        .compile(JavaFileObjects.forResource("entities/UserMoreDtoMultipleMap.java"));

        assertThat(compilation).succeeded();
        assertThat(compilation)
                .generatedSourceFile("entities.UserMoreDtoMultipleMapCreators")
                .hasSourceEquivalentTo(JavaFileObjects.forResource("entities/UserMoreDtoMultipleMapCreators.java"));
    }
}