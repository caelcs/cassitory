# Cassitory
[![Development status](https://img.shields.io/badge/status-incubator-green.svg)](https://shields.io/)
[![Build Status](https://travis-ci.org/caelcs/cassitory.svg?branch=master)](https://travis-ci.org/caelcs/cassitory)
[![Coverage Status](https://coveralls.io/repos/github/caelcs/cassitory/badge.svg)](https://coveralls.io/github/caelcs/cassitory)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.co.caeldev/cassitory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.co.caeldev/cassitory)

Cassitory allows you to handle redundancy tables in Cassandra (In case that Apache Spark is not an option or to complex for your needs)

In a normal scenario you would have to define a cassandra entity per table that holds the cassandra annotations to map to it.
and for each of them a Repository which will be used by your service to coordinate all the write operations and making sure that you persist
 to both tables by using both repositories, therefore you will have to create one instance per cassandra entity to persist.

The idea behind Cassitory is to hide all that complexity from you. 
Although you will still need to have your cassandra entities but no Repositories, no creation of multiple Cassandra entity instances.  
Instead you will have DTO that contains the data and knows how to map to each Cassandra entity and a Single repository to handle all the persistence layer to the multiple tables.
Based on the mapping will create an instance of each Cassandra Entity and save it.

**No Reflexion**:
Cassitory does not use reflexion to do all the operations that provides abd because of it there are some compromises if you want to use it.

- Your Cassandra entities must have a Getters and Setters.
- Your DTO must have Getters.

Cassitory is annotation processing to generate code than otherwise you would have to write it and it is quite good that if there is any error you would get it at compile time.

## Motivation
it is well known that Cassandra recommends de-normalisation of your model. Meaning that in order to support different criteria of search the data, Cassandra recommends to create a redundancy table having as partition key the fields that you want search for. Applying this pattern in your application could be very difficult to maintain. it would be nice to have a generic repository that allowing you to have support multiple tables and also execute queries and decide which table is the correct one.

## Steps to use the library
The library is quite simple to use just following the steps will give you a Repository for your DTO ready to use in your application.

### Step 1: Create your cassandra entities
First you create your Cassandra Entities mapping to your tables as you would do it normally.
Eg. you a users users_by_name tables so that would mean two Cassandra entities more like this:

```java
@Table(keyspace = "ks", name = "users",
       readConsistency = "QUORUM",
       writeConsistency = "QUORUM",
       caseSensitiveKeyspace = false,
       caseSensitiveTable = false)
public class User {
    @PartitionKey
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "name")
    private String name;

    //WITH THE SETTERS AND GETTERS
}

@Table(keyspace = "ks", name = "users_by_name",
       readConsistency = "QUORUM",
       writeConsistency = "QUORUM",
       caseSensitiveKeyspace = false,
       caseSensitiveTable = false)
public class UserByName {
    @PartitionKey(0)
    @Column(name = "user_id")
    private UUID userId;

    @PartitionKey(1)
    @Column(name = "full_name")
    private String name;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    //WITH THE SETTERS AND GETTERS
}
```
NOTE: for version 1, there a convention that, let say that your main table is USERS, and USERS_BY_NAME is derived the beans needs to have the same name of fields.
Also all the getters and setters.

### Step 2: Create your DTO.

The library works by using the DTO as placeholder for the data that you want to persist and it will hold how to map to the Cassandra entities.
Following the eg. above this would looks like this.

```java
@CassitoryEntity(target={UserByName.class, User.class})
class UserDto {
	
	@Map(target={User.class, UserByName.class}, field="userId")
	private String id;

	@Map(target={UserByName.class}, field="creationDate")
	private LocalDate creationDate;

	@Map(target={User.class, UserByName.class}, field="name")
	private String name;

    // GENERATE ALL THE GETTERS AND SETTERS
}
```



