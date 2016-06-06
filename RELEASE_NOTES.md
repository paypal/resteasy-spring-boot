# Release notes

## 2.0.1-RELEASE

#### Release date
TBD

#### Third-party versions
- RESTEasy: 3.0.17.Final
- Spring Boot: 1.3.5.RELEASE

#### New features and enhancements

1. [18 - Component annotation removal from ResteasyEmbeddedServletInitializer](https://github.com/paypal/resteasy-spring-boot/issues/18)
1. [17 - Upgrade Sprig Boot to version 1.3.5](https://github.com/paypal/resteasy-spring-boot/issues/17)
1. [16 - Upgrade RESTEasy to version 3.0.17.Final](https://github.com/paypal/resteasy-spring-boot/issues/16)
1. [7 - Remove custom RESTEasy classes as soon as RESTEasy 3.0.17.Final is released](https://github.com/paypal/resteasy-spring-boot/issues/7)

#### Bug fixes

1. [19 - JAX-RS resources resolution should be targeted towards Spring beans only](https://github.com/paypal/resteasy-spring-boot/issues/19)

## 2.0.0-RELEASE

#### Important note
The starter artifact id is being renamed to **resteasy-spring-boot-starter**, due to [issue 2](https://github.com/paypal/resteasy-spring-boot/issues/2).

#### Release date
April 27th, 2016.

#### Third-party versions
- RESTEasy: 3.0.16.Final
- Spring Boot: 1.3.3.RELEASE

#### New features and enhancements

1. [2 - Renaming artifact id due to Spring team request](https://github.com/paypal/resteasy-spring-boot/issues/2)
1. [4 - Replace ComponentScan by Import annotations](https://github.com/paypal/resteasy-spring-boot/issues/4) 

#### Bug fixes

1. [5 - Classpath scanner prints warning when a non jar file is scanned](https://github.com/paypal/resteasy-spring-boot/issues/5)
1. [6 - JAX-RS application and endpoints don't get registered when running mvn spring-boot:run](https://github.com/paypal/resteasy-spring-boot/issues/6)
1. [8 - Don't skip all applications if one has no path bug ](https://github.com/paypal/resteasy-spring-boot/issues/8)

## 1.0.0-RELEASE

#### Release date
February 26th, 2016.

#### Third-party versions
- RESTEasy: 3.0.16.Final
- Spring Boot: 1.3.2.RELEASE

#### New features and enhancements

1. Enable RESTEasy and Spring integration for Spring Boot applications
1. Support JAX-RS sub-resources
1. Support automatic discovery of JAX-RS Application classes

#### Bug fixes
None