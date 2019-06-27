package uk.co.caeldev.cassitory.statements.generators;

public class MappingDescriptor {
    private String table;
    private String field;
    private boolean pk;

    public MappingDescriptor(String table, String field, boolean pk) {
        this.table = table;
        this.field = field;
        this.pk = pk;
    }

    public MappingDescriptor() {
    }

    public String getTable() {
        return table;
    }

    public String getField() {
        return field;
    }

    public boolean isPk() {
        return pk;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setPk(boolean pk) {
        this.pk = pk;
    }
}
