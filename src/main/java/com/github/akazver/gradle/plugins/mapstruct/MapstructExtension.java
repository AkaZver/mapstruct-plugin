package com.github.akazver.gradle.plugins.mapstruct;

import lombok.Getter;
import lombok.Setter;

/**
 * Mapstruct plugin configuration class based on
 * <a href="https://mapstruct.org/documentation/stable/reference/html/#configuration-options">official site</a>
 *
 * @author Vasiliy Sobolev
 */
@Getter
@Setter
public class MapstructExtension {

    private boolean suppressGeneratorTimestamp = false;
    private boolean verbose = false;
    private boolean suppressGeneratorVersionInfoComment = false;
    private String defaultComponentModel = "default";
    private String defaultInjectionStrategy = "field";
    private String unmappedTargetPolicy = "WARN";
    private String unmappedSourcePolicy = "WARN";
    private boolean disableBuilders = false;
    private String nullValueIterableMappingStrategy = "RETURN_NULL";
    private String nullValueMapMappingStrategy = "RETURN_NULL";

}
