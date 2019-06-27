package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto8ParametersCreator {

    private final UserDto8 userDto8;

    public UserDto8ParametersCreator(UserDto8 userDto8) {
        this.userDto8 = userDto8;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto8.getFullName()).toArray(), "users_by_age", newArrayList(userDto8.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto8.getFullName()).toArray(), "users_by_age", newArrayList(userDto8.getFullName()).toArray());
    }
}
