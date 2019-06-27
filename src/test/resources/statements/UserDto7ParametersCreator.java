package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto7ParametersCreator {

    private final UserDto7 userDto7;

    public UserDto7ParametersCreator(UserDto7 userDto7) {
        this.userDto7 = userDto7;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto7.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto7.getFullName()).toArray());
    }
}
