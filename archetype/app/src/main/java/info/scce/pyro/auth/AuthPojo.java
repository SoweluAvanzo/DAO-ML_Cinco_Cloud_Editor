package info.scce.pyro.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Author zweihoff
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthPojo {
    public String username;
    public String email;
    public String id;
    public String profile_image;
}