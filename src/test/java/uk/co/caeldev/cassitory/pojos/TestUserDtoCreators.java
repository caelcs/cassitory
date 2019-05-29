package uk.co.caeldev.cassitory.pojos;

import java.util.List;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;

public class TestUserDtoCreators {
    private final UserDto userDto;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserByName> userByNameCreator = () -> createUserByName();

    public List<Supplier> creators = newArrayList(userCreator, userByNameCreator);

    public TestUserDtoCreators(UserDto userDto) {
        this.userDto = userDto;
    }

    private User createUser() {
        User user = new User();
        user.setName(userDto.getName());
        return user;
    }

    private UserByName createUserByName() {
        UserByName userByName = new UserByName();
        userByName.setName(userDto.getName());
        userByName.setAge(userDto.getAge());
        return userByName;
    }
}
