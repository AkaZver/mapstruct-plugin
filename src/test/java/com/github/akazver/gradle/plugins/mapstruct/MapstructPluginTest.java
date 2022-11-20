package com.github.akazver.gradle.plugins.mapstruct;

import io.freefair.gradle.plugins.lombok.LombokPlugin;
import org.gradle.api.Project;
import org.gradle.api.ProjectConfigurationException;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.internal.plugins.ExtensionContainerInternal;
import org.gradle.api.internal.project.DefaultProject;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MapstructPlugin}
 */
class MapstructPluginTest {

    private static final PluginDependency SOME_SPRING_LIBRARY =
            new PluginDependency("implementation", "org.springframework:some-library:1.0.0");

    private static final PluginDependency SOME_LOMBOK_LIBRARY =
            new PluginDependency("annotationProcessor", "org.projectlombok:some-library:1.0.0");

    @TempDir
    private File tempDirectory;

    @Test
    @DisplayName("Apply")
    void apply() {
        Project project = fetchProject();

        assertThat(project.getPlugins().hasPlugin("com.github.akazver.mapstruct")).isTrue();
        assertThat(project.getExtensions().findByName("mapstruct")).isNotNull();
    }

    @Test
    @DisplayName("Add dependency")
    void addDependency() {
        Project project = fetchProject();

        assertThat(fetchDependencies(project, "annotationProcessor"))
                .hasSize(1)
                .first()
                .extracting(this::fetchDependencyId)
                .isEqualTo(MAPSTRUCT_PROCESSOR.getId());

        assertThat(fetchDependencies(project, "implementation"))
                .hasSize(1)
                .first()
                .extracting(this::fetchDependencyId)
                .isEqualTo(MAPSTRUCT.getId());
    }

    @Test
    @DisplayName("Add optional dependencies without binding")
    void addOptionalDependenciesWithoutBinding() {
        Project project = fetchProject(SOME_SPRING_LIBRARY, LOMBOK);

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), LOMBOK_MAPSTRUCT_BINDING.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId(), MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                SOME_SPRING_LIBRARY.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        assertThat(fetchDependencies(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedAnnotationProcessor);

        assertThat(fetchDependencies(project, "implementation"))
                .hasSize(expectedImplementation.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with binding")
    void addOptionalDependenciesWithBinding() {
        Project project = fetchProject(LOMBOK_MAPSTRUCT_BINDING, SOME_SPRING_LIBRARY, LOMBOK);

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), LOMBOK_MAPSTRUCT_BINDING.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId(), MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                SOME_SPRING_LIBRARY.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        assertThat(fetchDependencies(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedAnnotationProcessor);

        assertThat(fetchDependencies(project, "implementation"))
                .hasSize(expectedImplementation.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with incorrect binding")
    void addOptionalDependenciesWithIncorrectBinding() {
        Project project = fetchProject(SOME_LOMBOK_LIBRARY, SOME_SPRING_LIBRARY, LOMBOK);

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), MAPSTRUCT_PROCESSOR.getId(), LOMBOK_MAPSTRUCT_BINDING.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId(), SOME_LOMBOK_LIBRARY.getId()
        };

        String[] expectedImplementation = {
                SOME_SPRING_LIBRARY.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        assertThat(fetchDependencies(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedAnnotationProcessor);

        assertThat(fetchDependencies(project, "implementation"))
                .hasSize(expectedImplementation.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Lombok plugin")
    void addOptionalDependenciesWithLombokPlugin() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(LombokPlugin.class);
        evaluate(project);

        DependencySet lombokDefaultDependencies = project.getConfigurations()
                .getByName("lombok")
                .getIncoming()
                .getDependencies();

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), MAPSTRUCT_PROCESSOR.getId(),
                LOMBOK_MAPSTRUCT_BINDING.getId()
        };

        assertThat(project.getPlugins().hasPlugin("io.freefair.lombok")).isTrue();
        assertThat(project.getExtensions().findByName("lombok")).isNotNull();

        assertThat(lombokDefaultDependencies)
                .hasSize(1)
                .first()
                .extracting(this::fetchDependencyId)
                .isEqualTo(LOMBOK.getId());

        assertThat(fetchDependencies(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .extracting(this::fetchDependencyId)
                .contains(expectedAnnotationProcessor);

        assertThat(fetchDependencies(project, "implementation"))
                .hasSize(1)
                .extracting(this::fetchDependencyId)
                .contains(MAPSTRUCT.getId());
    }

    @Test
    @DisplayName("Add compiler arguments success")
    void addCompilerArgumentsSuccess() {
        Project project = fetchProject();

        String[] actualCompilerArgs = project
                .getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions()
                .getCompilerArgs()
                .toArray(new String[0]);

        String[] expectedCompilerArgs = {
                "-Amapstruct.suppressGeneratorTimestamp=false",
                "-Amapstruct.verbose=false",
                "-Amapstruct.suppressGeneratorVersionInfoComment=false",
                "-Amapstruct.defaultComponentModel=default",
                "-Amapstruct.defaultInjectionStrategy=field",
                "-Amapstruct.unmappedTargetPolicy=WARN",
                "-Amapstruct.unmappedSourcePolicy=WARN",
                "-Amapstruct.disableBuilders=false"
        };

        assertThat(actualCompilerArgs).isEqualTo(expectedCompilerArgs);
    }

    @Test
    @DisplayName("Add compiler arguments fail")
    void addCompilerArgumentsFail() {
        Project project = spy(fetchBaseProject());
        ExtensionContainer extensions = mock(ExtensionContainerInternal.class);
        MapstructExtension extension = spy(MapstructExtension.class);

        when(project.getExtensions())
                .thenReturn(extensions);

        when(extensions.getByType(MapstructExtension.class))
                .thenReturn(extension);

        when(extension.getVerbose())
                .thenThrow(new RuntimeException("Test exception"));

        assertThatThrownBy(() -> evaluate(project))
                .isInstanceOf(ProjectConfigurationException.class)
                .extracting(Throwable::getCause)
                .isInstanceOf(IllegalStateException.class)
                .extracting(Throwable::getMessage)
                .isEqualTo("Can't fetch compiler argument for verbose");
    }

    @Test
    @DisplayName("Incorrect plugin dependency")
    void incorrectPluginDependency() {
        assertThatThrownBy(() -> new PluginDependency("implementation", "broken"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Dependency id is invalid: broken");
    }

    private Project fetchProject() {
        return evaluate(fetchBaseProject());
    }

    private Project fetchProject(PluginDependency... pluginDependencies) {
        Project project = fetchBaseProject();
        DependencyHandler dependencies = project.getDependencies();

        for (PluginDependency pluginDependency : pluginDependencies) {
            dependencies.add(pluginDependency.getConfiguration(), pluginDependency.getId());
        }

        return evaluate(project);
    }

    private Project fetchBaseProject() {
        Project project = ProjectBuilder
                .builder()
                .withProjectDir(tempDirectory)
                .withName("test-project")
                .build();

        PluginManager pluginManager = project.getPluginManager();
        pluginManager.apply(JavaPlugin.class);
        pluginManager.apply(MapstructPlugin.class);

        return project;
    }

    private Project evaluate(Project project) {
        ((DefaultProject) project).evaluate();
        return project;
    }

    private List<Dependency> fetchDependencies(Project project, String name) {
        Iterable<Dependency> iterator = () ->
                project.getConfigurations()
                        .getByName(name)
                        .getAllDependencies()
                        .iterator();

        return StreamSupport
                .stream(iterator.spliterator(), false)
                .collect(Collectors.toList());
    }

    private String fetchDependencyId(Dependency dependency) {
        return String.join(":", dependency.getGroup(), dependency.getName(), dependency.getVersion());
    }

}
