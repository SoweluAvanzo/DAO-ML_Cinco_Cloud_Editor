package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.db.SettingsDB;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Map;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class RegistrationControllerTest extends AbstractCincoCloudTest {

  private final Map<String, String> defaultHeaders = Map.of("content-type", "application/json");

  @BeforeEach
  public void setup() {
    reset();
  }

  @Test
  public void register_userIsValid_200() {
    given()
        .when()
        .body(createUserJson("test", "test@test.de", "test", "123456"))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(200)
        .body(is("Activation mail send"));
  }

  @Test
  public void register_registrationIsDisabled_403() {
    disablePublicUserRegistration();
    given()
        .when()
        .body(createUserJson("test", "test@test.de", "test", "123456"))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(403);
  }

  @Test
  public void register_passwordConfirmIsInvalid_400() {
    given()
        .when()
        .body(createUserJson("test", "test@test.de", "test", "123456", "234567"))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(400);
  }

  @ParameterizedTest(name = "Password {0} is too short.")
  @ValueSource(strings = {"", "1234"})
  public void register_passwordTooShort_400(String password) {
    given()
        .when()
        .body(createUserJson("test", "test@test.de", "test", password))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(400);
  }

  @ParameterizedTest(name = "Email {0} is invalid.")
  @ValueSource(strings = {"", "test", "test@"})
  public void register_invalidEmail_400(String email) {
    given()
        .when()
        .body(createUserJson("test", email, "test", "123345"))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_nameIsEmpty_400() {
    given()
        .when()
        .body(createUserJson("test", "test@test.de", "", "123345"))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_usernameIsEmpty_400() {
    given()
        .when()
        .body(createUserJson("", "test@test.de", "test", "123345"))
        .headers(defaultHeaders)
        .post("/api/register/new/public")
        .then()
        .statusCode(400);
  }

  @Transactional
  public void disablePublicUserRegistration() {
    var settings = (SettingsDB) SettingsDB.findAll().list().get(0);
    settings.allowPublicUserRegistration = false;
    settings.persistAndFlush();
  }

  private String createUserJson(String username, String email, String name, String password) {
    return createUserJson(username, email, name, password, password);
  }

  private String createUserJson(String username, String email, String name, String password, String passwordConfirm) {
    return String.format(
        "{\"username\":\"%s\", \"email\": \"%s\", \"name\": \"%s\", \"password\": \"%s\", \"passwordConfirm\": \"%s\"}",
        username, email, name, password, passwordConfirm);
  }
}
