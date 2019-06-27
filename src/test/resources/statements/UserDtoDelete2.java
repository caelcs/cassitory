package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users"})
public class UserDtoDelete2 {

    @Mapping(table = "users", field = "name", pk=true)
    private String fullName;

    @Mapping(table = "users", field = "age")
    private String age;

    public String getFullName() {
        return fullName;
    }

    public String getAge() {
        return age;
    }
}
