package uk.co.caeldev.cassitory.repository;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.MappingManager;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.caeldev.cassitory.pojos.UserDto;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.org.fyodor.generators.RDG.integer;
import static uk.org.fyodor.generators.RDG.string;

class BaseRepositoryIntegrationTest {

    public static final String KEYSPACE_NAME = "security";
    private MappingManager mappingManager;

    @BeforeEach
    public void setUp() throws IOException, TTransportException {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra(EmbeddedCassandraServerHelper.DEFAULT_CASSANDRA_YML_FILE, 1000000L);

        Cluster cluster = new Cluster.Builder().addContactPoints("127.0.0.1").withPort(9142).build();
        Session session = cluster.connect();

        CQLDataLoader dataLoader = new CQLDataLoader(session);
        dataLoader.load(new ClassPathCQLDataSet("cql/create-tables.cql", true, KEYSPACE_NAME));

        this.mappingManager = new MappingManager(session);
    }

    @AfterEach
    public void tearDown() {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }
    
    @Test
    @DisplayName("should persist DTO to both tables")
    public void shouldPersistToBothTables() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setAge(integer().next());
        userDto.setName(string().next());

        //When
        UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
        userDtoRepository.save(userDto);

        //Then
        Statement usersQueryStatement = new SimpleStatement(String.format("select name from %s.users", KEYSPACE_NAME));
        ResultSet usersRows = mappingManager.getSession().execute(usersQueryStatement);
        List<Row> result = usersRows.all();
        assertThat(result).hasSize(1);

        //And
        String name = result.get(0).get("name", String.class);
        assertThat(name).isEqualTo(userDto.getName());

        //And
        Statement usersByNameQueryStatement = new SimpleStatement(String.format("select name, age from %s.usersByName", KEYSPACE_NAME));
        ResultSet usersByNameRows = mappingManager.getSession().execute(usersByNameQueryStatement);
        List<Row> resultsByName = usersByNameRows.all();
        assertThat(resultsByName).hasSize(1);

        //And
        String name2 = resultsByName.get(0).get("name", String.class);
        Integer age = resultsByName.get(0).get("age", Integer.class);
        assertThat(name2).isEqualTo(userDto.getName());
        assertThat(age).isEqualTo(userDto.getAge());
    }

}