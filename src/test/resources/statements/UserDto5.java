package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users"})
public class UserDto5 {

    @Mapping(table = "users", field = "name", pk = true)
    @Mapping(table = "users_by_age", field = "name")
    private String fullName;

    @Mapping(table = "users_by_age", field = "age", pk = true)
    private int age;

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }
}
