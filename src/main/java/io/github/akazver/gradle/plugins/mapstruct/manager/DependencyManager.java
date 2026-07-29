package io.github.akazver.gradle.plugins.mapstruct.manager;

import io.github.akazver.gradle.plugins.mapstruct.dependency.PluginDependency;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.PluginManager;

import static io.github.akazver.gradle.plugins.mapstruct.dependency.AdditionalDependency.*;
import static io.github.akazver.gradle.plugins.mapstruct.dependency.MarkerDependency.*;

/**
 * Manages required and optional dependencies addition
 *
 * @author Vasiliy Sobolev
 */
public class DependencyManager {

    private static final Logger LOGGER = Logging.getLogger(DependencyManager.class);

    private static final String ADDING_MESSAGE = "Adding {} dependencies";
    private static final String DEPENDENCY_PREFIX = "- {}";

    private final ConfigurationContainer configurations;
    private final ExtensionContainer extensions;
    private final DependencyHandler dependencies;
    private final PluginManager pluginManager;

    public DependencyManager(Project project) {
        this.configurations = project.getConfigurations();
        this.extensions = project.getExtensions();
        this.dependencies = project.getDependencies();
        this.pluginManager = project.getPluginManager();
    }

    public void addDependencies() {
        boolean hasKotlin = hasExtension("kotlin");
        String processorConfig = hasKotlin ? "kapt" : "annotationProcessor";

        if (hasKotlin) {
            pluginManager.apply("org.jetbrains.kotlin.kapt");
        }

        addRequiredDependencies(processorConfig);
        addOptionalDependencies(processorConfig);
    }

    private void addRequiredDependencies(String processorConfig) {
        LOGGER.lifecycle(ADDING_MESSAGE, "MapStruct");
        addDependency(MAPSTRUCT);
        addDependency(MAPSTRUCT_PROCESSOR, processorConfig);
    }

    private void addOptionalDependencies(String processorConfig) {
        boolean hasLombok = hasExtension("lombok") || hasDependency(LOMBOK);
        boolean hasBinding = hasDependency(LOMBOK_MAPSTRUCT_BINDING);
        boolean hasSpringBoot = hasExtension("springBoot") || hasDependency(SPRING_BOOT);
        boolean hasSpring = hasDependency(SPRING_CORE);
        boolean hasCamel = hasDependency(CAMEL_CORE);
        boolean hasQuarkus = hasExtension("quarkus") || hasDependency(QUARKUS_CORE);
        boolean hasProtobuf = hasExtension("protobuf") || hasDependency(PROTOBUF_JAVA);

        if (hasLombok && !hasBinding) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Lombok");
            addDependency(LOMBOK_MAPSTRUCT_BINDING, processorConfig);
        }

        if (hasSpringBoot || hasSpring) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Spring");
            addDependency(MAPSTRUCT_SPRING_EXTENSIONS, processorConfig);
            addDependency(MAPSTRUCT_SPRING_ANNOTATIONS);
            addDependency(MAPSTRUCT_SPRING_TEST_EXTENSIONS);
        }

        if (hasCamel) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Camel");

            if (hasSpringBoot) {
                addDependency(CAMEL_MAPSTRUCT_STARTER);
            } else if (hasQuarkus) {
                addDependency(CAMEL_QUARKUS_MAPSTRUCT);
            } else {
                addDependency(CAMEL_MAPSTRUCT);
            }
        }

        if (hasQuarkus) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Quarkus");
            addDependency(QUARKUS_MAPSTRUCT);
        }

        if (hasProtobuf) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Protobuf");
            addDependency(PROTOBUF_SPI_IMPL, processorConfig);
        }
    }

    private boolean isNeededDependency(Dependency dependency, PluginDependency pluginDependency) {
        return pluginDependency.getGroup().equals(dependency.getGroup())
                && pluginDependency.getName().equals(dependency.getName());
    }

    private boolean hasDependency(PluginDependency pluginDependency) {
        return configurations.getByName(pluginDependency.getConfiguration())
                .getAllDependencies()
                .stream()
                .anyMatch(dependency -> isNeededDependency(dependency, pluginDependency));
    }

    private boolean hasExtension(String extensionName) {
        return extensions.findByName(extensionName) != null;
    }

    private void addDependency(PluginDependency pluginDependency, String configuration) {
        LOGGER.lifecycle(DEPENDENCY_PREFIX, pluginDependency.getId());
        dependencies.add(configuration, pluginDependency.getId());
    }

    private void addDependency(PluginDependency pluginDependency) {
        addDependency(pluginDependency, pluginDependency.getConfiguration());
    }

}
