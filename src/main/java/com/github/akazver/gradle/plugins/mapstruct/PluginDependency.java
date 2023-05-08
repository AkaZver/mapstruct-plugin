package com.github.akazver.gradle.plugins.mapstruct;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Description for all plugin dependencies
 */
@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("java:S1192")
public class PluginDependency {

    public static final PluginDependency LOMBOK = new PluginDependency(
            "annotationProcessor",
            "org.projectlombok",
            "lombok",
            "1.18.26"
    );

    public static final PluginDependency LOMBOK_MAPSTRUCT_BINDING = new PluginDependency(
            "annotationProcessor",
            "org.projectlombok",
            "lombok-mapstruct-binding",
            "0.2.0"
    );

    public static final PluginDependency MAPSTRUCT = new PluginDependency(
            "implementation",
            "org.mapstruct",
            "mapstruct",
            "1.5.5.Final"
    );

    public static final PluginDependency MAPSTRUCT_PROCESSOR = new PluginDependency(
            "annotationProcessor",
            "org.mapstruct",
            "mapstruct-processor",
            "1.5.5.Final"
    );

    public static final PluginDependency MAPSTRUCT_SPRING_EXTENSIONS = new PluginDependency(
            "annotationProcessor",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-extensions",
            "1.0.1"
    );

    public static final PluginDependency MAPSTRUCT_SPRING_ANNOTATIONS = new PluginDependency(
            "implementation",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-annotations",
            "1.0.1"
    );

    private final String configuration;
    private final String group;
    private final String name;
    private final String version;
    private final String id;

    public PluginDependency(String configuration, String group, String name, String version) {
        this.configuration = configuration;
        this.group = group;
        this.name = name;
        this.version = version;
        this.id = String.join(":", group, name, version);
    }

    public PluginDependency(String configuration, String id) {
        this.configuration = configuration;
        this.id = id;

        String[] split = id.split(":");

        if (split.length != 3) {
            throw new IllegalStateException("Dependency id is invalid: " + id);
        }

        this.group = split[0];
        this.name = split[1];
        this.version = split[2];
    }

}
