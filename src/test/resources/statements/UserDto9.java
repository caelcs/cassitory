package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {" users ", " users_by_age "})
public class UserDto9 {

    @Mapping(table = " users ", pk = true)
    @Mapping(table = " users_by_age ", pk = true)
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
