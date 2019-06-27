package statements;

import com.google.common.collect.ImmutableMap;
import java.lang.String;
import java.util.Map;

public class UserDto3QueriesCreator {
    public static Map<String, String> saveQueries = ImmutableMap.of("users", "INSERT INTO users (name, age) VALUES (?, ?);");

    public static Map<String, String> deleteQueries = ImmutableMap.of("users", "DELETE FROM users WHERE name=? AND age=?;");
}