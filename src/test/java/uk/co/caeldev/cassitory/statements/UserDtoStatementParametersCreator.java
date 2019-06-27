package uk.co.caeldev.cassitory.statements;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import uk.co.caeldev.cassitory.pojos.UserDtoStatement;

import java.util.Map;

class UserDtoStatementParametersCreator {

    private final UserDtoStatement userDtoStatement;

    public UserDtoStatementParametersCreator(UserDtoStatement userDtoStatement) {
        this.userDtoStatement = userDtoStatement;
    }

    public Map<String, Object[]> createDeleteParameters() {
        return ImmutableMap.of("users", Lists.newArrayList(userDtoStatement.getFullName()).toArray(),
                "usersByName", Lists.newArrayList(userDtoStatement.getFullName(), userDtoStatement.getAge()).toArray());
    }

    public Map<String, Object[]> createSaveParameters() {
        return ImmutableMap.of("users", Lists.newArrayList(userDtoStatement.getFullName()).toArray(),
                "usersByName", Lists.newArrayList(userDtoStatement.getFullName(), userDtoStatement.getAge()).toArray());
    }
}
