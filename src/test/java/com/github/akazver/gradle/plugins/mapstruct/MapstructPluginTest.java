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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MapstructPlugin}
 *
 * @author Vasiliy Sobolev
 */
class MapstructPluginTest {

    @TempDir
    private File tempDirectory;

    @Test
    @DisplayName("Add required dependencies")
    void addRequiredDependencies() {
        Project project = fetchProject();

        assertThat(project.getPlugins().hasPlugin("com.github.akazver.mapstruct")).isTrue();
        assertThat(project.getExtensions().findByName("mapstruct")).isNotNull();

        assertThat(fetchDependencyIds(project, "annotationProcessor"))
                .hasSize(1)
                .first()
                .isEqualTo(MAPSTRUCT_PROCESSOR.getId());

        assertThat(fetchDependencyIds(project, "implementation"))
                .hasSize(1)
                .first()
                .isEqualTo(MAPSTRUCT.getId());
    }

    private void baseOptionalDependenciesTest(Project project, String[] expectedAnnotationProcessor, String[] expectedImplementation) {
        String[] expectedTestImplementation =
                Stream.concat(Arrays.stream(expectedImplementation), Stream.of(MAPSTRUCT_SPRING_TEST_EXTENSIONS.getId()))
                        .toArray(String[]::new);

        assertThat(fetchDependencyIds(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .containsExactlyInAnyOrder(expectedAnnotationProcessor);

        assertThat(fetchDependencyIds(project, "implementation"))
                .hasSize(expectedImplementation.length)
                .containsExactlyInAnyOrder(expectedImplementation);

        assertThat(fetchDependencyIds(project, "testImplementation"))
                .hasSize(expectedTestImplementation.length)
                .containsExactlyInAnyOrder(expectedTestImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies without binding")
    void addOptionalDependenciesWithoutBinding() {
        Project project = fetchProject(SPRING_CORE, LOMBOK);

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), LOMBOK_MAPSTRUCT_BINDING.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId(), MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                SPRING_CORE.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        baseOptionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with binding")
    void addOptionalDependenciesWithBinding() {
        Project project = fetchProject(LOMBOK_MAPSTRUCT_BINDING, SPRING_CORE, LOMBOK);

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), LOMBOK_MAPSTRUCT_BINDING.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId(), MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                SPRING_CORE.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        baseOptionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
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

        assertThat(fetchDependencyIds(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .containsExactlyInAnyOrder(expectedAnnotationProcessor);

        assertThat(fetchDependencyIds(project, "implementation"))
                .hasSize(1)
                .containsExactlyInAnyOrder(MAPSTRUCT.getId());
    }

    @Test
    @DisplayName("Add optional dependencies with Spring Boot plugin")
    void addOptionalDependenciesWithSpringBootPlugin() {
        Project project = fetchBaseProject();
        project.getConfigurations().create("bootArchives");
        project.getDependencies().add(SPRING_CORE.getConfiguration(), SPRING_CORE.getId());
        evaluate(project);

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId()
        };

        String[] expectedImplementation = {
                SPRING_CORE.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        baseOptionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    private String[] fetchActualCompilerArgs(Project project) {
        return project.getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions()
                .getCompilerArgs()
                .toArray(new String[0]);
    }

    @Test
    @DisplayName("Add compiler arguments with default values success")
    void addCompilerArgumentsWithDefaultValuesSuccess() {
        Project project = fetchProject();

        String[] expectedCompilerArgs = {
                "-Amapstruct.suppressGeneratorTimestamp=false",
                "-Amapstruct.verbose=false",
                "-Amapstruct.suppressGeneratorVersionInfoComment=false",
                "-Amapstruct.defaultComponentModel=default",
                "-Amapstruct.defaultInjectionStrategy=field",
                "-Amapstruct.unmappedTargetPolicy=WARN",
                "-Amapstruct.unmappedSourcePolicy=WARN",
                "-Amapstruct.disableBuilders=false",
                "-Amapstruct.nullValueIterableMappingStrategy=RETURN_NULL",
                "-Amapstruct.nullValueMapMappingStrategy=RETURN_NULL"
        };

        assertThat(fetchActualCompilerArgs(project))
                .isEqualTo(expectedCompilerArgs);
    }

    @Test
    @DisplayName("Add compiler arguments with new values success")
    void addCompilerArgumentsWithNewValuesSuccess() {
        Project project = fetchBaseProject();
        MapstructExtension extension = project.getExtensions().getByType(MapstructExtension.class);

        extension.setSuppressGeneratorTimestamp(true);
        extension.setVerbose(true);
        extension.setSuppressGeneratorVersionInfoComment(true);
        extension.setDefaultComponentModel("spring");
        extension.setDefaultInjectionStrategy("constructor");
        extension.setUnmappedTargetPolicy("ERROR");
        extension.setUnmappedSourcePolicy("ERROR");
        extension.setDisableBuilders(true);
        extension.setNullValueIterableMappingStrategy("RETURN_DEFAULT");
        extension.setNullValueMapMappingStrategy("RETURN_DEFAULT");

        evaluate(project);

        String[] expectedCompilerArgs = {
                "-Amapstruct.suppressGeneratorTimestamp=true",
                "-Amapstruct.verbose=true",
                "-Amapstruct.suppressGeneratorVersionInfoComment=true",
                "-Amapstruct.defaultComponentModel=spring",
                "-Amapstruct.defaultInjectionStrategy=constructor",
                "-Amapstruct.unmappedTargetPolicy=ERROR",
                "-Amapstruct.unmappedSourcePolicy=ERROR",
                "-Amapstruct.disableBuilders=true",
                "-Amapstruct.nullValueIterableMappingStrategy=RETURN_DEFAULT",
                "-Amapstruct.nullValueMapMappingStrategy=RETURN_DEFAULT"
        };

        assertThat(fetchActualCompilerArgs(project))
                .isEqualTo(expectedCompilerArgs);
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
                .isInstanceOf(MapstructPluginException.class)
                .extracting(Throwable::getMessage)
                .isEqualTo("Can't fetch compiler argument for 'verbose'");
    }

    @Test
    @DisplayName("Correct plugin dependency")
    void correctPluginDependency() {
        assertThatCode(() -> new PluginDependency("implementation", "absolutely:not-broken:1.0.0"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Incorrect plugin dependency")
    void incorrectPluginDependency() {
        assertThatThrownBy(() -> new PluginDependency("implementation", "broken"))
                .isInstanceOf(MapstructPluginException.class)
                .hasMessage("Dependency id 'broken' is invalid");
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
        Project project = ProjectBuilder.builder()
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

    private List<String> fetchDependencyIds(Project project, String name) {
        Iterable<Dependency> iterator = () ->
                project.getConfigurations()
                        .getByName(name)
                        .getAllDependencies()
                        .iterator();

        return StreamSupport.stream(iterator.spliterator(), false)
                .map(this::fetchDependencyId)
                .collect(Collectors.toList());
    }

    private String fetchDependencyId(Dependency dependency) {
        return String.join(":", dependency.getGroup(), dependency.getName(), dependency.getVersion());
    }

}
