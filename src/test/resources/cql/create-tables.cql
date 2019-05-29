CREATE TABLE security.users (name text, PRIMARY KEY (name));

CREATE TABLE security.usersByName (name text, age int, PRIMARY KEY (name, age));