package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;

public class UserDtoCreators {

    private final UserDto userDto;

    private Supplier<User> userCreator = () -> createUser();

    public List<Supplier> creators = newArrayList(userCreator);

    public UserDtoCreators(UserDto userDto) {
        this.userDto = userDto;
    }

    private User createUser() {
        User user = new User();
        return user;
    }
}
