package info.scce.cincocloud.core.rest.types;

import info.scce.cincocloud.db.PyroOrganizationAccessRightDB;
import info.scce.cincocloud.db.PyroOrganizationAccessRightVectorDB;

public class PyroOrganizationAccessRightVector extends info.scce.cincocloud.rest.RESTBaseImpl {

    private java.util.List<PyroOrganizationAccessRightDB> accessRights = new java.util.LinkedList<>();
    private PyroUser user;
    private PyroOrganization organization;

    public static PyroOrganizationAccessRightVector fromEntity(
            final PyroOrganizationAccessRightVectorDB entity,
            final info.scce.cincocloud.rest.ObjectCache objectCache) {

        if (objectCache.containsRestTo(entity)) {
            return objectCache.getRestTo(entity);
        }

        final PyroOrganizationAccessRightVector result;
        result = new PyroOrganizationAccessRightVector();
        result.setId(entity.id);

        result.setuser(PyroUser.fromEntity(entity.user, objectCache));
        result.setorganization(PyroOrganization.fromEntity(entity.organization, objectCache));

        objectCache.putRestTo(entity, result);

        for (PyroOrganizationAccessRightDB ar : entity.accessRights) {
            result.getaccessRights().add(ar);
        }

        return result;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("accessRights")
    public java.util.List<PyroOrganizationAccessRightDB> getaccessRights() {
        return this.accessRights;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("accessRights")
    public void setaccessRights(final java.util.List<PyroOrganizationAccessRightDB> accessRights) {
        this.accessRights = accessRights;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public PyroUser getuser() {
        return this.user;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("user")
    public void setuser(final PyroUser user) {
        this.user = user;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("organization")
    public PyroOrganization getorganization() {
        return this.organization;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("organization")
    public void setorganization(final PyroOrganization organization) {
        this.organization = organization;
    }
}