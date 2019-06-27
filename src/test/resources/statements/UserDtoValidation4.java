package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"    ", "users"})
public class UserDtoValidation4 {

    @Mapping(table = "users", field = "name")
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
