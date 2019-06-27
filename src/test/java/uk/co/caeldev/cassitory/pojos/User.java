package uk.co.caeldev.cassitory.pojos;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "security2", name = "users",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM")
public class User {

    @PartitionKey
    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
