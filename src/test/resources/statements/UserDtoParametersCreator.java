package statements;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.ImmutableMap;
import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class UserDtoParametersCreator {

    private final UserDto userDto;

    public UserDtoParametersCreator(UserDto userDto) {
        this.userDto = userDto;
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", newArrayList(userDto.getFullName()).toArray());
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", newArrayList(userDto.getFullName()).toArray());
    }
}
