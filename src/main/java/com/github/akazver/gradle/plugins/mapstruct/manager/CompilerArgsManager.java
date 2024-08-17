package com.github.akazver.gradle.plugins.mapstruct.manager;

import com.github.akazver.gradle.plugins.mapstruct.MapstructExtension;
import com.github.akazver.gradle.plugins.mapstruct.MapstructPluginException;
import lombok.RequiredArgsConstructor;
import org.gradle.api.Project;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages correct compiler arguments building
 *
 * @author Vasiliy Sobolev
 */
@RequiredArgsConstructor
public class CompilerArgsManager {

    private static final String COMPILER_ARG_PATTERN = "-Amapstruct.%s=%s";

    private final Project project;

    public void addCompilerArgs() {
        MapstructExtension extension = project.getExtensions().getByType(MapstructExtension.class);

        CompileOptions compileOptions = project.getTasks()
                .withType(JavaCompile.class)
                .getByName("compileJava")
                .getOptions();

        Stream<String> projectCompilerArgs = compileOptions.getCompilerArgs().stream();

        Stream<String> mapstructCompilerArgs = Arrays.stream(MapstructExtension.class.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(Field::getName)
                .map(name -> fetchCompilerArg(extension, name));

        List<String> compilerArgs = Stream.concat(projectCompilerArgs, mapstructCompilerArgs)
                .collect(Collectors.toList());

        compileOptions.setCompilerArgs(compilerArgs);
    }

    private String fetchCompilerArg(MapstructExtension extension, String name) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(name, MapstructExtension.class);
            Object value = descriptor.getReadMethod().invoke(extension);

            return String.format(COMPILER_ARG_PATTERN, name, value);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException exception) {
            String message = String.format("Can't fetch compiler argument for '%s'", name);
            throw new MapstructPluginException(message, exception);
        }
    }

}
