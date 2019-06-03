package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserBySurname;

@CassitoryEntity(target = {User.class, UserBySurname.class})
public class UserMoreDto2 {

    @Mapping(target = {User.class}, field = "name")
    @Mapping(target = {UserBySurname.class}, field = "surname")
    private String name;

    @Mapping(target = {UserBySurname.class}, field = "age")
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
