package com.github.akazver.gradle.plugins.mapstruct;

/**
 * Description for all plugin dependencies
 */
@SuppressWarnings({"unused", "java:S1192"})
public enum PluginDependency {

    LOMBOK(
            "annotationProcessor",
            "org.projectlombok",
            "lombok",
            "1.18.22"
    ),

    LOMBOK_MAPSTRUCT_BINDING(
            "annotationProcessor",
            "org.projectlombok",
            "lombok-mapstruct-binding",
            "0.2.0"
    ),

    MAPSTRUCT(
            "implementation",
            "org.mapstruct",
            "mapstruct",
            "1.4.2.Final"
    ),

    MAPSTRUCT_PROCESSOR(
            "annotationProcessor",
            "org.mapstruct",
            "mapstruct-processor",
            "1.4.2.Final"
    ),

    MAPSTRUCT_SPRING_EXTENSIONS(
            "implementation",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-extensions",
            "0.1.1"
    );

    private final String configuration;
    private final String group;
    private final String name;
    private final String version;
    private final String id;

    PluginDependency(String configuration, String group, String name, String version) {
        this.configuration = configuration;
        this.group = group;
        this.name = name;
        this.version = version;
        this.id = String.join(":", group, name, version);
    }

    public String getConfiguration() {
        return configuration;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getId() {
        return id;
    }

}
