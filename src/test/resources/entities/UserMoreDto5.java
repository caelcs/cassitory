package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;
import uk.co.caeldev.cassitory.pojos.User;

@CassitoryEntity(target = {User.class})
public class UserMoreDto5 {

    @Mapping(target = {User.class})
    @Mapping(target = {User.class}, field = "name")
    private String name;

    public String getName() {
        return name;
    }
}
