package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;

public class UserDtoWithMapCreators {
    private final UserDtoWithMap userDtoWithMap;

    private Supplier<User> userCreator = () -> createUser();

    public List<Supplier> creators = newArrayList(userCreator);

    public UserDtoWithMapCreators(UserDtoWithMap userDtoWithMap) {
        this.userDtoWithMap = userDtoWithMap;
    }

    private User createUser() {
        User user = new User();
        user.setName(userDtoWithMap.getName());
        return user;
    }
}
