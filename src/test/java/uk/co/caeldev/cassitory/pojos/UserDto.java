package uk.co.caeldev.cassitory.pojos;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;

@CassitoryEntity(target = {User.class, UserByName.class})
public class UserDto {

    @Mapping(target = {User.class, UserByName.class}, field = "name")
    private String name;

    @Mapping(target = {UserByName.class}, field = "age")
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
