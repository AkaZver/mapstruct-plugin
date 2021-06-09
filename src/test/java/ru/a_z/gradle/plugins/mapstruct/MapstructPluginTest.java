package ru.a_z.gradle.plugins.mapstruct;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.a_z.gradle.plugins.mapstruct.PluginDependency.*;

/**
 * Tests for whole {@link MapstructPlugin}
 */
public class MapstructPluginTest {

    @TempDir
    protected File tempDirectory;
    private Project project;

    @BeforeEach
    public void setUpBeforeEach() {
        project = ProjectBuilder
                .builder()
                .withProjectDir(tempDirectory)
                .withName("test-project")
                .build();
    }

    @Test
    public void pluginAttached() {
        addBuildFile("build-base.gradle");

        assertDoesNotThrow(() -> project.getPluginManager().apply(MapstructPlugin.class));
        assertDoesNotThrow(() -> project.getPlugins().getPlugin(MapstructPlugin.class));

        MapstructExtension extension = project.getExtensions().findByType(MapstructExtension.class);
        assertThat(extension).isNotNull();

        assertSoftly(softly -> {
            softly.assertThat(extension.getSuppressGeneratorTimestamp()).isFalse();
            softly.assertThat(extension.getVerbose()).isFalse();
            softly.assertThat(extension.getSuppressGeneratorVersionInfoComment()).isFalse();
            softly.assertThat(extension.getDefaultComponentModel()).isEqualTo("default");
            softly.assertThat(extension.getDefaultInjectionStrategy()).isEqualTo("field");
            softly.assertThat(extension.getUnmappedTargetPolicy()).isEqualTo("WARN");
        });
    }

    @Test
    public void pluginConfigured() {
        addBuildFile("build-with-configuration.gradle");

        String gradleOutput = executeGradle("clean");

        List<String> pluginParams = Arrays.asList(
                "suppressGeneratorTimestamp:true",
                "verbose:true",
                "suppressGeneratorVersionInfoComment:true",
                "defaultComponentModel:spring",
                "defaultInjectionStrategy:constructor",
                "unmappedTargetPolicy:INFO"
        );

        assertThat(gradleOutput).contains(pluginParams);
    }

    @Test
    public void filledCompilerArguments() {
        addBuildFile("build-with-java-compile.gradle");

        String gradleOutput = executeGradle("clean");

        List<String> pluginArgs = Arrays.asList(
                "-Amapstruct.suppressGeneratorTimestamp=false",
                "-Amapstruct.verbose=false",
                "-Amapstruct.suppressGeneratorVersionInfoComment=false",
                "-Amapstruct.defaultComponentModel=default",
                "-Amapstruct.defaultInjectionStrategy=field",
                "-Amapstruct.unmappedTargetPolicy=WARN"
        );

        assertThat(gradleOutput).contains(pluginArgs);
    }

    @Test
    public void mapstructDependenciesAdded() {
        addBuildFile("build-base.gradle");

        String gradleOutput = executeGradle("dependencies");

        List<String> whiteList = toList(MAPSTRUCT, MAPSTRUCT_PROCESSOR);
        List<String> blackList = toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT_SPRING_EXTENSIONS);

        assertThat(gradleOutput)
                .contains(whiteList)
                .doesNotContain(blackList);
    }

    @Test
    public void lombokDependenciesAdded() {
        addBuildFile("build-with-lombok-dependencies.gradle");

        String gradleOutput = executeGradle("dependencies");

        List<String> whiteList = toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT, MAPSTRUCT_PROCESSOR);
        List<String> blackList = toList(MAPSTRUCT_SPRING_EXTENSIONS);

        assertThat(gradleOutput)
                .contains(whiteList)
                .doesNotContain(blackList);
    }

    @Test
    public void springDependenciesAdded() {
        addBuildFile("build-with-spring-dependencies.gradle");

        String gradleOutput = executeGradle("dependencies");

        List<String> whiteList = toList(MAPSTRUCT, MAPSTRUCT_PROCESSOR, MAPSTRUCT_SPRING_EXTENSIONS);
        List<String> blackList = toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING);

        assertThat(gradleOutput)
                .contains(whiteList)
                .doesNotContain(blackList);
    }

    private List<String> toList(PluginDependency... pluginDependency) {
        return Arrays.stream(pluginDependency)
                .map(PluginDependency::getId)
                .collect(Collectors.toList());
    }

    private String executeGradle(String... args) {
        return GradleRunner
                .create()
                .withPluginClasspath()
                .withProjectDir(project.getProjectDir())
                .withArguments(args)
                .build()
                .getOutput();
    }

    private void addBuildFile(String source) {
        Path targetPath = project.file("build.gradle").toPath();
        Supplier<InputStream> inputStream = () ->
                Objects.requireNonNull(this.getClass().getResourceAsStream("/" + source));

        try (InputStream sourceStream = inputStream.get()) {
            Files.copy(sourceStream, targetPath);
        } catch (IOException exception) {
            String message = String.format("Can't copy %s to %s", source, targetPath);
            throw new RuntimeException(message, exception);
        }
    }

}
