package statements;

import com.google.common.collect.ImmutableMap;
import java.lang.String;
import java.util.Map;

public class UserDto4QueriesCreator {
    public static Map<String, String> saveQueries = ImmutableMap.of("users", "INSERT INTO users (name) VALUES (?);", "users_by_age", "INSERT INTO users_by_age (name, age) VALUES (?, ?);");

    public static Map<String, String> deleteQueries = ImmutableMap.of("users", "DELETE FROM users WHERE name=?;", "users_by_age", "DELETE FROM users_by_age WHERE name=? AND age=?;");
}