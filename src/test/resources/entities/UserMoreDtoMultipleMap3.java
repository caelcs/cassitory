package entities;

import com.datastax.driver.core.ConsistencyLevel;
import uk.co.caeldev.cassitory.entities.CassitoryEntity;
import uk.co.caeldev.cassitory.entities.Mapping;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;

@CassitoryEntity(target = {User.class, UserByName.class}, destinationPackage = "entities.repositories", consistencyLevel = ConsistencyLevel.ALL)
public class UserMoreDtoMultipleMap3 {

    @Mapping(target = {User.class, UserByName.class})
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
