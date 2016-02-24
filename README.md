[![Build Status](https://travis-ci.org/paypal/resteasy-spring-boot.svg?branch=master)](https://travis-ci.org/paypal/resteasy-spring-boot)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/4d23b74b13c3464b95f1acdb40b35cd7)](https://www.codacy.com/app/fabiocarvalho777/resteasy-spring-boot)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.paypal.springboot/spring-boot-starter-resteasy/badge.svg?style=flat)](http://search.maven.org/#search|ga|1|g:com.paypal.springboot)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

# RESTEasy Spring Boot Starter

There was no RESTEasy Spring Boot starter out there, so PayPal team decided to create one and share it with the community.<br>

This starter is fully functional and has good code coverage. It has ZERO PayPal specific code, and can be used normally by any regular Spring Boot application that wants to have REST endpoints chosing RESTEasy as the JAX-RS implementation.

Also, this RESTEasy Spring Boot starter integrates to Spring as expected, which means every JAX-RS REST resource that is also a Spring bean will be automatically available.

See the list of issues for any known current limitation and/or pending issues.

## Features
* Enable RESTEasy and Spring integration for Spring Boot applications
* Support JAX-RS sub-resources
* Support automatic discovery of [JAX-RS Application](https://docs.oracle.com/javaee/7/api/javax/ws/rs/core/Application.html) classes

## Usage
Just add the Maven dependency below to your Spring Boot application pom file.<br>

```
<dependency>
	<groupId>com.paypal.springboot</groupId>
	<artifactId>spring-boot-starter-resteasy</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Releasing plan
RESTEasy Spring Boot starter is going through final tests, and its first RELEASE version should be available around March 2016.

## Projects

##### sample-app
A simple Spring Boot application that exposes JAX-RS endpoints as Spring beans using RESTEasy via this RESTEasy Spring Boot starter

##### spring-boot-starter-resteasy
The RESTEasy Spring Boot Starter project

## Reporting an issue
Please open an issue in using our [GitHub issues](https://github.com/paypal/resteasy-spring-boot/issues) page

## Contributing
You are very welcome to contribute to RESTEasy Spring Boot starter! Read our [Contribution guidelines](./CONTRIBUTING.md).

##License
This project is licensed under the [Apache 2 License](License.html).
