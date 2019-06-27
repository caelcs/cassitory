package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users"})
public class UserDto7 {

    @Mapping(table = " users ", pk = true)
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
