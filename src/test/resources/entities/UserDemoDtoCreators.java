package entities;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.UserDemo;

public class UserDemoDtoCreators {
    private final UserDemoDto userDemoDto;

    private Supplier<UserDemo> userDemoCreator = () -> createUserDemo();

    public List<Supplier> creators = newArrayList(userDemoCreator);

    public UserDemoDtoCreators(UserDemoDto userDemoDto) {
        this.userDemoDto = userDemoDto;
    }

    private UserDemo createUserDemo() {
        UserDemo userDemo = new UserDemo();
        userDemo.setNickname(userDemoDto.getName());
        return userDemo;
    }
}
