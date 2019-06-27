package uk.co.caeldev.cassitory.statements.generators;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.caeldev.cassitory.statements.CassitoryEntityProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

class ParameterCreatorsGeneratorTest {

    @Nested
    @DisplayName("Creator for save operation")
    class CreatorGeneratorSaveTest {

        @Test
        @DisplayName("Should generate a creator class with a constructor and one field with one table field mapping")
        public void shouldGenerateCreatorClassWithAnnotatedArguments() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDtoParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDtoParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and one field with one table field mapping and spaces surrounded")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndSpaceAtBeginnningAndEnd() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto6.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto6ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto6ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and one field with one table field mapping and spaces surrounded and no field")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndSpaceAtBeginningAndEndWithoutField() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto7.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto7ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto7ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and one field with two table field mapping and spaces surrounded and no field")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndSpaceAtBeginningAndEndWithoutField2() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto8.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto8ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto8ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and one field with two table field mapping and spaces surrounded and no field and several table with spaces surronded")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndSpaceAtBeginningAndEndWithoutField3() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto9.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto9ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto9ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and one field with multiple table field mappings")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndMultipleTableMapping() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto2.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto2ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto2ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and multiple fields with one field mapping each")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndMultipleFields() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto3.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto3ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto3ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor and multiple fields with multiple field mapping each")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndMultipleFields2() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto4.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto4ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto4ParametersCreator.java"));
        }

        @Test
        @DisplayName("Should generate a creator class with a constructor generating all for one table")
        public void shouldGenerateCreatorClassWithAnnotatedArgumentsAndMultipleFields3() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDto5.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDto5ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDto5ParametersCreator.java"));
        }

        @Nested
        @DisplayName("Validation scenarios")
        class ValidationTest {

            @Test
            @DisplayName("Validate that Mapping annotation table field is not blank")
            public void shouldNotBeEmptyMappingTableField() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoValidation.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Table in Mapping annotation cannot be null or empty");
            }

            @Test
            @DisplayName("Validate that Mapping annotation table field has only spaces")
            public void shouldNotBeEmptyMappingTableField2() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoValidation2.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Table in Mapping annotation cannot be null or empty");
            }

            @Test
            @DisplayName("Validate that CassitoryEntity annotation table field is not Blank")
            public void shouldNotBeEmptyCassitoryEntityTableField1() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoValidation3.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Tables in CassitoryEntity cannot be null or empty");
            }

            @Test
            @DisplayName("Validate that CassitoryEntity annotation table field does not contains empty spaces")
            public void shouldNotBeEmptyCassitoryEntityTableField2() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoValidation4.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Tables in CassitoryEntity cannot be null or empty");
            }

            @Test
            @DisplayName("Validate that CassitoryEntity annotation table field has no entries")
            public void shouldNotBeEmptyCassitoryEntityTableField3() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoValidation5.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Tables in CassitoryEntity cannot be null or empty");
            }

            @Test
            @DisplayName("Validate that Mapping annotation table field is not blank when there are multiple Mapping annotations")
            public void shouldNotBeEmptyCassitoryEntityTableField4() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoValidation6.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("table field cannot by null or empty");
            }

        }
    }

    @Nested
    @DisplayName("Creator for delete operation")
    class CreatorGeneratorDeleteTest {

        @Test
        @DisplayName("Should generate method with single the field marked as partition key")
        public void shouldGenerateMethodUsingFieldMarkedAsPartitionKey() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDtoDelete.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDtoDeleteParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDtoDeleteParametersCreator.java"));

        }

        @Test
        @DisplayName("Should generate method with multiple fields and some marked as partition key")
        public void shouldGenerateMethodUsingFieldsMarkedAsPartitionKey() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDtoDelete2.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDtoDelete2ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDtoDelete2ParametersCreator.java"));

        }

        @Test
        @DisplayName("Should generate method with multiple fields and multiple mappings and some marked as partition key")
        public void shouldGenerateMethodUsingFieldsMarkedAsPartitionKey2() {
            Compilation compilation =
                    javac()
                            .withProcessors(new CassitoryEntityProcessor())
                            .compile(JavaFileObjects.forResource("statements/UserDtoDelete3.java"));

            assertThat(compilation).succeeded();
            assertThat(compilation)
                    .generatedSourceFile("statements.UserDtoDelete3ParametersCreator")
                    .hasSourceEquivalentTo(JavaFileObjects.forResource("statements/UserDtoDelete3ParametersCreator.java"));

        }

        @Nested
        @DisplayName("Validations for delete parameters")
        class CreatorGeneratorDeleteValidationTest {

            @Test
            @DisplayName("Every table should have at least one pk field")
            public void shouldHaveAPkPerTable() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoDelete1Validation.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Every table must have pk attribute set to true");
            }

            @Test
            @DisplayName("Every table should have at least one pk field even when there are tables with pk")
            public void shouldHaveAPkPerTable2() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoDelete2Validation.java"));

                assertThat(compilation).failed();
                assertThat(compilation).hadErrorCount(2);
                assertThat(compilation).hadErrorContaining("Every table must have pk attribute set to true");
            }

            @Test
            @DisplayName("should validate PK is present for the annotated tables only")
            public void shouldHaveAPkPerTable3() {
                Compilation compilation =
                        javac()
                                .withProcessors(new CassitoryEntityProcessor())
                                .compile(JavaFileObjects.forResource("statements/UserDtoDelete3Validation.java"));

                assertThat(compilation).succeeded();
            }
        }
    }

}