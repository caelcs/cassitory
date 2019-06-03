package entities.repositories;

import static com.google.common.collect.Lists.newArrayList;

import entities.UserMoreDtoMultipleMap2;
import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;

public class UserMoreDtoMultipleMap2Creators {

    private final UserMoreDtoMultipleMap2 userMoreDtoMultipleMap2;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserByName> userByNameCreator = () -> createUserByName();

    public List<Supplier> creators = newArrayList(userCreator, userByNameCreator);

    public UserMoreDtoMultipleMap2Creators(UserMoreDtoMultipleMap2 userMoreDtoMultipleMap2) {
        this.userMoreDtoMultipleMap2 = userMoreDtoMultipleMap2;
    }

    private User createUser() {
        User user = new User();
        user.setName(userMoreDtoMultipleMap2.getName());
        return user;
    }

    private UserByName createUserByName() {
        UserByName userByName = new UserByName();
        userByName.setName(userMoreDtoMultipleMap2.getName());
        userByName.setAge(userMoreDtoMultipleMap2.getAge());
        return userByName;
    }
}
