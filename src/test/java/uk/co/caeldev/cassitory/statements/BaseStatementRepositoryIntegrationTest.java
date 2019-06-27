package uk.co.caeldev.cassitory.statements;

import com.datastax.driver.core.*;
import org.apache.thrift.transport.TTransportException;
import org.awaitility.Duration;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.*;
import uk.co.caeldev.cassitory.pojos.UserDtoStatement;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static uk.org.fyodor.generators.RDG.integer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseStatementRepositoryIntegrationTest {

    public static final String KEYSPACE_NAME = "security";
    private Session session;

    @BeforeAll
    public void setUp() throws IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(EmbeddedCassandraServerHelper.CASSANDRA_RNDPORT_YML_FILE, 1000000L);

        Cluster cluster = new Cluster.Builder().addContactPoints(EmbeddedCassandraServerHelper.getHost())
                .withPort(EmbeddedCassandraServerHelper.getNativeTransportPort())
                .build();

        session = cluster.connect();

        CQLDataLoader dataLoader = new CQLDataLoader(session);
        dataLoader.load(new ClassPathCQLDataSet("cql/create-tables.cql", true, KEYSPACE_NAME));
    }

    @AfterAll
    public void tearDown() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Nested
    @DisplayName("Sync execution of operations")
    class SyncExecutionTests {

        @Test
        @DisplayName("Should save all the data into the tables")
        public void shouldSaveAllData() {
            //Given
            Integer age = 35;
            String fullName = "test";
            UserDtoStatement dtoEntity = new UserDtoStatement(fullName, age);

            //When
            UserDtoStatementBaseStatementRepository userDtoStatementBaseStatementRepository = new UserDtoStatementBaseStatementRepository(session);
            ResultSet resultSet = userDtoStatementBaseStatementRepository.save(dtoEntity);

            //Then
            assertThat(resultSet).isNotNull();
            assertThat(resultSet.wasApplied()).isTrue();

            //And
            ResultSet users = session.execute("select * from security.users where name = 'test';");
            ResultSet usersByName = session.execute("select * from security.usersByName where name = 'test' and age = 35;");

            List<Row> allUsers = users.all();
            List<Row> allUsersByName = usersByName.all();

            assertThat(allUsers).isNotEmpty();
            assertThat(allUsersByName).isNotEmpty();
            assertThat(allUsers).hasSize(1);
            assertThat(allUsersByName).hasSize(1);
        }

        @Test
        @DisplayName("Should delete the data in both tables")
        public void shouldDeleteDataFromBothTables() {
            //Given
            session.execute(String.format("INSERT INTO %s.users (name) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2');", KEYSPACE_NAME));
            session.execute(String.format("INSERT INTO %s.usersByName (name, age) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2', 25);", KEYSPACE_NAME));

            Integer age = 25;
            String fullName = "5b6962dd-3f90-4c93-8f61-eabfa4a803e2";
            UserDtoStatement dtoEntity = new UserDtoStatement(fullName, age);

            //When
            UserDtoStatementBaseStatementRepository userDtoStatementBaseStatementRepository = new UserDtoStatementBaseStatementRepository(session);
            ResultSet resultSet = userDtoStatementBaseStatementRepository.delete(dtoEntity);

            //Then
            assertThat(resultSet).isNotNull();
            assertThat(resultSet.wasApplied()).isTrue();

            //And
            ResultSet users = session.execute("select * from security.users where name = '5b6962dd-3f90-4c93-8f61-eabfa4a803e2';");
            ResultSet usersByName = session.execute("select * from security.usersByName where name = '5b6962dd-3f90-4c93-8f61-eabfa4a803e2' AND age = 25;");

            List<Row> allUsers = users.all();
            List<Row> allUsersByName = usersByName.all();

            assertThat(allUsers).isEmpty();
            assertThat(allUsersByName).isEmpty();
        }

        @Test
        @DisplayName("should rollback both operations when one operation fail.")
        public void shouldRollbackBothOperations() {
            //Given
            int expectedAge = integer().next();
            String expectedName = "asd.-''sdsdsddfsdfsasd";

            //And
            UserDtoStatement userDto = new UserDtoStatement(expectedName, expectedAge);

            //When
            UserDtoStatementBaseStatementRepository userDtoRepository = new UserDtoStatementBaseStatementRepository(session);
            ResultSetFuture resultSetFuture = userDtoRepository.saveAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = session.execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();

            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = session.execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();

            //And
            assertThat(resultSetFuture).isDone();
        }
    }

    @Nested
    @DisplayName("Async execution of transaction tests")
    class AsyncExecutionTests {

        @Test
        @DisplayName("should persist all to both tables")
        public void shouldPersistToBothTables() {
            //Given
            int expectedAge = integer().next();
            String expectedName = UUID.randomUUID().toString();

            //And
            UserDtoStatement userDto = new UserDtoStatement(expectedName, expectedAge);

            //When
            UserDtoStatementBaseStatementRepository userDtoRepository = new UserDtoStatementBaseStatementRepository(session);
            ResultSetFuture resultSetFuture = userDtoRepository.saveAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = session.execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).hasSize(1);

            //And
            String name = result.get(0).get("name", String.class);
            assertThat(name).isEqualTo(userDto.getFullName());

            //And
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = session.execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).hasSize(1);

            //And
            String name2 = resultsByName.get(0).get("name", String.class);
            Integer age = resultsByName.get(0).get("age", Integer.class);
            assertThat(name2).isEqualTo(userDto.getFullName());
            assertThat(age).isEqualTo(userDto.getAge());

            //And
            assertThat(resultSetFuture).isDone();
        }

        @Test
        @DisplayName("should delete all data from both tables")
        public void shouldDeleteFromBothTables() {
            //Given
            session.execute(String.format("INSERT INTO %s.users (name) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2');", KEYSPACE_NAME));
            session.execute(String.format("INSERT INTO %s.usersByName (name, age) VALUES ('5b6962dd-3f90-4c93-8f61-eabfa4a803e2', 25);", KEYSPACE_NAME));

            //And
            int expectedAge = 21;
            String expectedName = "5b6962dd-3f91-qc93-8f61-eabfa4a803e2";

            //And
            UserDtoStatement userDto = new UserDtoStatement(expectedName, expectedAge);

            //When
            UserDtoStatementBaseStatementRepository userDtoRepository = new UserDtoStatementBaseStatementRepository(session);
            ResultSetFuture resultSetFuture = userDtoRepository.deleteAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = session.execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();

            //And
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = session.execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();

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
            UserDtoStatement userDto = new UserDtoStatement(expectedName, expectedAge);

            //When
            UserDtoStatementBaseStatementRepository userDtoRepository = new UserDtoStatementBaseStatementRepository(session);
            ResultSetFuture resultSetFuture = userDtoRepository.saveAsync(userDto);
            await().atMost(Duration.TEN_SECONDS).until(resultSetFuture::isDone);

            //Then
            Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName where name = '%s' and age = %s", KEYSPACE_NAME, expectedName, expectedAge));
            ResultSet usersByNameRows = session.execute(usersByNameQueryStatement);
            List<Row> resultsByName = usersByNameRows.all();
            assertThat(resultsByName).isEmpty();

            Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users where name = '%s'", KEYSPACE_NAME, expectedName));
            ResultSet usersRows = session.execute(usersQueryStatement);
            List<Row> result = usersRows.all();
            assertThat(result).isEmpty();

            //And
            assertThat(resultSetFuture).isDone();
        }
    }
}