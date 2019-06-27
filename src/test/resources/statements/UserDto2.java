package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users", "users_by_name"})
public class UserDto2 {

    @Mapping(table = "users", field = "name",pk = true)
    @Mapping(table = "users_by_name", field = "fullName", pk = true)
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
