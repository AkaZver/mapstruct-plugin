[![Actions Status](https://github.com/AkaZver/mapstruct-plugin/workflows/Main/badge.svg)](https://github.com/AkaZver/mapstruct-plugin/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=security_rating)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=AkaZver_mapstruct-plugin)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_shield)

# Mapstruct Gradle Plugin

Gradle plugin for easy [mapstruct](https://mapstruct.org/) setup

Installation:
```groovy
plugins {
    id 'com.github.akazver.mapstruct' version '1.0.4'
}
```

## Dependencies
Plugin will add [mapstruct](https://mvnrepository.com/artifact/org.mapstruct/mapstruct) 
with [mapstruct-processor](https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor) by default

If you are using [lombok](https://projectlombok.org/) it will add 
[lombok-mapstruct-binding](https://mvnrepository.com/artifact/org.projectlombok/lombok-mapstruct-binding), 
if [spring](https://spring.io/) - 
[mapstruct-spring-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-extensions)

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
