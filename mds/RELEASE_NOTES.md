# Release notes

## 2.3.2-RELEASE

#### Release date
June 5th, 2017.

#### Third-party versions
- RESTEasy: 3.1.3.Final
- Spring Boot: 1.5.3.RELEASE

#### New features and enhancements
1. [61 - Adding support for RESTEasy Asynchronous Job Service](https://github.com/paypal/resteasy-spring-boot/issues/61)
1. [65 - Use Spring Framework scanning facility in JaxrsApplicationScanner](https://github.com/paypal/resteasy-spring-boot/issues/65)
1. [72 - Upgrade RESTEasy to version 3.1.3.Final](https://github.com/paypal/resteasy-spring-boot/issues/72)

#### Bug fixes
None

#### Important notes
1. Starting on version 3.0.0, the behavior of the `scanning` JAX-RS Application subclass registration method will change, being more restrictive. Instead of scanning the whole classpath, it will scan only packages registered to be scanned by Spring framework (regardless of the JAX-RS Application subclass being a Spring bean or not). The reason is to improve application startup performance. Having said that, it is recommended that every application use any method, other than `scanning`. Or, if using `scanning`, make sure your JAX-RS Application subclass is under a package to be scanned by Spring framework. If not, starting on version 3.0.0,it won't be found.

## 2.3.1-RELEASE

#### Release date
May 12th, 2017.

#### Third-party versions
- RESTEasy: 3.1.0.Final
- Spring Boot: 1.5.3.RELEASE

#### New features and enhancements
1. [66 - Upgrade to Spring Boot 1.5.3.RELEASE](https://github.com/paypal/resteasy-spring-boot/issues/66)
1. [64 - Renaming `ResteasySpringBootConfig` to `RestEasyAutoConfiguration`](https://github.com/paypal/resteasy-spring-boot/issues/64)
1. [54 - Add a test case to integration tests to reproduce issue with multiple applications](https://github.com/paypal/resteasy-spring-boot/issues/54)
1. [57 - Add a test case to check for warning or error messages](https://github.com/paypal/resteasy-spring-boot/issues/57)

#### Bug fixes
1. [67 - Remove all warnings during build](https://github.com/paypal/resteasy-spring-boot/issues/67)

## 2.3.0-RELEASE

#### Release date
February 22nd, 2017.

#### Third-party versions
- RESTEasy: 3.1.0.Final
- Spring Boot: 1.5.1.RELEASE

#### New features and enhancements
1. [49 - Upgrade to RESTEasy version 3.1.0](https://github.com/paypal/resteasy-spring-boot/issues/49)
1. [50 - Upgrade to Spring Boot version 1.5.1](https://github.com/paypal/resteasy-spring-boot/issues/50)

#### Bug fixes
1. [56 - Warning messages during startup](https://github.com/paypal/resteasy-spring-boot/issues/56)

## 2.2.2-RELEASE

#### Release date
February 1st, 2017.

#### Third-party versions
- RESTEasy: 3.0.19.Final
- Spring Boot: 1.4.2.RELEASE

#### New features and enhancements
1. [13 - Add automated integration tests](https://github.com/paypal/resteasy-spring-boot/issues/13)

#### Bug fixes
1. [51 - Do not share ResteasyProviderFactory and Registry among applications](https://github.com/paypal/resteasy-spring-boot/issues/51)
1. [44 - Fix CI travis jpm usage](https://github.com/paypal/resteasy-spring-boot/issues/44)

## 2.2.1-RELEASE

#### Release date
November 20th, 2016.

#### Third-party versions
- RESTEasy: 3.0.19.Final
- Spring Boot: 1.4.2.RELEASE

#### New features and enhancements
1. [43 - Upgrade Spring Boot to version 1.4.2](https://github.com/paypal/resteasy-spring-boot/issues/43)
1. [40 - Spring 4.1 is not supported although it easily can be](https://github.com/paypal/resteasy-spring-boot/issues/40)

#### Bug fixes
1. [42 - Remove deprecated code](https://github.com/paypal/resteasy-spring-boot/issues/42)

## 2.2.0-RELEASE

#### Release date
August 31st, 2016.

#### Third-party versions
- RESTEasy: 3.0.19.Final
- Spring Boot: 1.4.0.RELEASE

#### New features and enhancements
1. [33 - Upgrade to the latest Spring Boot and the RESTEasy versions](https://github.com/paypal/resteasy-spring-boot/issues/33)

#### Bug fixes
1. [35 - Not possible to use application property in application.yml](https://github.com/paypal/resteasy-spring-boot/issues/35)
1. [34 - Mockito should be set to test scope](https://github.com/paypal/resteasy-spring-boot/issues/34)
1. [32 - Documentation has a contradiction about registering Application subclasses](https://github.com/paypal/resteasy-spring-boot/issues/32)

## 2.1.1-RELEASE

#### Release date
July 22nd, 2016.

#### Third-party versions
- RESTEasy: 3.0.18.Final
- Spring Boot: 1.3.6.RELEASE

#### New features and enhancements
None

#### Bug fixes

1. [23 - JAX-RS application fails to register after upgrade to 2.1.0](https://github.com/paypal/resteasy-spring-boot/issues/30)

## 2.1.0-RELEASE

#### Release date
July 21st, 2016.

#### Third-party versions
- RESTEasy: 3.0.18.Final
- Spring Boot: 1.3.6.RELEASE

#### New features and enhancements

1. [22 -  Add configuration to turn OFF scanning for JAX-RS Application sub-classes](https://github.com/paypal/resteasy-spring-boot/issues/22)
1. [10 -  Support RESTEasy configuration](https://github.com/paypal/resteasy-spring-boot/issues/10)
1. [20 -  Add documentation](https://github.com/paypal/resteasy-spring-boot/issues/20)
1. [25 -  Add Cobertura code coverage](https://github.com/paypal/resteasy-spring-boot/issues/25)
1. [24 -  Automate Travis CI](https://github.com/paypal/resteasy-spring-boot/issues/24)

#### Bug fixes

1. [23 -  Make sure section 2.3.2 in JAX-RS 2.0 spec is followed strictly in regard to the absence of JAX-RS Application classes](https://github.com/paypal/resteasy-spring-boot/issues/23)

## 2.0.1-RELEASE

#### Release date
June 9th, 2016.

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