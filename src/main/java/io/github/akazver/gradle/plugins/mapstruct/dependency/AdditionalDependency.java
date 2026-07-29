package io.github.akazver.gradle.plugins.mapstruct.dependency;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Dependencies added by the plugin
 *
 * @author Vasiliy Sobolev
 */
@Getter
@ToString
@SuppressWarnings("java:S1192")
@EqualsAndHashCode(callSuper = true)
public class AdditionalDependency extends PluginDependency {

    // https://mvnrepository.com/artifact/org.mapstruct/mapstruct
    public static final AdditionalDependency MAPSTRUCT =
            new AdditionalDependency("implementation", "org.mapstruct:mapstruct:1.6.3");

    // https://mvnrepository.com/artifact/org.mapstruct/mapstruct-processor
    public static final AdditionalDependency MAPSTRUCT_PROCESSOR =
            new AdditionalDependency("annotationProcessor", "org.mapstruct:mapstruct-processor:1.6.3");

    // https://mvnrepository.com/artifact/org.projectlombok/lombok-mapstruct-binding
    public static final AdditionalDependency LOMBOK_MAPSTRUCT_BINDING =
            new AdditionalDependency("annotationProcessor", "org.projectlombok:lombok-mapstruct-binding:0.2.0");

    // https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-extensions
    public static final AdditionalDependency MAPSTRUCT_SPRING_EXTENSIONS =
            new AdditionalDependency("annotationProcessor", "org.mapstruct.extensions.spring:mapstruct-spring-extensions:2.0.0");

    // https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-annotations
    public static final AdditionalDependency MAPSTRUCT_SPRING_ANNOTATIONS =
            new AdditionalDependency("implementation", "org.mapstruct.extensions.spring:mapstruct-spring-annotations:2.0.0");

    // https://mvnrepository.com/artifact/org.mapstruct.extensions.spring/mapstruct-spring-test-extensions
    public static final AdditionalDependency MAPSTRUCT_SPRING_TEST_EXTENSIONS =
            new AdditionalDependency("testImplementation", "org.mapstruct.extensions.spring:mapstruct-spring-test-extensions:2.0.0");

    // https://mvnrepository.com/artifact/org.apache.camel/camel-mapstruct
    public static final AdditionalDependency CAMEL_MAPSTRUCT =
            new AdditionalDependency("implementation", "org.apache.camel:camel-mapstruct:4.21.0");

    // https://mvnrepository.com/artifact/org.apache.camel.springboot/camel-mapstruct-starter
    public static final AdditionalDependency CAMEL_MAPSTRUCT_STARTER =
            new AdditionalDependency("implementation", "org.apache.camel.springboot:camel-mapstruct-starter:4.21.0");

    // https://mvnrepository.com/artifact/org.apache.camel.quarkus/camel-quarkus-mapstruct
    public static final AdditionalDependency CAMEL_QUARKUS_MAPSTRUCT =
            new AdditionalDependency("implementation", "org.apache.camel.quarkus:camel-quarkus-mapstruct:3.36.0");

    // https://mvnrepository.com/artifact/io.quarkiverse.mapstruct/quarkus-mapstruct
    public static final AdditionalDependency QUARKUS_MAPSTRUCT =
            new AdditionalDependency("implementation", "io.quarkiverse.mapstruct:quarkus-mapstruct:1.1.0");

    // https://mvnrepository.com/artifact/no.entur.mapstruct.spi/protobuf-spi-impl
    public static final AdditionalDependency PROTOBUF_SPI_IMPL =
            new AdditionalDependency("implementation", "no.entur.mapstruct.spi:protobuf-spi-impl:1.62.0");

    public AdditionalDependency(String configuration, String id) {
        super(configuration, id);
    }

}
