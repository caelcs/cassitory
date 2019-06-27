package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto5ParametersCreator {

    private final UserDto5 userDto5;

    public UserDto5ParametersCreator(UserDto5 userDto5) {
        this.userDto5 = userDto5;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto5.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto5.getFullName()).toArray());
    }
}
