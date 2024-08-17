package com.github.akazver.gradle.plugins.mapstruct.dependency;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Marker dependencies for different frameworks
 *
 * @author Vasiliy Sobolev
 */
@Getter
@ToString
@SuppressWarnings("java:S1192")
@EqualsAndHashCode(callSuper = true)
public class MarkerDependency extends PluginDependency {

    public static final MarkerDependency LOMBOK =
            new MarkerDependency("annotationProcessor", "org.projectlombok:lombok:1.18.32");

    public static final MarkerDependency SPRING_CORE =
            new MarkerDependency("implementation", "org.springframework:spring-core:6.1.12");

    public static final MarkerDependency SPRING_BOOT =
            new MarkerDependency("implementation", "org.springframework.boot:spring-boot:3.3.2");

    public static final MarkerDependency CAMEL_CORE =
            new MarkerDependency("implementation", "org.apache.camel:camel-core:4.7.0");

    public static final MarkerDependency QUARKUS_CORE =
            new MarkerDependency("implementation", "io.quarkus:quarkus-core:3.13.2");

    public MarkerDependency(String configuration, String id) {
        super(configuration, id);
    }

}
