package com.github.akazver.gradle.plugins.mapstruct.dependency;

import com.github.akazver.gradle.plugins.mapstruct.MapstructPluginException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Description for dependencies used by plugin
 *
 * @author Vasiliy Sobolev
 */
@Getter
@ToString
@EqualsAndHashCode
public class PluginDependency {

    private final String configuration;
    private final String group;
    private final String name;
    private final String version;
    private final String id;

    public PluginDependency(String configuration, String id) {
        this.configuration = configuration;
        this.id = id;

        String[] split = id.split(":");

        if (split.length != 3) {
            String message = String.format("Dependency id '%s' is invalid", id);
            throw new MapstructPluginException(message);
        }

        this.group = split[0];
        this.name = split[1];
        this.version = split[2];
    }

}
