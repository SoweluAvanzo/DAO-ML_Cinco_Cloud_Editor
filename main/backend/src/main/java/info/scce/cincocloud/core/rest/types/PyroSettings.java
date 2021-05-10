package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroSettingsDB;

public class PyroSettings extends info.scce.cincocloud.rest.RESTBaseImpl {

    private PyroStyle style;
    private boolean globallyCreateOrganizations;

    public static PyroSettings fromEntity(
            final PyroSettingsDB entity,
            final info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }

        final PyroSettings result;
        result = new PyroSettings();
        result.setId(entity.id);
        result.setstyle(PyroStyle.fromEntity(entity.style, objectCache));
        result.setgloballyCreateOrganizations(entity.globallyCreateOrganizations);

        objectCache.putRestTo(entity, result);

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public PyroStyle getstyle() {
        return this.style;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public void setstyle(final PyroStyle style) {
        this.style = style;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("globallyCreateOrganizations")
    public boolean getgloballyCreateOrganizations() {
        return this.globallyCreateOrganizations;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("globallyCreateOrganizations")
    public void setgloballyCreateOrganizations(final boolean globallyCreateOrganizations) {
        this.globallyCreateOrganizations = globallyCreateOrganizations;
    }
}