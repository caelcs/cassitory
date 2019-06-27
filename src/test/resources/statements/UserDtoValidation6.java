package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users"})
public class UserDtoValidation6 {

    @Mapping(table = "users", field = "name", pk = true)
    @Mapping(table = "", field = "name", pk = true)
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
