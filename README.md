<p align="center">
  <a href="https://github.com/AkaZver/mapstruct-plugin/actions"><img src="https://github.com/AkaZver/mapstruct-plugin/workflows/Build/badge.svg" alt="Actions Status"></a>
  <a href="https://plugins.gradle.org/plugin/io.github.akazver.mapstruct"><img src="https://img.shields.io/maven-metadata/v.svg?metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fio%2Fgithub%2Fakazver%2Fmapstruct%2Fio.github.akazver.mapstruct.gradle.plugin%2Fmaven-metadata.xml&label=Plugin%20Version&logo=github" alt="Plugin Version"></a>
  <a href="https://sonarcloud.io/project/overview?id=AkaZver_mapstruct-plugin"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=alert_status" alt="Quality Gate Status"></a>
  <a href="https://sonarcloud.io/project/overview?id=AkaZver_mapstruct-plugin"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=coverage" alt="Coverage"></a>
  <br/>
  <a href="https://sonarcloud.io/project/overview?id=AkaZver_mapstruct-plugin"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=security_rating" alt="Security Rating"></a>
  <a href="https://sonarcloud.io/project/overview?id=AkaZver_mapstruct-plugin"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=reliability_rating" alt="Reliability Rating"></a>
  <a href="https://sonarcloud.io/project/overview?id=AkaZver_mapstruct-plugin"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=sqale_rating" alt="Maintainability Rating"></a>
  <a href="https://sonarcloud.io/project/overview?id=AkaZver_mapstruct-plugin"><img src="https://sonarcloud.io/api/project_badges/measure?project=AkaZver_mapstruct-plugin&metric=vulnerabilities" alt="Vulnerabilities"></a>
  <br/>
  <a href="#"><img src="https://img.shields.io/badge/Java-17-blue.svg?logo=intellijidea" alt="Java"></a>
  <a href="#"><img src="https://img.shields.io/badge/Gradle-9.6.1-blue.svg?logo=gradle" alt="Gradle"></a>
  <a href="#"><img src="https://img.shields.io/badge/License-Apache--2.0-blue.svg?logo=apache" alt="License"></a>
  <a href="https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_shield"><img src="https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=shield" alt="FOSSA Status"></a>
</p>

# MapStruct Gradle Plugin

Gradle plugin for easy [MapStruct](https://mapstruct.org/) setup

## Requirements

- **Gradle:** 9.0 or higher
- **Java:** 17 or higher

## Usage

```groovy
plugins {
    id 'io.github.akazver.mapstruct' version 'X.Y.Z'
}
```

## How it works

The plugin automatically:

- Adds MapStruct dependencies (`mapstruct` and `mapstruct-processor`)
- Detects optional dependencies (Lombok, Spring, Camel, Quarkus, Protobuf) in your project
- Adds required binding libraries when needed (e.g., `lombok-mapstruct-binding` for Lombok)
- Detects Kotlin and automatically applies `kapt` plugin with proper configuration
- Configures compiler arguments based on your `mapstruct {}` block settings
- Runs after project evaluation to ensure all dependencies are resolved

## Dependencies

| Category                   | GitHub                                                                                            | Maven                                                                                                                                                                   | Configuration         |
|----------------------------|---------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| **MapStruct** *(required)* | [mapstruct/mapstruct](https://github.com/mapstruct/mapstruct)                                     | [org.mapstruct/mapstruct](https://mvnrepository.com/artifact/org.mapstruct/mapstruct)                                                                                   | `implementation`      |
|                            |                                                                                                   | [org.mapstruct/mapstruct-processor](https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor)                                                               | `annotationProcessor` |
| **Lombok**                 | [projectlombok/lombok](https://github.com/projectlombok/lombok)                                   | [org.projectlombok/lombok-mapstruct-binding](https://mvnrepository.com/artifact/org.projectlombok/lombok-mapstruct-binding)                                             | `annotationProcessor` |
| **Spring**                 | [mapstruct/mapstruct-spring-extensions](https://github.com/mapstruct/mapstruct-spring-extensions) | [org.mapstruct.extensions.spring/mapstruct-spring-annotations](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-annotations)         | `implementation`      |
|                            |                                                                                                   | [org.mapstruct.extensions.spring/mapstruct-spring-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-extensions)           | `annotationProcessor` |
|                            |                                                                                                   | [org.mapstruct.extensions.spring/mapstruct-spring-test-extensions](https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-test-extensions) | `testImplementation`  |
| **Camel**                  | [apache/camel](https://github.com/apache/camel)                                                   | [org.apache.camel/camel-mapstruct](https://mvnrepository.com/artifact/org.apache.camel/camel-mapstruct)                                                                 | `implementation`      |
|                            | [apache/camel-spring-boot](https://github.com/apache/camel-spring-boot)                           | [org.apache.camel.springboot/camel-mapstruct-starter](https://mvnrepository.com/artifact/org.apache.camel.springboot/camel-mapstruct-starter)                           | `implementation`      |
|                            | [apache/camel-quarkus](https://github.com/apache/camel-quarkus)                                   | [org.apache.camel.quarkus/camel-quarkus-mapstruct](https://mvnrepository.com/artifact/org.apache.camel.quarkus/camel-quarkus-mapstruct)                                 | `implementation`      |
| **Quarkus**                | [quarkiverse/quarkus-mapstruct](https://github.com/quarkiverse/quarkus-mapstruct)                 | [io.quarkiverse.mapstruct/quarkus-mapstruct](https://mvnrepository.com/artifact/io.quarkiverse.mapstruct/quarkus-mapstruct)                                             | `implementation`      |
| **Protobuf**               | [entur/mapstruct-spi-protobuf](https://github.com/entur/mapstruct-spi-protobuf)                   | [no.entur.mapstruct.spi/protobuf-spi-impl](https://mvnrepository.com/artifact/no.entur.mapstruct.spi/protobuf-spi-impl)                                                 | `implementation`      |

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

## Troubleshooting

### MapStruct not generating mappers

Make sure you have the Java plugin applied and that annotation processing is enabled in your IDE.

### Lombok + MapStruct conflicts

The plugin automatically adds `lombok-mapstruct-binding` when Lombok is detected. If you still have issues, ensure
Lombok plugin is applied before MapStruct plugin.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_large)
