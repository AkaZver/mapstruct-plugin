package com.github.akazver.gradle.plugins.mapstruct;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.akazver.gradle.plugins.mapstruct.PluginDependency.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Tests for {@link MapstructPlugin} with Gradle execution
 */
class MapstructPluginGradleTest {

    @TempDir
    protected File tempDirectory;
    private Project project;

    @BeforeEach
    void beforeEach() {
        project = ProjectBuilder
                .builder()
                .withProjectDir(tempDirectory)
                .withName("test-project")
                .build();
    }

    @Test
    @DisplayName("Plugin attached")
    void pluginAttached() {
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

    @ParameterizedTest(name = "[{index}] Plugin configured with {0}")
    @MethodSource("fetchArgumentsWithConfiguration")
    void pluginConfigured(String buildFile, List<String> params) {
        addBuildFile(buildFile);
        assertThat(executeGradle("clean")).contains(params);
    }

    @ParameterizedTest(name = "[{index}] Dependencies added from {0}")
    @MethodSource("fetchArgumentsWithDependencies")
    void dependenciesAdded(String buildFile, List<String> whiteList, List<String> blackList) {
        addBuildFile(buildFile);

        assertThat(executeGradle("dependencies"))
                .contains(whiteList)
                .doesNotContain(blackList);
    }

    // MethodSource for pluginConfigured
    @SuppressWarnings("unused")
    private static Stream<Arguments> fetchArgumentsWithConfiguration() {
        List<String> pluginParams = Arrays.asList(
                "suppressGeneratorTimestamp:true",
                "verbose:true",
                "suppressGeneratorVersionInfoComment:true",
                "defaultComponentModel:spring",
                "defaultInjectionStrategy:constructor",
                "unmappedTargetPolicy:INFO"
        );

        List<String> pluginArgs = Arrays.asList(
                "-Amapstruct.suppressGeneratorTimestamp=false",
                "-Amapstruct.verbose=false",
                "-Amapstruct.suppressGeneratorVersionInfoComment=false",
                "-Amapstruct.defaultComponentModel=default",
                "-Amapstruct.defaultInjectionStrategy=field",
                "-Amapstruct.unmappedTargetPolicy=WARN"
        );

        return Stream.of(
                arguments("build-with-configuration.gradle", pluginParams),
                arguments("build-with-java-compile.gradle", pluginArgs),
                arguments("build-with-java-test-compile.gradle", Collections.singletonList("Compiler args -> []"))
        );
    }

    // MethodSource for dependenciesAdded
    @SuppressWarnings("unused")
    private static Stream<Arguments> fetchArgumentsWithDependencies() {
        return Stream.of(
                arguments("build-base.gradle",
                        toList(MAPSTRUCT, MAPSTRUCT_PROCESSOR),
                        toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT_SPRING_EXTENSIONS)),

                arguments("build-with-lombok.gradle",
                        toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT, MAPSTRUCT_PROCESSOR),
                        toList(MAPSTRUCT_SPRING_EXTENSIONS)),

                arguments("build-with-lombok-plugin.gradle",
                        toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT, MAPSTRUCT_PROCESSOR),
                        toList(MAPSTRUCT_SPRING_EXTENSIONS)),

                arguments("build-with-spring.gradle",
                        toList(MAPSTRUCT, MAPSTRUCT_PROCESSOR, MAPSTRUCT_SPRING_EXTENSIONS),
                        toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING)),

                arguments("build-with-lombok-and-spring.gradle",
                        toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT, MAPSTRUCT_PROCESSOR, MAPSTRUCT_SPRING_EXTENSIONS),
                        Collections.singletonList("No blacklist for 'build-with-lombok-and-spring' variant")),

                arguments("build-with-lombok-plugin-and-spring.gradle",
                        toList(LOMBOK, LOMBOK_MAPSTRUCT_BINDING, MAPSTRUCT, MAPSTRUCT_PROCESSOR, MAPSTRUCT_SPRING_EXTENSIONS),
                        Collections.singletonList("No blacklist for 'build-with-lombok-plugin-and-spring' variant"))
        );
    }

    private static List<String> toList(PluginDependency... pluginDependency) {
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
