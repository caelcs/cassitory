package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.Mapping;
import uk.co.caeldev.cassitory.pojos.UserByFullName;

@CassitoryEntity(target = {UserByFullName.class})
public class UserMoreDto11 {

    @Mapping(target = {UserByFullName.class}, field = "surname")
    @Mapping(target = {UserByFullName.class}, field = "name")
    private String name;

    public String getName() {
        return name;
    }
}
