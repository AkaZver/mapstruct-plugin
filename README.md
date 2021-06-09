# Mapstruct Gradle Plugin
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_shield)


Gradle plugin for easy [mapstruct](https://mapstruct.org/) setup

Installation:
```groovy
plugins {
    id 'ru.a_z.mapstruct' version '1.0.0'
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
    unmappedTargetPolicy = 'INFO'
}
```

All parameters used according to official 
[documentation](https://mapstruct.org/documentation/stable/reference/html/#configuration-options)


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FAkaZver%2Fmapstruct-plugin?ref=badge_large)