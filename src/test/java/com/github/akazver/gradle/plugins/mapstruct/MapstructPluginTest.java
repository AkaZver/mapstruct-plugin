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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
        Project project = fetchProject(SOME_SPRING_LIBRARY, LOMBOK);

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), LOMBOK_MAPSTRUCT_BINDING.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId(), MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                SOME_SPRING_LIBRARY.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        baseOptionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
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

        baseOptionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
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
                .isEqualTo("Can't fetch compiler argument for 'verbose'");
    }

    @Test
    @DisplayName("Incorrect plugin dependency")
    void incorrectPluginDependency() {
        assertThatThrownBy(() -> new PluginDependency("implementation", "broken"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Dependency id 'broken' is invalid");
    }

    @ParameterizedTest
    @DisplayName("Is Spring dependency")
    @MethodSource("fetchSpringDependencies")
    void isSpringDependency(PluginDependency pluginDependency, boolean hasSpringDependencies) {
        Project project = fetchBaseProject();
        DependencyHandler projectDependencies = project.getDependencies();
        projectDependencies.add(pluginDependency.getConfiguration(), pluginDependency.getId());
        evaluate(project);

        assertSoftly(softly ->
                SPRING_DEPENDENCIES.forEach(dependency -> {
                            List<String> dependencyIds = fetchDependencyIds(project, dependency.getConfiguration());

                            softly.assertThat(dependencyIds.contains(dependency.getId()))
                                    .isEqualTo(hasSpringDependencies);
                        }
                )
        );
    }

    private static Stream<Arguments> fetchSpringDependencies() {
        Function<String, PluginDependency> fetchDependency = id ->
                new PluginDependency("implementation", id);

        PluginDependency springCore = fetchDependency.apply("org.springframework:lib:1.0.0");
        PluginDependency notSpringLibrary = fetchDependency.apply("org.winter:lib:1.0.0");
        PluginDependency libraryWithNullGroup = fetchDependency.apply(":lib:1.0.0");

        return Stream.of(
                arguments(springCore, true),
                arguments(notSpringLibrary, false),
                arguments(libraryWithNullGroup, false)
        );
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
