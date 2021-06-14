package com.github.akazver.gradle.plugins.mapstruct;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Arguments for compiler based on SupportedOptions in mapstruct-processor
 */
public enum MapstructArgument {

    SUPPRESS_GENERATOR_TIMESTAMP("suppressGeneratorTimestamp",
            extension -> extension::getSuppressGeneratorTimestamp),

    VERBOSE("verbose",
            extension -> extension::getVerbose),

    SUPPRESS_GENERATOR_VERSION_INFO_COMMENT("suppressGeneratorVersionInfoComment",
            extension -> extension::getSuppressGeneratorVersionInfoComment),

    DEFAULT_COMPONENT_MODEL("defaultComponentModel",
            extension -> extension::getDefaultComponentModel),

    DEFAULT_INJECTION_STRATEGY("defaultInjectionStrategy",
            extension -> extension::getDefaultInjectionStrategy),

    UNMAPPED_TARGET_POLICY("unmappedTargetPolicy",
            extension -> extension::getUnmappedTargetPolicy);

    private final String name;
    private final Function<MapstructExtension, Supplier<Object>> accessor;

    MapstructArgument(String name, Function<MapstructExtension, Supplier<Object>> accessor) {
        this.name = name;
        this.accessor = accessor;
    }

    public String getName() {
        return name;
    }

    public Function<MapstructExtension, Supplier<Object>> getAccessor() {
        return accessor;
    }

}
