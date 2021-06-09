package ru.a_z.gradle.plugins.mapstruct;

/**
 * Mapstruct plugin configuration class based on
 * <a href="https://mapstruct.org/documentation/stable/reference/html/#configuration-options">official site</a>
 */
@SuppressWarnings("unused")
public class MapstructExtension {

    private boolean suppressGeneratorTimestamp = false;
    private boolean verbose = false;
    private boolean suppressGeneratorVersionInfoComment = false;
    private String defaultComponentModel = "default";
    private String defaultInjectionStrategy = "field";
    private String unmappedTargetPolicy = "WARN";

    public boolean getSuppressGeneratorTimestamp() {
        return suppressGeneratorTimestamp;
    }

    public void setSuppressGeneratorTimestamp(boolean suppressGeneratorTimestamp) {
        this.suppressGeneratorTimestamp = suppressGeneratorTimestamp;
    }

    public boolean getSuppressGeneratorVersionInfoComment() {
        return suppressGeneratorVersionInfoComment;
    }

    public void setSuppressGeneratorVersionInfoComment(boolean suppressGeneratorVersionInfoComment) {
        this.suppressGeneratorVersionInfoComment = suppressGeneratorVersionInfoComment;
    }

    public boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getDefaultComponentModel() {
        return defaultComponentModel;
    }

    public void setDefaultComponentModel(String defaultComponentModel) {
        this.defaultComponentModel = defaultComponentModel;
    }

    public String getDefaultInjectionStrategy() {
        return defaultInjectionStrategy;
    }

    public void setDefaultInjectionStrategy(String defaultInjectionStrategy) {
        this.defaultInjectionStrategy = defaultInjectionStrategy;
    }

    public String getUnmappedTargetPolicy() {
        return unmappedTargetPolicy;
    }

    public void setUnmappedTargetPolicy(String unmappedTargetPolicy) {
        this.unmappedTargetPolicy = unmappedTargetPolicy;
    }

}
