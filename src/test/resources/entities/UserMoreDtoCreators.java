package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;

public class UserMoreDtoCreators {

    private final UserMoreDto userMoreDto;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserByName> userByNameCreator = () -> createUserByName();

    public List<Supplier> creators = newArrayList(userCreator, userByNameCreator);

    public UserMoreDtoCreators(UserMoreDto userMoreDto) {
        this.userMoreDto = userMoreDto;
    }

    private User createUser() {
        User user = new User();
        return user;
    }

    private UserByName createUserByName() {
        UserByName userByName = new UserByName();
        return userByName;
    }
}
