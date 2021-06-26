package com.github.akazver.gradle.plugins.mapstruct;

/**
 * Dependency configuration types
 */
public enum ConfigurationType {

    ANNOTATION_PROCESSOR("annotationProcessor"),
    IMPLEMENTATION("implementation");

    private final String name;

    ConfigurationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
