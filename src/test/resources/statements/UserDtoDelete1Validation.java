package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users", "users_by_age"})
public class UserDtoDelete1Validation {

    @Mapping(table = "users", field = "name")
    @Mapping(table = "users_by_age", field = "name")
    private String fullName;

    @Mapping(table = "users", field = "age")
    @Mapping(table = "users_by_age", field = "age")
    private String age;

    public String getFullName() {
        return fullName;
    }

    public String getAge() {
        return age;
    }
}
