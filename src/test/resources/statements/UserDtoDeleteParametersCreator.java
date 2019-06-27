package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDtoDeleteParametersCreator {

    private final UserDtoDelete userDtoDelete;

    public UserDtoDeleteParametersCreator(UserDtoDelete userDtoDelete) {
        this.userDtoDelete = userDtoDelete;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDtoDelete.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDtoDelete.getFullName()).toArray());
    }
}
