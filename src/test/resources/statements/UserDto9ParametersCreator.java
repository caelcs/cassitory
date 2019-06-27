package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto9ParametersCreator {

    private final UserDto9 userDto9;

    public UserDto9ParametersCreator(UserDto9 userDto9) {
        this.userDto9 = userDto9;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto9.getFullName()).toArray(), "users_by_age", newArrayList(userDto9.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto9.getFullName()).toArray(), "users_by_age", newArrayList(userDto9.getFullName()).toArray());
    }
}
