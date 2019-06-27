package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto2ParametersCreator {

    private final UserDto2 userDto2;

    public UserDto2ParametersCreator(UserDto2 userDto2) {
        this.userDto2 = userDto2;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto2.getFullName()).toArray(), "users_by_name", newArrayList(userDto2.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto2.getFullName()).toArray(), "users_by_name", newArrayList(userDto2.getFullName()).toArray());
    }
}
