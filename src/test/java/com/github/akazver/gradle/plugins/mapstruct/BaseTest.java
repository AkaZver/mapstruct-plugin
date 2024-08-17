package com.github.akazver.gradle.plugins.mapstruct;

import com.github.akazver.gradle.plugins.mapstruct.dependency.PluginDependency;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

/**
 * Base functionality for other tests
 *
 * @author Vasiliy Sobolev
 */
abstract class BaseTest {

    @TempDir
    private File tempDirectory;

    protected Project fetchProject() {
        return evaluate(fetchBaseProject());
    }

    protected Project fetchProject(PluginDependency... pluginDependencies) {
        Project project = fetchBaseProject();
        DependencyHandler dependencies = project.getDependencies();

        for (PluginDependency pluginDependency : pluginDependencies) {
            dependencies.add(pluginDependency.getConfiguration(), pluginDependency.getId());
        }

        return evaluate(project);
    }

    protected Project fetchBaseProject() {
        Project project = ProjectBuilder.builder()
                .withProjectDir(tempDirectory)
                .withName("test-project")
                .build();

        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply(JavaPlugin.class);
        pluginManager.apply(MapstructPlugin.class);

        return project;
    }

    protected Project evaluate(Project project) {
        ((DefaultProject) project).evaluate();
        return project;
    }

}
