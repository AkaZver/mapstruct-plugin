package com.github.akazver.gradle.plugins.mapstruct;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
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
 */
public class MapstructPlugin implements Plugin<Project> {

    private static final String COMPILER_ARG_PATTERN = "-Amapstruct.%s=%s";

    @Override
    public void apply(Project project) {
        project.getExtensions().create("mapstruct", MapstructExtension.class);

        project.afterEvaluate(it -> {
            addDependencies(it, MAPSTRUCT_DEPENDENCIES);
            addOptionalDependencies(it);
            addCompilerArgs(it);
        });
    }

    private void addDependencies(Project project, List<PluginDependency> pluginDependencies) {
        DependencyHandler projectDependencies = project.getDependencies();

        pluginDependencies.forEach(pluginDependency ->
                projectDependencies.add(pluginDependency.getConfiguration(), pluginDependency.getId()));
    }

    @SuppressWarnings({"java:S2583", "java:S2589"})
    private void addOptionalDependencies(Project project) {
        ConfigurationContainer configurations = project.getConfigurations();
        boolean hasLombok = configurations.findByName("lombok") != null;
        boolean hasBinding = false;
        boolean hasSpring = false;

        for (Configuration configuration : configurations) {
            for (Dependency dependency : configuration.getAllDependencies()) {
                if (!hasLombok && isLombok(dependency)) {
                    hasLombok = true;
                } else if (!hasBinding && isBinding(dependency)) {
                    hasBinding = true;
                } else if (!hasSpring && isSpring(dependency)) {
                    hasSpring = true;
                }
            }
        }

        if (hasLombok && !hasBinding) {
            addDependencies(project, LOMBOK_DEPENDENCIES);
        }

        if (hasSpring) {
            addDependencies(project, SPRING_DEPENDENCIES);
        }
    }

    private boolean isLombok(Dependency dependency) {
        return LOMBOK.getGroup().equals(dependency.getGroup())
                && LOMBOK.getName().equals(dependency.getName());
    }

    private boolean isBinding(Dependency dependency) {
        return LOMBOK_MAPSTRUCT_BINDING.getGroup().equals(dependency.getGroup())
                && LOMBOK_MAPSTRUCT_BINDING.getName().equals(dependency.getName());
    }

    private boolean isSpring(Dependency dependency) {
        String group = dependency.getGroup();
        return group != null && group.startsWith("org.springframework");
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
            throw new IllegalStateException(message, exception);
        }
    }

}
