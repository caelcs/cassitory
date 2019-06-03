package uk.co.caeldev.cassitory.pojos;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "security", name = "usersBySurname",
        readConsistency = "QUORUM",
        writeConsistency = "QUORUM")
public class UserBySurname {

    @PartitionKey
    @Column(name = "surname")
    private String surname;

    @PartitionKey(1)
    @Column(name = "age")
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
