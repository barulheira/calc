ST WIT Calculator demo
======================

A basic REST calculator built using Spring Boot and AMQP in a modular
Maven project.

Modules
-------

 - `calculator`: a service that connects to a messaging server awaiting for
    calculation requests
 - `rest`: a web service that forwards REST requests to the messaging server,
    responding with the results of calculations

Available requests
------------------

GET requests with `a` and `b` decimal values in the query string. The available
operations are:

 - `sum` for adding
 - `sub` for subtracting
 - `multi` for multiplying
 - `div` for dividing

Example: `GET /multi?a=1.200&b=2.5` returns `{"result":3.0000}`

The decimal precision of the results depend on the decimal precisions of the
operands.

Runtime errors are returning raw.

Run
---

Run as a Spring Boot project.

Each module can be run locally with:

    mvn spring-boot:run

It connects the module service to the messaging server (i. e. RabbitMQ) which is
running at `localhost`.

Each module can run on different machines. Configure a messaging server other
than `localhost` at runtime with the application property `st.wit.rabbitmq.host`,
using [Spring configuration options for application.properties](https://www.baeldung.com/properties-with-spring)