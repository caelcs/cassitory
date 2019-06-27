package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto6ParametersCreator {

    private final UserDto6 userDto6;

    public UserDto6ParametersCreator(UserDto6 userDto6) {
        this.userDto6 = userDto6;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto6.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto6.getFullName()).toArray());
    }
}
