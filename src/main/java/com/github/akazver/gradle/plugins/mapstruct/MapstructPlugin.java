package com.github.akazver.gradle.plugins.mapstruct;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.tasks.compile.JavaCompile;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;

/**
 * Plugin implementation which adds necessary dependencies and compiler options
 */
public class MapstructPlugin implements Plugin<Project> {

    private static final String ARGUMENT_PATTERN = "-Amapstruct.%s=%s";
    private static final String SPRING_GROUP = "org.springframework";

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

    private void addDependency(Project project, PluginDependency pluginDependency) {
        project.getDependencies().add(pluginDependency.getConfiguration(), pluginDependency.getId());
    }

    private void addOptionalDependencies(Project project) {
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
        } else if (hasSpring) {
            addDependency(project, MAPSTRUCT_SPRING_EXTENSIONS);
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
        return SPRING_GROUP.equals(dependency.getGroup());
    }

    private void addCompilerArguments(Project project) {
        MapstructExtension extension = project.getExtensions().getByType(MapstructExtension.class);

        Function<MapstructArgument, String> fetchCompilerArgument = value ->
                String.format(ARGUMENT_PATTERN, value.getName(), value.getAccessor().apply(extension).get());

        List<String> compilerArguments = Arrays
                .stream(MapstructArgument.values())
                .map(fetchCompilerArgument)
                .collect(Collectors.toList());

        project.getTasks().withType(JavaCompile.class).forEach(task ->
                task.getOptions().getCompilerArgs().addAll(compilerArguments));
    }

}
