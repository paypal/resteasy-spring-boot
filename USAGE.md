# How to use RESTEasy Spring Boot Starter

### Adding POM dependency
Add the Maven dependency below to your Spring Boot application pom file.<br>

```
<dependency>
   <groupId>com.paypal.springboot</groupId>
   <artifactId>resteasy-spring-boot-starter</artifactId>
   <version>2.1.0-SNAPSHOT</version>
   <scope>runtime</scope>
</dependency>
```

### Registering JAX-RS application classes
Just define your JAX-RS application class (a subclass of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html)) as a Spring bean, and it will be automatically registered.
See section [JAX-RS application registration methods](#jax-rs-application-registration-methods) for further information.

### Registering JAX-RS resources and providers
Just define them as Spring beans, and they will be automatically registered.
Notice that JAX-RS resources can be singleton or request scoped, while JAX-RS providers must be singletons.

## Advanced topics

### JAX-RS application registration methods

JAX-RS applications are defined via sub-classes of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html). One or more JAX-RS applications can be registered, and there are three different methods to do so:

1. By having them defined as Spring beans.
2. By setting property `resteasy.jaxrs.app` via Spring Boot `application.properties` file. This property should contain a comma separated list of JAX-RS sub-classes.
3. Automatically by classpath scanning (looking for `javax.ws.rs.core.Application` sub-classes).

You can define the method you prefer by setting property `resteasy.jaxrs.app.registration` (via Spring Boot configuration file), although you don't have to, in that case the `auto` method is the default. The possible values are:

1. `beans`
1. `property`
1. `scanning`
1. `auto` (default)

The first three values refer respectively to each one of the three methods described earlier. The last one, `auto`, when set (or when property `resteasy.jaxrs.app.registration` is not present), attempts first to find JAX-RS application classes by searching them as Spring beans. If any is found, the search stops, and those are the only JAX-RS applications to be registered. If no JAX-RS application Spring beans are found, then the `property` approach is tried. If still no JAX-RS application classes could be found, then the last method, `scanning`, is attempted. If after that still no JAX-RS application class could be registered, then the Spring Boot application won't be able to serve any request.

#### Important notes

1. If no JAX-RS application classes are found, a default one will be automatically created mapping to `/*` (_according to section 2.3.2 in the JAX-RS 2.0 specification_). Notice that, in this case, if you have any other Servlet in your application, their URL matching might conflict. For example, if you have SpringBoot actuator, its endpoints might not be reachable.
2. It is recommended to always have at least one JAX-RS application class.
3. A JAX-RS application class with no `javax.ws.rs.ApplicationPath` annotation will not be registered.
4. Avoid setting the JAX-RS application base URI to simply `/` to prevent URI conflicts, as explained in the first item.