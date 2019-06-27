package entities;

import uk.co.caeldev.cassitory.entities.Mapping;
import uk.co.caeldev.cassitory.entities.CassitoryEntity;
import uk.co.caeldev.cassitory.pojos.User;

@CassitoryEntity(target = {User.class})
public class FooInvalid7 {

    @Mapping(target = {User.class, User.class}, field = "")
    private String value;

    public String getValue() {
        return value;
    }
}
