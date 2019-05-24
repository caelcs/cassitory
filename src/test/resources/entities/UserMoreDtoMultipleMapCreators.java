package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;

public class UserMoreDtoMultipleMapCreators {

    private final UserMoreDtoMultipleMap userMoreDtoMultipleMap;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserByName> userByNameCreator = () -> createUserByName();

    public List<Supplier> creators = newArrayList(userCreator, userByNameCreator);

    public UserMoreDtoMultipleMapCreators(UserMoreDtoMultipleMap userMoreDtoMultipleMap) {
        this.userMoreDtoMultipleMap = userMoreDtoMultipleMap;
    }

    private User createUser() {
        User user = new User();
        user.setName(userMoreDtoMultipleMap.getName());
        return user;
    }

    private UserByName createUserByName() {
        UserByName userByName = new UserByName();
        userByName.setName(userMoreDtoMultipleMap.getName());
        userByName.setAge(userMoreDtoMultipleMap.getAge());
        return userByName;
    }
}
