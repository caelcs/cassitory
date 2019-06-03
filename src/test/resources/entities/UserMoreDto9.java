package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserBySurname;

@CassitoryEntity(target = {User.class})
public class UserMoreDto9 {

    @Mapping(target = {User.class, UserBySurname.class}, field = "")
    @Mapping(target = {User.class, UserBySurname.class}, field = "")
    private String name;

    public String getName() {
        return name;
    }
}
