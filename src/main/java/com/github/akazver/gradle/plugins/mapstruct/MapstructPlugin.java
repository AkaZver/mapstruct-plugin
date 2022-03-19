package com.github.akazver.gradle.plugins.mapstruct;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.compile.JavaCompile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;

/**
 * Plugin implementation which adds necessary dependencies and compiler options
 */
public class MapstructPlugin implements Plugin<Project> {

    private static final String ARGUMENT_PATTERN = "-Amapstruct.%s=%s";

    @Override
    public void apply(Project project) {
        project.getExtensions().create("mapstruct", MapstructExtension.class);

        project.afterEvaluate(it -> {
            addDependency(it, MAPSTRUCT);
            addDependency(it, MAPSTRUCT_PROCESSOR);
            addOptionalDependencies(it);
            addCompilerArguments(it);
        });
    }

    protected void addDependency(Project project, PluginDependency pluginDependency) {
        project.getDependencies().add(pluginDependency.getConfiguration(), pluginDependency.getId());
    }

    protected void addOptionalDependencies(Project project) {
        boolean hasLombok = false;
        boolean hasBinding = false;
        boolean hasSpring = false;

        for (Configuration configuration : project.getConfigurations()) {
            for (Dependency dependency : configuration.getAllDependencies()) {
                if (isLombok(dependency)) {
                    hasLombok = true;
                } else if (isBinding(dependency)) {
                    hasBinding = true;
                } else if (isSpring(dependency)) {
                    hasSpring = true;
                }
            }
        }

        if (hasLombok && !hasBinding) {
            addDependency(project, LOMBOK_MAPSTRUCT_BINDING);
        }

        if (hasSpring) {
            addDependency(project, MAPSTRUCT_SPRING_EXTENSIONS);
        }
    }

    protected boolean isLombok(Dependency dependency) {
        return LOMBOK.getGroup().equals(dependency.getGroup())
                && LOMBOK.getName().equals(dependency.getName());
    }

    protected boolean isBinding(Dependency dependency) {
        return LOMBOK_MAPSTRUCT_BINDING.getGroup().equals(dependency.getGroup())
                && LOMBOK_MAPSTRUCT_BINDING.getName().equals(dependency.getName());
    }

    protected boolean isSpring(Dependency dependency) {
        return "org.springframework".equals(dependency.getGroup());
    }

    protected void addCompilerArguments(Project project) {
        MapstructExtension extension = project.getExtensions().getByType(MapstructExtension.class);

        UnaryOperator<String> fetchCompilerArg = name -> {
            try {
                PropertyDescriptor descriptor = new PropertyDescriptor(name, MapstructExtension.class);
                Object value = descriptor.getReadMethod().invoke(extension);

                return String.format(ARGUMENT_PATTERN, name, value);
            } catch (IllegalAccessException | InvocationTargetException | IntrospectionException exception) {
                String message = "Can't fetch compiler argument for " + name;
                throw new IllegalStateException(message, exception);
            }
        };

        List<String> compilerArgs = Arrays
                .stream(MapstructExtension.class.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(Field::getName)
                .map(fetchCompilerArg)
                .collect(Collectors.toList());

        project.getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions()
                .getCompilerArgs()
                .addAll(compilerArgs);
    }

}
