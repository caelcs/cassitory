package uk.co.caeldev.cassitory.pojos;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "security", name = "usersByFullName",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM")
public class UserByFullName {

    @PartitionKey
    @Column(name = "surname")
    private String surname;

    @PartitionKey(1)
    @Column(name = "name")
    private String name;

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
