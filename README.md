<p align="center">
  <a href="https://github.com/AkaZver/mapstruct-plugin/actions"><img src="https://github.com/AkaZver/mapstruct-plugin/workflows/Build/badge.svg" alt="Actions Status"></a>
  <a href="https://sonarcloud.io"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=alert_status" alt="Quality Gate Status"></a>
  <a href="https://sonarcloud.io"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=coverage" alt="Coverage"></a>
  <br/>
  <a href="https://sonarcloud.io"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=security_rating" alt="Security Rating"></a>
  <a href="https://sonarcloud.io"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=reliability_rating" alt="Reliability Rating"></a>
  <a href="https://sonarcloud.io"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=sqale_rating" alt="Maintainability Rating"></a>
  <a href="https://sonarcloud.io"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=vulnerabilities" alt="Vulnerabilities"></a>
  <br/>
  <a href="#"><img src="https://img.shields.io/badge/Java-17-blue.svg?logo=intellijidea" alt="Java"></a>
  <a href="#"><img src="https://img.shields.io/badge/Gradle-9.6.1-blue.svg?logo=gradle" alt="Gradle"></a>
  <a href="#"><img src="https://img.shields.io/badge/License-Apache--2.0-blue.svg?logo=apache" alt="License"></a>
  <a href="https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_shield"><img src="https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=shield" alt="FOSSA Status"></a>
</p>

# MapStruct Gradle Plugin

Gradle plugin for easy [MapStruct](https://mapstruct.org/) setup

Usage:
```groovy
plugins {
    id 'com.github.akazver.mapstruct' version '2.0.0'
}
```

## Dependencies
**MapStruct** (required)
- [mapstruct](https://mvnrepository.com/artifact/org.mapstruct/mapstruct) (implementation)
- [mapstruct-processor](https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor) (annotationProcessor)

**Lombok** (optional)
- [lombok-mapstruct-binding](https://mvnrepository.com/artifact/org.projectlombok/lombok-mapstruct-binding) (annotationProcessor)

**Spring** (optional)
- [mapstruct-spring-annotations](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-annotations) (implementation)
- [mapstruct-spring-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-extensions) (annotationProcessor)
- [mapstruct-spring-test-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-test-extensions) (testImplementation)

**Camel** (optional)
- [camel-mapstruct](https://mvnrepository.com/artifact/org.apache.camel/camel-mapstruct) (implementation)
- [camel-mapstruct-starter](https://mvnrepository.com/artifact/org.apache.camel.springboot/camel-mapstruct-starter) (implementation)
- [camel-quarkus-mapstruct](https://mvnrepository.com/artifact/org.apache.camel.quarkus/camel-quarkus-mapstruct) (implementation)

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
    nullValueIterableMappingStrategy = 'RETURN_DEFAULT'
    nullValueMapMappingStrategy = 'RETURN_DEFAULT'
}
```

All parameters used according to official 
[documentation](https://mapstruct.org/documentation/stable/reference/html/#configuration-options)

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_large)
