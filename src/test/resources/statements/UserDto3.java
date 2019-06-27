package statements;

import uk.co.caeldev.cassitory.statements.Mapping;
import uk.co.caeldev.cassitory.statements.CassitoryEntity;

@CassitoryEntity(tables = {"users"})
public class UserDto3 {

    @Mapping(table = "users", field = "name", pk = true)
    private String fullName;

    @Mapping(table = "users", field = "age")
    private int age;

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }
}
