package uk.co.caeldev.cassitory.entities.repositories;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.MappingManager;
import org.apache.thrift.transport.TTransportException;
import org.awaitility.Duration;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.*;
import uk.co.caeldev.cassitory.pojos.UserDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static uk.org.fyodor.generators.RDG.integer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseRepositoryIntegrationTest {

    public static final String KEYSPACE_NAME = "security2";
    private MappingManager mappingManager;

    @BeforeAll
    public void setUp() throws IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE, 1000000L);

        Cluster cluster = new Cluster.Builder().addContactPoints(EmbeddedCassandraServerHelper.getHost())
                .withPort(EmbeddedCassandraServerHelper.getNativeTransportPort())
                .build();

        Session session = cluster.connect();

        CQLDataLoader dataLoader = new CQLDataLoader(session);
        dataLoader.load(new ClassPathCQLDataSet("cql/create-tables-2.cql", true, KEYSPACE_NAME));

        this.mappingManager = new MappingManager(session);
    }

    @AfterAll
    public void tearDown() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Nested
    @DisplayName("Sync execution of transaction tests")
    class SyncExecutionTests {
        @Test
        @DisplayName("should persist DTO to both tables")
        public void shouldPersistToBothTables() {
            //Given
            int expectedAge = integer().next();
            String expectedName = UUID.randomUUID().toString();

            //And
            UserDto userDto = new UserDto();
            userDto.setAge(expectedAge);
            userDto.setName(expectedName);

            //When
            UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
            userDtoRepository.save(userDto);

            //Then
            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).hasSize(1);

            //And
            String name = result.get(0).get("name", String.class);
            assertThat(name).isEqualTo(userDto.getName());

            //And
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).hasSize(1);

            //And
            String name2 = resultsByName.get(0).get("name", String.class);
            Integer age = resultsByName.get(0).get("age", Integer.class);
            assertThat(name2).isEqualTo(userDto.getName());
            assertThat(age).isEqualTo(userDto.getAge());
        }

        @Test
        @DisplayName("should rollback both operations when one operation fail.")
        public void shouldRollbackBothOperations() {
            //Given
            int expectedAge = integer().next();
            String expectedName = "asdasd.-''sasd";

            //And
            UserDto userDto = new UserDto();
            userDto.setAge(expectedAge);
            userDto.setName(expectedName);

            //When
            UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
            userDtoRepository.save(userDto);

            //Then
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();

            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should delete DTO from both tables")
        public void shouldDeleteFromBothTables() {
            //Given
            mappingManager.getSession().execute(String.format("INSERT INTO %s.users (name) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2');", KEYSPACE_NAME));
            mappingManager.getSession().execute(String.format("INSERT INTO %s.usersByName (name, age) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2', 25);", KEYSPACE_NAME));

            //And
            int expectedAge = 25;
            String expectedName = "5b6962dd-3f90-4c93-8f61-eabfa4a803e2";

            //And
            UserDto userDto = new UserDto();
            userDto.setAge(expectedAge);
            userDto.setName(expectedName);

            //When
            UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
            userDtoRepository.delete(userDto);

            //Then
            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();

            //And
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();
        }
    }

    @Nested
    @DisplayName("ASync execution of transaction tests")
    class AsyncExecutionTests {

        @Test
        @DisplayName("should persist DTO to both tables")
        public void shouldPersistToBothTables() {
            //Given
            int expectedAge = integer().next();
            String expectedName = UUID.randomUUID().toString();

            //And
            UserDto userDto = new UserDto();
            userDto.setAge(expectedAge);
            userDto.setName(expectedName);

            //When
            UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
            ResultSetFuture resultSetFuture = userDtoRepository.saveAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).hasSize(1);

            //And
            String name = result.get(0).get("name", String.class);
            assertThat(name).isEqualTo(userDto.getName());

            //And
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).hasSize(1);

            //And
            String name2 = resultsByName.get(0).get("name", String.class);
            Integer age = resultsByName.get(0).get("age", Integer.class);
            assertThat(name2).isEqualTo(userDto.getName());
            assertThat(age).isEqualTo(userDto.getAge());

            //And
            assertThat(resultSetFuture).isDone();
        }

        @Test
        @DisplayName("should rollback both operations when one operation fail.")
        public void shouldRollbackBothOperations() {
            //Given
            int expectedAge = integer().next();
            String expectedName = "asd.-''sdsdsddfsdfsasd";

            //And
            UserDto userDto = new UserDto();
            userDto.setAge(expectedAge);
            userDto.setName(expectedName);

            //When
            UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
            ResultSetFuture resultSetFuture = userDtoRepository.saveAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();

            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();

            //And
            assertThat(resultSetFuture).isDone();
        }

        @Test
        @DisplayName("should delete DTO from both tables")
        public void shouldDeleteFromBothTables() {
            //Given
            mappingManager.getSession().execute(String.format("INSERT INTO %s.users (name) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2');", KEYSPACE_NAME));
            mappingManager.getSession().execute(String.format("INSERT INTO %s.usersByName (name, age) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2', 25);", KEYSPACE_NAME));

            //And
            int expectedAge = 21;
            String expectedName = "5b6962dd-3f91-qc93-8f61-eabfa4a803e2";

            //And
            UserDto userDto = new UserDto();
            userDto.setAge(expectedAge);
            userDto.setName(expectedName);

            //When
            UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
            ResultSetFuture resultSetFuture = userDtoRepository.deleteAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();

            //And
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();

            //And
            assertThat(resultSetFuture).isDone();
        }

    }
}