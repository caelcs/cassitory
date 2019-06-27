package uk.co.caeldev.cassitory.pojos;


import uk.co.caeldev.cassitory.statements.CassitoryEntity;
import uk.co.caeldev.cassitory.statements.Mapping;

@CassitoryEntity(tables = {"users", "usersByName"})
public class UserDtoStatement {

    @Mapping(table = "users", field = "name", pk = true)
    @Mapping(table = "usersByName", field = "name", pk = true)
    private String fullName;

    @Mapping(table = "usersByName", field = "age", pk = true)
    private int age;

    public UserDtoStatement(String fullName, int age) {
        this.fullName = fullName;
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }
}