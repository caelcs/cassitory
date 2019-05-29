package uk.co.caeldev.cassitory.pojos;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "security", name = "usersByName",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM")
public class UserByName {

    @PartitionKey
    @Column(name = "name")
    private String name;

    @PartitionKey(1)
    @Column(name = "age")
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
