package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;

@CassitoryEntity(target = {User.class, UserByName.class})
public class UserMoreDto12 {

    @Mapping(target = {User.class, UserByName.class}, field = "name")
    private String name;

    @Mapping(target = {UserByName.class})
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
