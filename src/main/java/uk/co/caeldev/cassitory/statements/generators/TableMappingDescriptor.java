package uk.co.caeldev.cassitory.statements.generators;

import java.util.Map;

public class TableMappingDescriptor {

    private String tableName;
    private Map<String, MappingDescriptor> mappingDescriptors;

    public TableMappingDescriptor(String tableName, Map<String, MappingDescriptor> mappingDescriptors) {
        this.tableName = tableName;
        this.mappingDescriptors = mappingDescriptors;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, MappingDescriptor> getMappingDescriptors() {
        return mappingDescriptors;
    }
}
