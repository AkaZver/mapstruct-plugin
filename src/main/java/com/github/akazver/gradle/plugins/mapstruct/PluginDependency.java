package com.github.akazver.gradle.plugins.mapstruct;

import lombok.Getter;

/**
 * Description for all plugin dependencies
 */
@Getter
@SuppressWarnings("java:S1192")
public class PluginDependency {

    public static final PluginDependency LOMBOK = new PluginDependency(
            "annotationProcessor",
            "org.projectlombok",
            "lombok",
            "1.18.24"
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
            "1.5.3.Final"
    );

    public static final PluginDependency MAPSTRUCT_PROCESSOR = new PluginDependency(
            "annotationProcessor",
            "org.mapstruct",
            "mapstruct-processor",
            "1.5.3.Final"
    );

    public static final PluginDependency MAPSTRUCT_SPRING_EXTENSIONS = new PluginDependency(
            "implementation",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-extensions",
            "0.1.2"
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

    @Override
    public String toString() {
        return String.format("PluginDependency{configuration='%s', id='%s'}", configuration, id);
    }

}
