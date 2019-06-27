package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDto3ParametersCreator {

    private final UserDto3 userDto3;

    public UserDto3ParametersCreator(UserDto3 userDto3) {
        this.userDto3 = userDto3;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto3.getFullName(), userDto3.getAge()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto3.getFullName()).toArray());
    }
}
