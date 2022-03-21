package info.scce.cincocloud.core;

public class JsonUtils {
  public static String createUserRegistrationJson(String username, String email, String name, String password) {
    return createUserRegistrationJson(username, email, name, password, password);
  }

  public static String createUserRegistrationJson(String username, String email, String name, String password, String passwordConfirm) {
    return String.format(
        "{\"username\":\"%s\", \"email\": \"%s\", \"name\": \"%s\", \"password\": \"%s\", \"passwordConfirm\": \"%s\"}",
        username, email, name, password, passwordConfirm);
  }

  public static String createOrganizationJson(String name, String description) {
    return String.format(
        "{\"@id\":\"1\",\"id\":-1,\"name\":\"%s\",\"description\":\"%s\",\"owners\":[],\"members\":[],\"projects\":[],\"runtimeType\":\"info.scce.cincocloud.core.rest.tos.OrganizationTO\"}",
        name, description
    );
  }
}
