package uk.co.caeldev.cassitory;

import com.datastax.driver.core.Session;

public class BaseCassandraRepository {

    protected final Session session;
    protected final TableConfig tableConfig;

    public BaseCassandraRepository(Session session, TableConfig tableConfig) {
        this.session = session;
        this.tableConfig = tableConfig;
    }
}
