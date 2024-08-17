package com.github.akazver.gradle.plugins.mapstruct;

import com.github.akazver.gradle.plugins.mapstruct.manager.CompilerArgsManager;
import org.gradle.api.Project;
import org.gradle.api.internal.plugins.ExtensionContainerInternal;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Tests for compiler arguments addition
 *
 * @author Vasiliy Sobolev
 */
class CompilerArgsTest extends BaseTest {

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
        CompilerArgsManager compilerArgsManager = new CompilerArgsManager(project);

        when(project.getExtensions())
                .thenReturn(extensions);

        when(extensions.getByType(MapstructExtension.class))
                .thenReturn(extension);

        when(extension.getVerbose())
                .thenThrow(new RuntimeException("Test exception"));

        assertThatThrownBy(compilerArgsManager::addCompilerArgs)
                .isInstanceOf(MapstructPluginException.class)
                .extracting(Throwable::getMessage)
                .isEqualTo("Can't fetch compiler argument for 'verbose'");
    }

    private String[] fetchActualCompilerArgs(Project project) {
        return project.getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions()
                .getCompilerArgs()
                .toArray(new String[0]);
    }

}
