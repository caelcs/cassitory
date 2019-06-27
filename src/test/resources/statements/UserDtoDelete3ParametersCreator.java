package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDtoDelete3ParametersCreator {

    private final UserDtoDelete3 userDtoDelete3;

    public UserDtoDelete3ParametersCreator(UserDtoDelete3 userDtoDelete3) {
        this.userDtoDelete3 = userDtoDelete3;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDtoDelete3.getFullName(), userDtoDelete3.getAge()).toArray(), "users_by_age", newArrayList(userDtoDelete3.getFullName(), userDtoDelete3.getAge()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDtoDelete3.getFullName()).toArray(), "users_by_age", newArrayList(userDtoDelete3.getAge()).toArray());
    }
}
