[![Actions Status](https://github.com/AkaZver/mapstruct-plugin/workflows/Main/badge.svg)](https://github.com/AkaZver/mapstruct-plugin/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=security_rating)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_shield)

# MapStruct Gradle Plugin

Gradle plugin for easy [MapStruct](https://mapstruct.org/) setup

Usage:
```groovy
plugins {
    id 'com.github.akazver.mapstruct' version '1.0.6'
}
```

## Dependencies
**MapStruct** _(required)_
- [org.mapstruct:mapstruct](https://mvnrepository.com/artifact/org.mapstruct/mapstruct) _(implementation)_
- [org.mapstruct:mapstruct-processor](https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor) _(annotationProcessor)_

**Lombok** _(optional)_
- [org.projectlombok:lombok-mapstruct-binding](https://mvnrepository.com/artifact/org.projectlombok/lombok-mapstruct-binding) _(annotationProcessor)_

**Spring** _(optional)_
- [org.mapstruct.extensions.spring:mapstruct-spring-annotations](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-annotations) _(implementation)_
- [org.mapstruct.extensions.spring:mapstruct-spring-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-extensions) _(annotationProcessor)_
- [org.mapstruct.extensions.spring:mapstruct-spring-test-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-test-extensions) _(testImplementation)_

## Config
Plugin adds configuration block which looks like this:
```groovy
mapstruct {
    suppressGeneratorTimestamp = true
    verbose = true
    suppressGeneratorVersionInfoComment = true
    defaultComponentModel = 'spring'
    defaultInjectionStrategy = 'constructor'
    unmappedTargetPolicy = 'ERROR'
    unmappedSourcePolicy = 'ERROR'
    disableBuilders = true
}
```

All parameters used according to official 
[documentation](https://mapstruct.org/documentation/stable/reference/html/#configuration-options)

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_large)
