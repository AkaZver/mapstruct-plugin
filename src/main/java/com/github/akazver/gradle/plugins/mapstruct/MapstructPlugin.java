package com.github.akazver.gradle.plugins.mapstruct;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;

/**
 * Plugin implementation which adds necessary dependencies and compiler options
 *
 * @author Vasiliy Sobolev
 */
public class MapstructPlugin implements Plugin<Project> {

    private static final Logger LOGGER = Logging.getLogger(MapstructPlugin.class);
    private static final String ADDING_MESSAGE = "Adding {} dependencies";
    private static final String COMPILER_ARG_PATTERN = "-Amapstruct.%s=%s";

    @Override
    public void apply(Project project) {
        project.getExtensions().create("mapstruct", MapstructExtension.class);

        project.afterEvaluate(it -> {
            addRequiredDependencies(it);
            addOptionalDependencies(it);
            addCompilerArgs(it);
        });
    }

    private void addDependencies(Project project, List<PluginDependency> pluginDependencies) {
        DependencyHandler projectDependencies = project.getDependencies();

        pluginDependencies.forEach(pluginDependency -> {
            LOGGER.lifecycle("- {}", pluginDependency.getId());
            projectDependencies.add(pluginDependency.getConfiguration(), pluginDependency.getId());
        });
    }

    private void addRequiredDependencies(Project project) {
        LOGGER.lifecycle(ADDING_MESSAGE, "MapStruct");
        addDependencies(project, MAPSTRUCT_DEPENDENCIES);
    }

    private void addOptionalDependencies(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();

        if (hasLombok(configurations) && !hasBinding(configurations)) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Lombok");
            addDependencies(project, LOMBOK_DEPENDENCIES);
        }

        if (hasSpring(configurations)) {
            LOGGER.lifecycle(ADDING_MESSAGE, "Spring");
            addDependencies(project, SPRING_DEPENDENCIES);
        }
    }

    private boolean hasLombok(ConfigurationContainer configurations) {
        return hasConfiguration(configurations, "lombok")
                || hasDependency(configurations, LOMBOK);
    }

    private boolean hasBinding(ConfigurationContainer configurations) {
        return hasDependency(configurations, LOMBOK_MAPSTRUCT_BINDING);
    }

    private boolean hasSpring(ConfigurationContainer configurations) {
        return hasConfiguration(configurations, "bootArchives")
                || hasDependency(configurations, SPRING_CORE);
    }

    private boolean isNeededDependency(Dependency dependency, PluginDependency pluginDependency) {
        return pluginDependency.getGroup().equals(dependency.getGroup())
                && pluginDependency.getName().equals(dependency.getName());
    }

    private boolean hasDependency(ConfigurationContainer configurations, PluginDependency pluginDependency) {
        return configurations.getByName(pluginDependency.getConfiguration())
                .getAllDependencies()
                .stream()
                .anyMatch(dependency -> isNeededDependency(dependency, pluginDependency));
    }

    private boolean hasConfiguration(ConfigurationContainer configurations, String configurationName) {
        return configurations.findByName(configurationName) != null;
    }

    private void addCompilerArgs(Project project) {
        MapstructExtension extension = project.getExtensions().getByType(MapstructExtension.class);

        CompileOptions compileOptions = project.getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions();

        Stream<String> projectCompilerArgs = compileOptions.getCompilerArgs().stream();

        Stream<String> mapstructCompilerArgs = Arrays.stream(MapstructExtension.class.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(Field::getName)
                .map(name -> fetchCompilerArg(extension, name));

        List<String> compilerArgs = Stream.concat(projectCompilerArgs, mapstructCompilerArgs)
                .collect(Collectors.toList());

        compileOptions.setCompilerArgs(compilerArgs);
    }

    private String fetchCompilerArg(MapstructExtension extension, String name) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(name, MapstructExtension.class);
            Object value = descriptor.getReadMethod().invoke(extension);

            return String.format(COMPILER_ARG_PATTERN, name, value);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException exception) {
            String message = String.format("Can't fetch compiler argument for '%s'", name);
            throw new MapstructPluginException(message, exception);
        }
    }

}
