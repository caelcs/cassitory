package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;
import uk.co.caeldev.cassitory.pojos.User;

@CassitoryEntity(target = {User.class})
public class UserMoreDto8 {

    @Mapping(target = {User.class}, field = "")
    @Mapping(target = {User.class}, field = "")
    private String name;

    public String getName() {
        return name;
    }
}
