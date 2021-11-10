package info.scce.cincocloud.core.rest.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import info.scce.cincocloud.db.PyroProjectDB;
import info.scce.cincocloud.db.PyroSystemRoleDB;
import info.scce.cincocloud.db.PyroUserDB;
import info.scce.cincocloud.rest.ObjectCache;
import info.scce.cincocloud.rest.RESTBaseImpl;
import java.util.List;

public class PyroUser extends RESTBaseImpl {

    private List<PyroProject> ownedProjects = new java.util.LinkedList<>();
    private List<PyroSystemRoleDB> systemRoles = new java.util.LinkedList<>();
    private String username;
    private String email;
    private FileReference profilePicture;

    public static PyroUser fromEntity(final PyroUserDB entity, final ObjectCache objectCache) {
        
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

    @JsonProperty("ownedProjects")
    public List<PyroProject> getownedProjects() {
        return this.ownedProjects;
    }

    @JsonProperty("ownedProjects")
    public void setownedProjects(final List<PyroProject> ownedProjects) {
        this.ownedProjects = ownedProjects;
    }

    @JsonProperty("systemRoles")
    public List<PyroSystemRoleDB> getsystemRoles() {
        return this.systemRoles;
    }

    @JsonProperty("systemRoles")
    public void setsystemRoles(final List<PyroSystemRoleDB> systemRoles) {
        this.systemRoles = systemRoles;
    }

    @JsonProperty("username")
    public String getusername() {
        return this.username;
    }

    @JsonProperty("username")
    public void setusername(final String username) {
        this.username = username;
    }

    @JsonProperty("email")
    public String getemail() {
        return this.email;
    }

    @JsonProperty("email")
    public void setemail(final String email) {
        this.email = email;
    }

    @JsonProperty("profilePicture")
    public FileReference getprofilePicture() {
        return this.profilePicture;
    }

    @JsonProperty("profilePicture")
    public void setprofilePicture(final FileReference profilePicture) {
        this.profilePicture = profilePicture;
    }
}