CREATE TABLE security2.users (name text, PRIMARY KEY (name));

CREATE TABLE security2.usersByName (name text, age int, PRIMARY KEY (name, age));