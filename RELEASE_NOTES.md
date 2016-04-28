# Release notes

## 2.0.0-RELEASE

#### Important note
The starter artifact id is being renamed to **resteasy-spring-boot-starter**, due to [issue 2](https://github.com/paypal/resteasy-spring-boot/issues/2).

#### Release date
April 27th, 2016.

#### Third-party versions
- RESTEasy: 3.0.16.Final
- Spring Boot: 1.3.3.RELEASE

#### New features and enhancements
- [2 - Renaming artifact id due to Spring team request](https://github.com/paypal/resteasy-spring-boot/issues/2)
- [4 - Replace ComponentScan by Import annotations](https://github.com/paypal/resteasy-spring-boot/issues/4) 

#### Bug fixes
- [5 - Classpath scanner prints warning when a non jar file is scanned](https://github.com/paypal/resteasy-spring-boot/issues/5)
- [6 - JAX-RS application and endpoints don't get registered when running mvn spring-boot:run](https://github.com/paypal/resteasy-spring-boot/issues/6)
- [8 - Don't skip all applications if one has no path bug ](https://github.com/paypal/resteasy-spring-boot/issues/8)

## 1.0.0-RELEASE

#### Release date
February 26th, 2016.

#### Third-party versions
- RESTEasy: 3.0.16.Final
- Spring Boot: 1.3.2.RELEASE

#### New features and enhancements
- Enable RESTEasy and Spring integration for Spring Boot applications
- Support JAX-RS sub-resources
- Support automatic discovery of JAX-RS Application classes

#### Bug fixes
- None