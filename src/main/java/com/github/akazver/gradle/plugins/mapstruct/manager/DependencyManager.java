package com.github.akazver.gradle.plugins.mapstruct.manager;

import com.github.akazver.gradle.plugins.mapstruct.dependency.PluginDependency;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtensionContainer;

import static com.github.akazver.gradle.plugins.mapstruct.dependency.AdditionalDependency.*;
import static com.github.akazver.gradle.plugins.mapstruct.dependency.MarkerDependency.*;

/**
 * Manages required and optional dependencies addition
 *
 * @author Vasiliy Sobolev
 */
public class DependencyManager {

    private static final Logger LOGGER = Logging.getLogger(DependencyManager.class);

    private static final String ADDING_MESSAGE = "Adding {} dependencies";
    private static final String DEPENDENCY_PREFIX = "- {}";

    private static final String LOMBOK_CONFIG_NAME = "lombok";
    private static final String SPRING_BOOT_CONFIG_NAME = "bootArchives";
    private static final String QUARKUS_EXT_NAME = "quarkus";

    private final ConfigurationContainer configurations;
    private final ExtensionContainer extensions;
    private final DependencyHandler dependencies;

    public DependencyManager(Project project) {
        this.configurations = project.getConfigurations();
        this.extensions = project.getExtensions();
        this.dependencies = project.getDependencies();
    }

    public void addRequiredDependencies() {
        LOGGER.lifecycle(ADDING_MESSAGE, "MapStruct");
        addDependencies(MAPSTRUCT, MAPSTRUCT_PROCESSOR);
    }

    public void addOptionalDependencies() {
        boolean hasLombok = hasConfiguration(LOMBOK_CONFIG_NAME) || hasDependency(LOMBOK);
        boolean hasBinding = hasDependency(LOMBOK_MAPSTRUCT_BINDING);
        boolean hasSpringBoot = hasConfiguration(SPRING_BOOT_CONFIG_NAME) || hasDependency(SPRING_BOOT);
        boolean hasSpring = hasDependency(SPRING_CORE);
        boolean hasCamel = hasDependency(CAMEL_CORE);
        boolean hasQuarkus = hasExtension(QUARKUS_EXT_NAME) || hasDependency(QUARKUS_CORE);

        if (hasLombok && !hasBinding) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Lombok");
            addDependency(LOMBOK_MAPSTRUCT_BINDING);
        }

        if (hasSpringBoot || hasSpring) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Spring");
            addDependencies(MAPSTRUCT_SPRING_EXTENSIONS, MAPSTRUCT_SPRING_ANNOTATIONS, MAPSTRUCT_SPRING_TEST_EXTENSIONS);
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

    private boolean hasConfiguration(String configurationName) {
        return configurations.findByName(configurationName) != null;
    }

    @SuppressWarnings("SameParameterValue")
    private boolean hasExtension(String extensionName) {
        return extensions.findByName(extensionName) != null;
    }

    private void addDependency(PluginDependency pluginDependency) {
        LOGGER.lifecycle(DEPENDENCY_PREFIX, pluginDependency.getId());
        dependencies.add(pluginDependency.getConfiguration(), pluginDependency.getId());
    }

    private void addDependencies(PluginDependency... pluginDependencies) {
        for (PluginDependency pluginDependency : pluginDependencies) {
            addDependency(pluginDependency);
        }
    }

}
