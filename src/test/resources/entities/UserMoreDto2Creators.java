package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserBySurname;


public class UserMoreDto2Creators {
    private final UserMoreDto2 userMoreDto2;

    private Supplier<User> userCreator = () -> createUser();

    private Supplier<UserBySurname> userBySurnameCreator = () -> createUserBySurname();

    public List<Supplier> creators = newArrayList(userCreator, userBySurnameCreator);

    public UserMoreDtoMultipleMapCreators(UserMoreDto2 userMoreDto2) {
        this.userMoreDto2 = userMoreDto2;
    }

    private User createUser() {
        User user = new User();
        user.setName(userMoreDto2.getName());
        return user;
    }

    private UserBySurname createUserBySurname() {
        UserBySurname userBySurname = new UserBySurname();
        userBySurname.setSurname(userMoreDto2.getName());
        userBySurname.setAge(userMoreDto2.getAge());
        return userBySurname;
    }
}
