package com.github.akazver.gradle.plugins.mapstruct;

/**
 * Custom runtime exception for the plugin
 *
 * @author Vasiliy Sobolev
 */
public class MapstructPluginException extends RuntimeException {

    public MapstructPluginException(String message) {
        super(message);
    }

    public MapstructPluginException(String message, Throwable cause) {
        super(message, cause);
    }

}
