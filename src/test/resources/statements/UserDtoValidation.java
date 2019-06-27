package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users"})
public class UserDtoValidation {

    @Mapping(table = "", field = "name", pk = true)
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
