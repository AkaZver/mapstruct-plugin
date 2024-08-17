package com.github.akazver.gradle.plugins.mapstruct;

import com.github.akazver.gradle.plugins.mapstruct.manager.CompilerArgsManager;
import com.github.akazver.gradle.plugins.mapstruct.manager.DependencyManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin implementation which adds necessary dependencies and compiler options
 *
 * @author Vasiliy Sobolev
 */
public class MapstructPlugin implements Plugin<Project> {

    @Override
    public void apply(@NotNull Project project) {
        DependencyManager dependencyManager = new DependencyManager(project);
        CompilerArgsManager compilerArgsManager = new CompilerArgsManager(project);

        project.getExtensions().create("mapstruct", MapstructExtension.class);

        project.afterEvaluate(it -> {
            dependencyManager.addRequiredDependencies();
            dependencyManager.addOptionalDependencies();
            compilerArgsManager.addCompilerArgs();
        });
    }

}
