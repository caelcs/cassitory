package uk.co.caeldev.cassitory;

import com.datastax.driver.core.Session;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BaseCassandraRepositoryTest {

    @Mock
    private Session session;

    private BaseCassandraRepository baseCassandraRepository;

    @Test
    @DisplayName("Should be able to create the instance correctly.")
    public void initialiseCorrectly() {
        //Given
        Set<String> derivedTables = ImmutableSet.of("derivedTable1", "derivedTable2");
        TableConfig tableConfig = new TableConfig("masterTableName", derivedTables);

        baseCassandraRepository = new BaseCassandraRepository(session, tableConfig);

        assertThat(baseCassandraRepository).isNotNull();
        assertThat(baseCassandraRepository.session).isNotNull();
        assertThat(baseCassandraRepository.session).isEqualTo(session);
        assertThat(baseCassandraRepository.tableConfig).isNotNull();
        assertThat(baseCassandraRepository.tableConfig).isEqualTo(tableConfig);
    }
}