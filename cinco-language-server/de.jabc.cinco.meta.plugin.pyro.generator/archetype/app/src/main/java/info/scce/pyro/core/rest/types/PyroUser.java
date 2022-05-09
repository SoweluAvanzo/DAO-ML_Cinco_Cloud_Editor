package info.scce.pyro.core.rest.types;

/**
 * Author zweihoff
 */

public class PyroUser extends info.scce.pyro.rest.RESTBaseImpl {   
    
    
   
    
    private java.lang.String username;

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public java.lang.String getusername() {
        return this.username;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("username")
    public void setusername(final java.lang.String username) {
        this.username = username;
    }
    
    private java.lang.String email;

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public java.lang.String getemail() {
        return this.email;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("email")
    public void setemail(final java.lang.String email) {
        this.email = email;
    }
    
    private String profilePicture;

    @com.fasterxml.jackson.annotation.JsonProperty("profilePicture")
    public String getprofilePicture() {
        return this.profilePicture;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("profilePicture")
    public void setprofilePicture(final String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public static PyroUser fromEntity(final entity.core.PyroUserDB entity, info.scce.pyro.rest.ObjectCache objectCache) {

        final PyroUser result;
        result = new PyroUser();
        result.setId(entity.id);

        result.setemail(entity.email);
        result.setusername(entity.username);
        result.setprofilePicture(entity.profilePicture);
        

        return result;
    }
}