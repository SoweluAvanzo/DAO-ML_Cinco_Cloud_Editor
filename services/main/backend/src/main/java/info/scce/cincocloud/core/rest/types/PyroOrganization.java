package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroOrganizationDB;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroUserDB;

public class PyroOrganization extends info.scce.cincocloud.rest.RESTBaseImpl {

    private java.lang.String name;
    private java.lang.String description;
    private java.util.List<PyroUser> owners = new java.util.LinkedList<>();
    private java.util.List<PyroUser> members = new java.util.LinkedList<>();
    private java.util.List<PyroProject> projects = new java.util.LinkedList<>();
    private PyroStyle style;

    public static PyroOrganization fromEntity(
            final PyroOrganizationDB entity,
            final info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }

        final PyroOrganization result;
        result = new PyroOrganization();
        result.setId(entity.id);

        result.setname(entity.name);
        result.setdescription(entity.description);
        result.setstyle(PyroStyle.fromEntity(entity.style, objectCache));
        objectCache.putRestTo(entity, result);

        for (PyroUserDB o : entity.owners) {
            result.getowners().add(PyroUser.fromEntity(o, objectCache));
        }

        for (PyroUserDB m : entity.members) {
            result.getmembers().add(PyroUser.fromEntity(m, objectCache));
        }

        for (PyroProjectDB p : entity.projects) {
            result.getprojects().add(PyroProject.fromEntity(p, objectCache));
        }

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public java.lang.String getname() {
        return this.name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("name")
    public void setname(final java.lang.String name) {
        this.name = name;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public java.lang.String getdescription() {
        return this.description;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("description")
    public void setdescription(final java.lang.String description) {
        this.description = description;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owners")
    public java.util.List<PyroUser> getowners() {
        return this.owners;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("owners")
    public void setowners(final java.util.List<PyroUser> owners) {
        this.owners = owners;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("members")
    public java.util.List<PyroUser> getmembers() {
        return this.members;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("members")
    public void setmembers(final java.util.List<PyroUser> members) {
        this.members = members;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("projects")
    public java.util.List<PyroProject> getprojects() {
        return this.projects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("projects")
    public void setprojects(final java.util.List<PyroProject> projects) {
        this.projects = projects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public PyroStyle getstyle() {
        return this.style;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("style")
    public void setstyle(final PyroStyle style) {
        this.style = style;
    }
}