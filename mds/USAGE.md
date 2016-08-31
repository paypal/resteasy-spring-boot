# How to use RESTEasy Spring Boot Starter

#### Adding POM dependency
Add the Maven dependency below to your Spring Boot application pom file.<br>

```
<dependency>
   <groupId>com.paypal.springboot</groupId>
   <artifactId>resteasy-spring-boot-starter</artifactId>
   <version>2.2.0-RELEASE</version>
   <scope>runtime</scope>
</dependency>
```

#### Registering JAX-RS application classes
Just define your JAX-RS application class (a subclass of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html)) as a Spring bean, and it will be automatically registered. See the example below.
See section [JAX-RS application registration methods](#jax-rs-application-registration-methods) for further information.

```
package com.test;

import org.springframework.stereotype.Component;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Component
@ApplicationPath("/sample-app/")
public class JaxrsApplication extends Application {
}
```

#### Registering JAX-RS resources and providers
Just define them as Spring beans, and they will be automatically registered.
Notice that JAX-RS resources can be singleton or request scoped, while JAX-RS providers must be singletons.

## Advanced topics

#### JAX-RS application registration methods

JAX-RS applications are defined via sub-classes of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html). One or more JAX-RS applications can be registered, and there are three different methods to do so:

1. By having them defined as Spring beans.
2. By setting property `resteasy.jaxrs.app.classes` via Spring Boot configuration file (properties or YAML). This property should contain a comma separated list of JAX-RS sub-classes.
3. Automatically by classpath scanning (looking for `javax.ws.rs.core.Application` sub-classes).

You can define the method you prefer by setting property `resteasy.jaxrs.app.registration` (via Spring Boot configuration file), although you don't have to, in that case the `auto` method is the default. The possible values are:

1. `beans`
1. `property`
1. `scanning`
1. `auto` (default)

The first three values refer respectively to each one of the three methods described earlier. The last one, `auto`, when set (or when property `resteasy.jaxrs.app.registration` is not present), attempts first to find JAX-RS application classes by searching them as Spring beans. If any is found, the search stops, and those are the only JAX-RS applications to be registered. If no JAX-RS application Spring beans are found, then the `property` approach is tried. If still no JAX-RS application classes could be found, then the last method, `scanning`, is attempted. If after that still no JAX-RS application class could be registered, then a default one will be automatically created mapping to `/*` (_according to section 2.3.2 in the JAX-RS 2.0 specification_).

__Important notes__

1. If no JAX-RS application classes are found, a default one will be automatically created mapping to `/*` (_according to section 2.3.2 in the JAX-RS 2.0 specification_). Notice that, in this case, if you have any other Servlet in your application, their URL matching might conflict. For example, if you have SpringBoot actuator, its endpoints might not be reachable.
1. It is recommended to always have at least one JAX-RS application class.
1. A JAX-RS application class with no `javax.ws.rs.ApplicationPath` annotation will not be registered.
1. Avoid setting the JAX-RS application base URI to simply `/` to prevent URI conflicts, as explained in the first item.
1. Property `resteasy.jaxrs.app` has been deprecated and replaced by `resteasy.jaxrs.app.classes` since version *2.2.0-RELEASE* (see [issue 35](https://github.com/paypal/resteasy-spring-boot/issues/35)). Property `resteasy.jaxrs.app` is going to be finally removed in version *3.0.0-RELEASE*.

#### RESTEasy configuration
RESTEasy offers a few configuration switches, [as seen here](http://docs.jboss.org/resteasy/docs/3.0.17.Final/userguide/html_single/index.html#d4e127), and they are set as Servlet context init parameters. In Spring Boot, Servlet context init parameters are defined via Spring Boot `application.properties` file, using the property prefix `server.context-parameters.*` (search for it in [Spring Boot reference guide](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)).</br>

As an example, to set RESTEasy property `resteasy.role.based.security` to `true`, just add the property bellow to Spring Boot `application.properties` file.

```
server.context-parameters.resteasy.role.based.security=true
```

It is important to mention that the following RESTEasy configuration options are NOT applicable to an application using RESTEasy Spring Boot starter.
All other RESTEasy configuration options are supported normally.

| Configuration option | Why it is not applicable |
|---|---|
|`javax.ws.rs.Application`|JAX-RS application classes are registered as explained in section _"JAX-RS application registration methods"_ above|
|`resteasy.servlet.mapping.prefix`|The url-pattern for the Resteasy servlet-mapping is always based on the `ApplicationPath` annotation in the JAX-RS application class|
|`resteasy.scan`<br/>`resteasy.scan.providers`<br/>`resteasy.scan.resources`<br/>`resteasy.providers`<br/>`resteasy.use.builtin.providers`<br/>`resteasy.resources`<br/>`resteasy.jndi.resources`|All JAX-RS resources and providers are always supposed to be Spring beans, and they are automatically discovered|
