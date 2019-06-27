package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {""})
public class UserDtoValidation3 {

    @Mapping(table = "users", field = "name")
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
