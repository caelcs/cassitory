# Cassitory
[![Development status](https://img.shields.io/badge/status-Ready-green.svg)](https://shields.io/)
[![Build Status](https://travis-ci.org/caelcs/cassitory.svg?branch=master)](https://travis-ci.org/caelcs/cassitory)
[![Coverage Status](https://coveralls.io/repos/github/caelcs/cassitory/badge.svg)](https://coveralls.io/github/caelcs/cassitory)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.co.caeldev/cassitory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.co.caeldev/cassitory)

Cassitory allows you to handle redundancy tables in Cassandra (In case that Apache Spark is not an option or to complex for your needs)

In a normal scenario you would have to define a cassandra entity per table that holds the cassandra annotations, and for each of them a Repository 
that will be used by your service coordinating all the write operations and making sure that you persist
 to both tables by using both repositories, therefore you will have to create one instance per cassandra entity to persist.

The idea behind Cassitory is to hide all that complexity from you. 
Although you will still need to have your cassandra entities but no Repositories, no creation of multiple Cassandra entity instances.  
Instead you will have DTO that contains the data and knows how to map to each Cassandra entity and a Single repository to handle all the persistence layer to the multiple tables.
Based on the mapping it will create an instance of each Cassandra Entity and save it.

**ASYNC** is awesome! so you can save and delete using async operations but also provides a sync way in case that you have go down that route.

**NO REFLEXION**:
Cassitory does not use reflexion to do all the operations that provides and because of it there are some compromises if you want to use it.

- Your Cassandra entities must have a Getters and Setters.
- Your DTO must have Getters.

Cassitory use annotation processing to generate code than otherwise you would have to write it and it is quite good that if there is any error you would get it at compile time.


**Important Release Note**:

up to 0.2.1: supports only Mappers Repositories
higher than 1.0.0: support for Mappers and Prepered statements**  

## Motivation
it is well known that Cassandra recommends de-normalisation of your model. Meaning that in order to support different search criteria for searching the data, Cassandra recommends to create a redundancy table 
having as partition key the fields that you want search for. Applying this pattern in your application could be very difficult to maintain. it would be nice to have a generic repository that allowing you to 
have support multiple tables and also execute queries and decide which table is the correct one.

## Introduction
Cassitory is quite simple to use just following the steps and it will give you a Repository for your DTO ready to use in your application.
Cassitory gives you two type of repositories. 

#### Using Cassandra entities
The first one is by using Cassandra Entities Classes where you have to create your POJO and annotate them using Cassandra Object Mapper library.
and apart of those POJO you will have to create your DTO entity and annotate it with Cassitory annotations and it will be used to map the values to your Cassandra entities.
Here you have the annotations that you will have to use:

```uk.co.caeldev.cassitory.entities.CassitoryEntity```
```uk.co.caeldev.cassitory.entities.Mapping```

#### Using Prepared Statements and queries
with this repo you don't have to create your Cassandra entities, just your DTO annotated with Cassitory annotations.
Here you have the annotations that you will have to use:

```uk.co.caeldev.cassitory.statements.CassitoryEntity```
```uk.co.caeldev.cassitory.statements.Mapping```

### How to use it
Add to your gradle dependency to run the annotation processor:

```
annotationProcessor 'uk.co.caeldev:cassitory:0.2.1'

or 

annotationProcessor 'uk.co.caeldev:cassitory:1.0.0'
```

then annotate your DTO class with the Cassitory annotations that fits better for your project. 

- [Using Cassandra Mappers](https://github.com/caelcs/cassitory/wiki/Cassitory-using-Entity-Mappers) 
- [Using Cassandra prepared statements](https://github.com/caelcs/cassitory/wiki/Cassitory-using-only-prepared-statements)
 
