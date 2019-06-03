package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;


public class UserMoreDto12Creators {
    private final UserMoreDto12 userMoreDto12;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserByName> userByNameCreator = () -> createUserByName();

    public List<Supplier> creators = newArrayList(userCreator, userByNameCreator);

    public UserMoreDto12Creators(UserMoreDto12 userMoreDto12) {
        this.userMoreDto12 = userMoreDto12;
    }

    private User createUser() {
        User user = new User();
        user.setName(userMoreDto12.getName());
        return user;
    }

    private UserByName createUserByName() {
        UserByName userByName = new UserByName();
        userByName.setName(userMoreDto12.getName());
        userByName.setAge(userMoreDto12.getAge());
        return userByName;
    }
}
