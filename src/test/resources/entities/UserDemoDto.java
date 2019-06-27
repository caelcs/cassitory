package entities;

import uk.co.caeldev.cassitory.entities.CassitoryEntity;
import uk.co.caeldev.cassitory.entities.Mapping;
import uk.co.caeldev.cassitory.pojos.UserDemo;

@CassitoryEntity(target = {UserDemo.class})
public class UserDemoDto {

    @Mapping(target = {UserDemo.class}, field = "nickname")
    private String name;

    public String getName() {
        return name;
    }
}
