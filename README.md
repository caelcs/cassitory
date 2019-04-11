# Builder4test
[![Build Status](https://travis-ci.org/caelcs/cassitory.svg?branch=master)](https://travis-ci.org/caelcs/cassitory)
[![Coverage Status](https://coveralls.io/repos/github/caelcs/cassitory/badge.svg)](https://coveralls.io/github/caelcs/cassitory)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/uk.co.caeldev/cassitory/badge.svg)](https://maven-badges.herokuapp.com/maven-central/uk.co.caeldev/cassitory)

Library allows you to have a easy way to handle redundancy tables in Cassandra.

## Motivation
it is well known that Cassandra recomends denormalisation of your model. Meaning that in order to support different criteria of search the data, Cassandra recommends to create a redundancy table having as partition key the fields that you want search for. Applying this pattern in your application could be very difficult to maintain. it would be nice to have a generic repository that allowing you to have support multiple tables and also execute queries and decide which table is the correct one.
