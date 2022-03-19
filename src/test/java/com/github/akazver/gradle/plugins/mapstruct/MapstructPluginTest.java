package com.github.akazver.gradle.plugins.mapstruct;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.LOMBOK;
import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.LOMBOK_MAPSTRUCT_BINDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MapstructPlugin} without Gradle execution
 */
@ExtendWith(MockitoExtension.class)
class MapstructPluginTest {

    @TempDir
    protected File tempDirectory;
    private Project project;
    private ConfigurationContainer configurations;
    private DependencyHandler dependencies;

    private final MapstructPlugin plugin = new MapstructPlugin();

    @BeforeEach
    void beforeEach() {
        project = ProjectBuilder
                .builder()
                .withProjectDir(tempDirectory)
                .withName("test-project")
                .build();

        project.getRepositories().mavenCentral();

        configurations = project.getConfigurations();
        configurations.create("annotationProcessor");
        configurations.create("implementation");

        dependencies = project.getDependencies();
    }

    @Test
    @DisplayName("Add dependency")
    void addDependency() {
        plugin.addDependency(project, LOMBOK);

        assertThat(fetchDependencies("annotationProcessor"))
                .hasSize(1)
                .first()
                .extracting(this::fetchDependencyId)
                .isEqualTo("org.projectlombok:lombok:1.18.22");
    }

    @Test
    @DisplayName("Add optional dependencies")
    void addOptionalDependencies() {
        String someSpringLibrary = "org.springframework:some-library:1.0.0";

        addDependency(LOMBOK);
        addDependency("implementation", someSpringLibrary);

        plugin.addOptionalDependencies(project);

        assertThat(fetchDependencies("annotationProcessor"))
                .hasSize(2)
                .extracting(this::fetchDependencyId)
                .contains("org.projectlombok:lombok:1.18.22", "org.projectlombok:lombok-mapstruct-binding:0.2.0");

        assertThat(fetchDependencies("implementation"))
                .hasSize(2)
                .extracting(this::fetchDependencyId)
                .contains(someSpringLibrary, "org.mapstruct.extensions.spring:mapstruct-spring-extensions:0.1.1");
    }

    @Test
    @DisplayName("Add optional dependencies with binding")
    void addOptionalDependenciesWithBinding() {
        addDependency(LOMBOK);
        addDependency(LOMBOK_MAPSTRUCT_BINDING);

        plugin.addOptionalDependencies(project);

        assertThat(fetchDependencies("annotationProcessor"))
                .hasSize(2)
                .extracting(this::fetchDependencyId)
                .contains("org.projectlombok:lombok:1.18.22", "org.projectlombok:lombok-mapstruct-binding:0.2.0");
    }

    @Test
    @DisplayName("Add optional dependencies without correct lombok")
    void addOptionalDependenciesWithoutCorrectLombok() {
        String someLombokLibrary = "org.projectlombok:some-library:1.0.0";

        addDependency("annotationProcessor", someLombokLibrary);

        plugin.addOptionalDependencies(project);

        assertThat(fetchDependencies("annotationProcessor"))
                .hasSize(1)
                .extracting(this::fetchDependencyId)
                .containsOnly(someLombokLibrary);
    }

    @Test
    @DisplayName("Add compiler arguments")
    void addCompilerArguments() {
        project.getPluginManager().apply(JavaPlugin.class);
        project.getExtensions().create("mapstruct", MapstructExtension.class);

        plugin.addCompilerArguments(project);

        String[] actual = project
                .getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions()
                .getCompilerArgs()
                .toArray(new String[0]);

        String[] expected = {
                "-Amapstruct.suppressGeneratorTimestamp=false",
                "-Amapstruct.verbose=false",
                "-Amapstruct.suppressGeneratorVersionInfoComment=false",
                "-Amapstruct.defaultComponentModel=default",
                "-Amapstruct.defaultInjectionStrategy=field",
                "-Amapstruct.unmappedTargetPolicy=WARN"
        };

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("Compiler argument exception")
    void compilerArgumentException() {
        Project fakeProject = mock(Project.class);
        ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
        MapstructExtension extension = spy(MapstructExtension.class);

        when(fakeProject.getExtensions())
                .thenReturn(extensionContainer);

        when(extensionContainer.getByType(MapstructExtension.class))
                .thenReturn(extension);

        when(extension.getVerbose())
                .thenThrow(new RuntimeException("Test exception"));

        assertThatThrownBy(() -> plugin.addCompilerArguments(fakeProject))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can't fetch compiler argument for verbose");
    }

    private void addDependency(PluginDependency pluginDependency) {
        addDependency(pluginDependency.getConfiguration(), pluginDependency.getId());
    }

    private void addDependency(String configuration, String id) {
        dependencies.add(configuration, id);
    }

    private String fetchDependencyId(Dependency dependency) {
        return String.join(":", dependency.getGroup(), dependency.getName(), dependency.getVersion());
    }

    private List<Dependency> fetchDependencies(String name) {
        Iterable<Dependency> iterator = () -> configurations
                .getAt(name)
                .getAllDependencies()
                .iterator();

        return StreamSupport
                .stream(iterator.spliterator(), false)
                .collect(Collectors.toList());
    }

}
