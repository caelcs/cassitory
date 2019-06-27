package entities;

import uk.co.caeldev.cassitory.entities.CassitoryEntity;
import uk.co.caeldev.cassitory.entities.Mapping;
import uk.co.caeldev.cassitory.pojos.User;

@CassitoryEntity(target = {User.class})
public class UserDtoWithMap {

    @Mapping(target = {User.class}, field = "name")
    private String name;

    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
