package io.github.akazver.gradle.plugins.mapstruct;

import io.github.akazver.gradle.plugins.mapstruct.dependency.PluginDependency;
import com.google.protobuf.gradle.ProtobufPlugin;
import io.freefair.gradle.plugins.lombok.LombokPlugin;
import io.quarkus.gradle.QuarkusPlugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.DependencySet;
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.github.akazver.gradle.plugins.mapstruct.dependency.AdditionalDependency.*;
import static io.github.akazver.gradle.plugins.mapstruct.dependency.MarkerDependency.*;
import static org.assertj.core.api.Assertions.*;

/**
 * Tests for required and optional dependencies addition
 *
 * @author Vasiliy Sobolev
 */
class DependencyTest extends BaseTest {

    @Test
    @DisplayName("Add required dependencies")
    void addRequiredDependencies() {
        Project project = fetchProject();

        assertThat(project.getPlugins().findPlugin("io.github.akazver.mapstruct")).isNotNull();
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

        optionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
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

        optionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Lombok plugin")
    void addOptionalDependenciesWithLombokPlugin() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(LombokPlugin.class);
        evaluate(project);

        assertThat(project.getPlugins().findPlugin("io.freefair.lombok")).isNotNull();
        assertThat(project.getExtensions().findByName("lombok")).isNotNull();
        assertThat(project.getConfigurations().findByName("lombok")).isNotNull();

        DependencySet lombokDefaultDependencies = project.getConfigurations()
                .getByName("lombok")
                .getIncoming()
                .getDependencies();

        String[] expectedAnnotationProcessor = {
                LOMBOK.getId(), MAPSTRUCT_PROCESSOR.getId(),
                LOMBOK_MAPSTRUCT_BINDING.getId()
        };

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
    @DisplayName("Add optional dependencies with Spring Boot")
    void addOptionalDependenciesWithSpringBoot() {
        Project project = fetchBaseProject();
        project.getDependencies().add(SPRING_BOOT.getConfiguration(), SPRING_BOOT.getId());
        evaluate(project);

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId()
        };

        String[] expectedImplementation = {
                SPRING_BOOT.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        optionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Spring Boot plugin")
    void addOptionalDependenciesWithSpringBootPlugin() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(SpringBootPlugin.class);
        project.getDependencies().add(SPRING_BOOT.getConfiguration(), SPRING_BOOT.getId());
        evaluate(project);

        assertThat(project.getPlugins().findPlugin("org.springframework.boot")).isNotNull();
        assertThat(project.getExtensions().findByName("springBoot")).isNotNull();
        assertThat(project.getConfigurations().findByName("bootArchives")).isNotNull();

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId()
        };

        String[] expectedImplementation = {
                SPRING_BOOT.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        optionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Camel")
    void addOptionalDependenciesWithCamel() {
        Project project = fetchBaseProject();
        project.getDependencies().add(CAMEL_CORE.getConfiguration(), CAMEL_CORE.getId());
        evaluate(project);

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId()
        };
        String[] expectedImplementation = {
                CAMEL_CORE.getId(), CAMEL_MAPSTRUCT.getId(),
                MAPSTRUCT.getId()
        };

        optionalDependenciesWithoutSpringTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Camel and Spring Boot")
    void addOptionalDependenciesWithCamelAndSpringBoot() {
        Project project = fetchBaseProject();
        project.getDependencies().add(SPRING_BOOT.getConfiguration(), SPRING_BOOT.getId());
        project.getDependencies().add(CAMEL_CORE.getConfiguration(), CAMEL_CORE.getId());
        evaluate(project);

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId()
        };
        String[] expectedImplementation = {
                SPRING_BOOT.getId(), MAPSTRUCT.getId(),
                CAMEL_CORE.getId(), CAMEL_MAPSTRUCT_STARTER.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        optionalDependenciesTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Camel and Quarkus")
    void addOptionalDependenciesWithCamelAndQuarkus() {
        Project project = fetchBaseProject();
        project.getDependencies().add(QUARKUS_CORE.getConfiguration(), QUARKUS_CORE.getId());
        project.getDependencies().add(CAMEL_CORE.getConfiguration(), CAMEL_CORE.getId());
        evaluate(project);

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                MAPSTRUCT.getId(), CAMEL_CORE.getId(), QUARKUS_CORE.getId(),
                CAMEL_QUARKUS_MAPSTRUCT.getId(), QUARKUS_MAPSTRUCT.getId()
        };

        optionalDependenciesWithoutSpringTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Camel and Quarkus plugin")
    void addOptionalDependenciesWithCamelAndQuarkusPlugin() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(QuarkusPlugin.class);
        project.getDependencies().add(CAMEL_CORE.getConfiguration(), CAMEL_CORE.getId());
        project.getDependencies().add(QUARKUS_CORE.getConfiguration(), QUARKUS_CORE.getId());
        evaluate(project);

        assertThat(project.getPlugins().findPlugin("io.quarkus")).isNotNull();
        assertThat(project.getExtensions().findByName("quarkus")).isNotNull();
        assertThat(project.getConfigurations().findByName("quarkusPlatformProperties")).isNotNull();

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                MAPSTRUCT.getId(), CAMEL_CORE.getId(), QUARKUS_CORE.getId(),
                CAMEL_QUARKUS_MAPSTRUCT.getId(), QUARKUS_MAPSTRUCT.getId()
        };

        optionalDependenciesWithoutSpringTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Protobuf")
    void addOptionalDependenciesWithProtobuf() {
        Project project = fetchBaseProject();
        project.getDependencies().add(PROTOBUF_JAVA.getConfiguration(), PROTOBUF_JAVA.getId());
        evaluate(project);

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                MAPSTRUCT.getId(), PROTOBUF_JAVA.getId(), PROTOBUF_SPI_IMPL.getId()
        };

        optionalDependenciesWithoutSpringTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add optional dependencies with Protobuf plugin")
    void addOptionalDependenciesWithProtobufPlugin() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(ProtobufPlugin.class);
        project.getDependencies().add(PROTOBUF_JAVA.getConfiguration(), PROTOBUF_JAVA.getId());
        evaluate(project);

        assertThat(project.getPlugins().findPlugin("com.google.protobuf")).isNotNull();
        assertThat(project.getExtensions().findByName("protobuf")).isNotNull();
        assertThat(project.getConfigurations().findByName("protobuf")).isNotNull();

        String[] expectedAnnotationProcessor = {
                MAPSTRUCT_PROCESSOR.getId()
        };

        String[] expectedImplementation = {
                MAPSTRUCT.getId(), PROTOBUF_JAVA.getId(), PROTOBUF_SPI_IMPL.getId()
        };

        optionalDependenciesWithoutSpringTest(project, expectedAnnotationProcessor, expectedImplementation);
    }

    @Test
    @DisplayName("Add dependencies with Kotlin plugin")
    void addDependenciesWithKotlinPlugin() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(KotlinPluginWrapper.class);
        evaluate(project);

        assertThat(project.getPlugins().findPlugin("org.jetbrains.kotlin.jvm")).isNotNull();
        assertThat(project.getPlugins().findPlugin("org.jetbrains.kotlin.kapt")).isNotNull();

        assertThat(fetchDependencyIds(project, "kapt"))
                .hasSize(1)
                .first()
                .isEqualTo(MAPSTRUCT_PROCESSOR.getId());

        assertThat(fetchDependencyIds(project, "implementation"))
                .hasSize(1)
                .first()
                .isEqualTo(MAPSTRUCT.getId());

        assertThat(fetchDependencyIds(project, "annotationProcessor"))
                .isEmpty();
    }

    @Test
    @DisplayName("Add dependencies with Kotlin plugin and Spring")
    void addDependenciesWithKotlinPluginAndSpring() {
        Project project = fetchBaseProject();
        project.getPluginManager().apply(KotlinPluginWrapper.class);
        project.getDependencies().add(SPRING_CORE.getConfiguration(), SPRING_CORE.getId());
        evaluate(project);

        assertThat(project.getPlugins().findPlugin("org.jetbrains.kotlin.jvm")).isNotNull();
        assertThat(project.getPlugins().findPlugin("org.jetbrains.kotlin.kapt")).isNotNull();

        String[] expectedKapt = {
                MAPSTRUCT_PROCESSOR.getId(),
                MAPSTRUCT_SPRING_EXTENSIONS.getId()
        };

        String[] expectedImplementation = {
                SPRING_CORE.getId(), MAPSTRUCT.getId(),
                MAPSTRUCT_SPRING_ANNOTATIONS.getId()
        };

        assertThat(fetchDependencyIds(project, "kapt"))
                .hasSize(expectedKapt.length)
                .containsExactlyInAnyOrder(expectedKapt);

        assertThat(fetchDependencyIds(project, "implementation"))
                .hasSize(expectedImplementation.length)
                .containsExactlyInAnyOrder(expectedImplementation);

        assertThat(fetchDependencyIds(project, "annotationProcessor"))
                .isEmpty();
    }

    private void optionalDependenciesTest(Project project, String[] expectedAnnotationProcessor, String[] expectedImplementation) {
        String[] expectedTestImplementation =
                Stream.concat(Arrays.stream(expectedImplementation), Stream.of(MAPSTRUCT_SPRING_TEST_EXTENSIONS.getId()))
                        .toArray(String[]::new);

        optionalDependenciesWithoutSpringTest(project, expectedAnnotationProcessor, expectedImplementation);

        assertThat(fetchDependencyIds(project, "testImplementation"))
                .hasSize(expectedTestImplementation.length)
                .containsExactlyInAnyOrder(expectedTestImplementation);
    }

    private void optionalDependenciesWithoutSpringTest(Project project, String[] expectedAnnotationProcessor, String[] expectedImplementation) {
        assertThat(fetchDependencyIds(project, "annotationProcessor"))
                .hasSize(expectedAnnotationProcessor.length)
                .containsExactlyInAnyOrder(expectedAnnotationProcessor);

        assertThat(fetchDependencyIds(project, "implementation"))
                .hasSize(expectedImplementation.length)
                .containsExactlyInAnyOrder(expectedImplementation);
    }

    private List<String> fetchDependencyIds(Project project, String name) {
        Iterable<Dependency> iterator = () ->
                project.getConfigurations()
                        .getByName(name)
                        .getAllDependencies()
                        .iterator();

        return StreamSupport.stream(iterator.spliterator(), false)
                .map(this::fetchDependencyId)
                .toList();
    }

    private String fetchDependencyId(Dependency dependency) {
        return String.join(":", dependency.getGroup(), dependency.getName(), dependency.getVersion());
    }

}
