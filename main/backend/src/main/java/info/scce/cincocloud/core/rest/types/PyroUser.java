package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;

public class PyroUser extends info.scce.cincocloud.rest.RESTBaseImpl {

    private java.util.List<PyroProject> ownedProjects = new java.util.LinkedList<>();
    private java.util.List<PyroSystemRoleDB> systemRoles = new java.util.LinkedList<>();
    private java.lang.String username;
    private java.lang.String email;
    private FileReference profilePicture;

    public static PyroUser fromEntity(final PyroUserDB entity, info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }
        final PyroUser result;
        result = new PyroUser();
        result.setId(entity.id);

        result.setemail(entity.email);
        result.setusername(entity.username);

        if (entity.profilePicture != null) {
            result.setprofilePicture(new FileReference(entity.profilePicture));
        }

        objectCache.putRestTo(entity, result);

        for (PyroProjectDB p : entity.ownedProjects) {
            result.getownedProjects().add(PyroProject.fromEntity(p, objectCache));
        }

        for (PyroSystemRoleDB p : entity.systemRoles) {
            result.getsystemRoles().add(p);
        }

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("ownedProjects")
    public java.util.List<PyroProject> getownedProjects() {
        return this.ownedProjects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("ownedProjects")
    public void setownedProjects(final java.util.List<PyroProject> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("systemRoles")
    public java.util.List<PyroSystemRoleDB> getsystemRoles() {
        return this.systemRoles;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("systemRoles")
    public void setsystemRoles(final java.util.List<PyroSystemRoleDB> systemRoles) {
        this.systemRoles = systemRoles;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public java.lang.String getusername() {
        return this.username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public void setusername(final java.lang.String username) {
        this.username = username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public java.lang.String getemail() {
        return this.email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public void setemail(final java.lang.String email) {
        this.email = email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("profilePicture")
    public FileReference getprofilePicture() {
        return this.profilePicture;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("profilePicture")
    public void setprofilePicture(final FileReference profilePicture) {
        this.profilePicture = profilePicture;
    }
}