# Resilience4j + Spring + Kotlin Demo

This repo shows how to use [resilience4j](https://resilience4j.readme.io/) with **kotlin** in a **spring webflux** app.

## Topics covered by this example

* Spring boot auto-configuration for resilience4j using `application.yml` or `application.properties`
* Kotlin `suspend` and `Flow` calls with circuit breaker
  using [resilience4j-kotlin](https://resilience4j.readme.io/v1.7.0/docs/getting-started-4) module
* Extend `CircuitBreaker` class to run or decorate `suspend` functions with a `fallback` action when circuit is open
* Add a `fallback` for a `Flow` call when circuit is open
