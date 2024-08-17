package com.github.akazver.gradle.plugins.mapstruct.dependency;

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

    public static final AdditionalDependency MAPSTRUCT =
            new AdditionalDependency("implementation", "org.mapstruct:mapstruct:1.6.0");

    public static final AdditionalDependency MAPSTRUCT_PROCESSOR =
            new AdditionalDependency("annotationProcessor", "org.mapstruct:mapstruct-processor:1.6.0");

    public static final AdditionalDependency LOMBOK_MAPSTRUCT_BINDING =
            new AdditionalDependency("annotationProcessor", "org.projectlombok:lombok-mapstruct-binding:0.2.0");

    public static final AdditionalDependency MAPSTRUCT_SPRING_EXTENSIONS =
            new AdditionalDependency("annotationProcessor", "org.mapstruct.extensions.spring:mapstruct-spring-extensions:1.1.1");

    public static final AdditionalDependency MAPSTRUCT_SPRING_ANNOTATIONS =
            new AdditionalDependency("implementation", "org.mapstruct.extensions.spring:mapstruct-spring-annotations:1.1.1");

    public static final AdditionalDependency MAPSTRUCT_SPRING_TEST_EXTENSIONS =
            new AdditionalDependency("testImplementation", "org.mapstruct.extensions.spring:mapstruct-spring-test-extensions:1.1.1");

    public static final AdditionalDependency CAMEL_MAPSTRUCT =
            new AdditionalDependency("implementation", "org.apache.camel:camel-mapstruct:4.7.0");

    public static final AdditionalDependency CAMEL_MAPSTRUCT_STARTER =
            new AdditionalDependency("implementation", "org.apache.camel.springboot:camel-mapstruct-starter:4.7.0");

    public static final AdditionalDependency CAMEL_QUARKUS_MAPSTRUCT =
            new AdditionalDependency("implementation", "org.apache.camel.quarkus:camel-quarkus-mapstruct:3.13.0");

    public AdditionalDependency(String configuration, String id) {
        super(configuration, id);
    }

}
