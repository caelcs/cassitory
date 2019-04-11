package uk.co.caeldev.cassitory;

import java.util.Set;

public class TableConfig {

    private final String masterTableName;
    private final Set<String> derivedTables;

    public TableConfig(String masterTableName, Set<String> derivedTables) {
        this.masterTableName = masterTableName;
        this.derivedTables = derivedTables;
    }
}
