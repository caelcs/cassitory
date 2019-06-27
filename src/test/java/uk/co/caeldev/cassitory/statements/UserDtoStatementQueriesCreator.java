package uk.co.caeldev.cassitory.statements;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

class UserDtoStatementQueriesCreator {

    public static Map<String, String> saveQueries = ImmutableMap.of("users", "INSERT INTO users (name) VALUES (?);", "usersByName", "INSERT INTO usersByName (name, age) VALUES (?, ?);");

    public static Map<String, String> deleteQueries = ImmutableMap.of("users", "DELETE FROM users WHERE name=?;", "usersByName", "DELETE FROM usersByName WHERE name=? AND age=?;");

}
