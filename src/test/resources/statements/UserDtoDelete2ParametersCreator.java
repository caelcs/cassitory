package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDtoDelete2ParametersCreator {

    private final UserDtoDelete2 userDtoDelete2;

    public UserDtoDelete2ParametersCreator(UserDtoDelete2 userDtoDelete2) {
        this.userDtoDelete2 = userDtoDelete2;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDtoDelete2.getFullName(), userDtoDelete2.getAge()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDtoDelete2.getFullName()).toArray());
    }
}
