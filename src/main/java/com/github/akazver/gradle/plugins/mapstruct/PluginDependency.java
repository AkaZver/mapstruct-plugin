package com.github.akazver.gradle.plugins.mapstruct;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Description for all plugin dependencies
 *
 * @author Vasiliy Sobolev
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
            "1.18.32"
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
            "1.6.0"
    );

    public static final PluginDependency MAPSTRUCT_PROCESSOR = new PluginDependency(
            "annotationProcessor",
            "org.mapstruct",
            "mapstruct-processor",
            "1.6.0"
    );

    public static final PluginDependency MAPSTRUCT_SPRING_EXTENSIONS = new PluginDependency(
            "annotationProcessor",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-extensions",
            "1.1.1"
    );

    public static final PluginDependency MAPSTRUCT_SPRING_ANNOTATIONS = new PluginDependency(
            "implementation",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-annotations",
            "1.1.1"
    );

    public static final PluginDependency MAPSTRUCT_SPRING_TEST_EXTENSIONS = new PluginDependency(
            "testImplementation",
            "org.mapstruct.extensions.spring",
            "mapstruct-spring-test-extensions",
            "1.1.1"
    );

    public static final PluginDependency SPRING_CORE = new PluginDependency(
            "implementation",
            "org.springframework",
            "spring-core",
            "6.1.12"
    );

    protected static final List<PluginDependency> MAPSTRUCT_DEPENDENCIES = new ArrayList<>();
    protected static final List<PluginDependency> LOMBOK_DEPENDENCIES = new ArrayList<>();
    protected static final List<PluginDependency> SPRING_DEPENDENCIES = new ArrayList<>();

    static {
        MAPSTRUCT_DEPENDENCIES.add(MAPSTRUCT);
        MAPSTRUCT_DEPENDENCIES.add(MAPSTRUCT_PROCESSOR);

        LOMBOK_DEPENDENCIES.add(LOMBOK_MAPSTRUCT_BINDING);

        SPRING_DEPENDENCIES.add(MAPSTRUCT_SPRING_EXTENSIONS);
        SPRING_DEPENDENCIES.add(MAPSTRUCT_SPRING_ANNOTATIONS);
        SPRING_DEPENDENCIES.add(MAPSTRUCT_SPRING_TEST_EXTENSIONS);
    }

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
            String message = String.format("Dependency id '%s' is invalid", id);
            throw new MapstructPluginException(message);
        }

        this.group = split[0];
        this.name = split[1];
        this.version = split[2];
    }

}
