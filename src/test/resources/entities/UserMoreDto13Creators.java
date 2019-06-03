package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;


public class UserMoreDto13Creators {
    private final UserMoreDto13 userMoreDto13;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserByName> userByNameCreator = () -> createUserByName();

    public List<Supplier> creators = newArrayList(userCreator, userByNameCreator);

    public UserMoreDto13Creators(UserMoreDto13 userMoreDto13) {
        this.userMoreDto13 = userMoreDto13;
    }

    private User createUser() {
        User user = new User();
        user.setName(userMoreDto13.getName());
        return user;
    }

    private UserByName createUserByName() {
        UserByName userByName = new UserByName();
        userByName.setName(userMoreDto13.getName());
        userByName.setAge(userMoreDto13.getAge());
        return userByName;
    }
}
