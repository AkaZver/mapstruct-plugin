package io.github.akazver.gradle.plugins.mapstruct.dependency;

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

    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    public static final MarkerDependency LOMBOK =
            new MarkerDependency("annotationProcessor", "org.projectlombok:lombok:1.18.44");

    // https://mvnrepository.com/artifact/org.springframework/spring-core
    public static final MarkerDependency SPRING_CORE =
            new MarkerDependency("implementation", "org.springframework:spring-core:7.0.8");

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot
    public static final MarkerDependency SPRING_BOOT =
            new MarkerDependency("implementation", "org.springframework.boot:spring-boot:4.1.0");

    // https://mvnrepository.com/artifact/org.apache.camel/camel-core
    public static final MarkerDependency CAMEL_CORE =
            new MarkerDependency("implementation", "org.apache.camel:camel-core:4.21.0");

    // https://mvnrepository.com/artifact/io.quarkus/quarkus-core
    public static final MarkerDependency QUARKUS_CORE =
            new MarkerDependency("implementation", "io.quarkus:quarkus-core:3.38.0");

    // https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java
    public static final MarkerDependency PROTOBUF_JAVA =
            new MarkerDependency("implementation", "com.google.protobuf:protobuf-java:4.35.1");

    public MarkerDependency(String configuration, String id) {
        super(configuration, id);
    }

}
