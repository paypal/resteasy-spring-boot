[![Build Status](https://travis-ci.org/paypal/resteasy-spring-boot.svg?branch=master)](https://travis-ci.org/paypal/resteasy-spring-boot)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/4d23b74b13c3464b95f1acdb40b35cd7)](https://www.codacy.com/app/fabiocarvalho777/resteasy-spring-boot)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.paypal.springboot/resteasy-spring-boot-starter/badge.svg?style=flat)](http://search.maven.org/#search|ga|1|g:com.paypal.springboot)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# RESTEasy Spring Boot Starter

There was no RESTEasy Spring Boot starter out there, so PayPal team decided to create one and share it with the community.<br>

This Spring Boot starter is fully functional, has ZERO PayPal specific code on it, and can be used normally by any regular Spring Boot application that wants to have REST endpoints and prefers RESTEasy as the JAX-RS implementation.

Also, this RESTEasy Spring Boot starter integrates with Spring as expected, which means every JAX-RS REST resource that is also a Spring bean will be automatically auto-scanned, integrated, and available.

## Features
* Enable RESTEasy and Spring integration for Spring Boot applications
* Support Spring Boot application execution from `mvn spring-boot:run`
* Support JAX-RS providers, resources and sub-resources
* Support automatic discovery of [JAX-RS Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html) classes as Spring beans, via class-path scanning, or via manual registration
* Support automatic discovery of JAX-RS providers and resources Spring beans 

## Usage

#### Adding POM dependency
Just add the Maven dependency below to your Spring Boot application pom file.<br>

```
<dependency>
	<groupId>com.paypal.springboot</groupId>
	<artifactId>resteasy-spring-boot-starter</artifactId>
	<version>2.1.0-SNAPSHOT</version>
	<scope>runtime</scope>
</dependency>
```

#### Registering JAX-RS application classes
JAX-RS applications are defined via sub-classes of [Application](http://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html). There are three different methods to find and register them:

1. By having them defined as Spring beans.
2. By setting property `resteasy.jaxrs.app` via Spring Boot application properties file. This property should contain a comma separated list of JAX-RS sub-classes.
3. Automatically by classpath scanning (looking for `javax.ws.rs.core.Application` sub-classes).

You can define the method you prefer by setting property `resteasy.jaxrs.app.definition` (via Spring Boot configuration file), although you don't have to, in that case the `auto` method is the default. The possible values are:

1. `beans`
1. `property`
1. `scanning`
1. `auto` (default)

The first three values refer respectively to each one of the three methods described earlier. The last one, `auto`, when set (or when property `resteasy.jaxrs.app.definition` is not present), attempts first to find JAX-RS application classes by searching them as Spring beans. If any is found, the search stops, and those are the only JAX-RS applications to be registered. If no JAX-RS application Spring beans are found, then the `property` approach is tried. If still no JAX-RS application classes could be found, then the last method, `scanning`, is attempted. If after that still no JAX-RS application class could be registered, then the Spring Boot application won't be able to serve any request.

#### Registering JAX-RS resources and providers
Just define them as Spring beans, and they will be automatically registered.
Notice that JAX-RS resources can be singleton or request scoped, while JAX-RS providers must be singletons.

## Release notes
[RESTEasy Spring Boot starter release notes](./RELEASE_NOTES.md).

## Projects

##### sample-app
A simple Spring Boot application that exposes JAX-RS endpoints as Spring beans using RESTEasy via this RESTEasy Spring Boot starter.

##### resteasy-spring-boot-starter
The RESTEasy Spring Boot Starter project.

## Reporting an issue
Please open an issue using our [GitHub issues](https://github.com/paypal/resteasy-spring-boot/issues) page.

## Contributing
You are very welcome to contribute to RESTEasy Spring Boot starter! Read our [Contribution guidelines](./CONTRIBUTING.md).

## Contacting us
To contact us, please send an email to fabiocarvalho777@gmail.com.

##License
This project is licensed under the [Apache 2 License](License.html).
