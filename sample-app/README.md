# Sample application

This is a super simple JAX-RS RESTEasy Spring Boot application just to exercise RESTEasy Spring Boot starter.<br>

## Starting the application

You can start the application as you for any other regular Spring Boot application. For example:

1. From the command line, under the sample application project, run `mvn spring-boot:run`
1. From your favorite IDE, run class `com.test.Application`

## Testing it

Send a **GET** request to the endpoint below.

[http://localhost:8080/sample-app/echo/echo](http://localhost:8080/sample-app/echo/echo)

You should receive a response message with this payload as result:

``` json
{
    "wordId": 1,
    "wordString": "Lua"
}
```
   
## Automated tests

 1. Unit tests are located in `src/test/java` and end with `Test`. Execute the tests with `mvn test`
 
 2. Integration tests are located in `src/test/java` and end with `IT`. Execute the integration tests with `mvn integration-test`
 
 
 Unit tests are fast running test which are executed on normal builds. Integration tests are longer-running tests. The integration test 
 for this example server starts an embedded container and fires requests against the running application.