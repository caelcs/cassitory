package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto4ParametersCreator {

    private final UserDto4 userDto4;

    public UserDto4ParametersCreator(UserDto4 userDto4) {
        this.userDto4 = userDto4;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto4.getFullName()).toArray(), "users_by_age", newArrayList(userDto4.getFullName(), userDto4.getAge()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto4.getFullName()).toArray(), "users_by_age", newArrayList(userDto4.getAge()).toArray());
    }
}
