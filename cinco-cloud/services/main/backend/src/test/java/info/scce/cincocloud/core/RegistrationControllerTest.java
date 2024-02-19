package info.scce.cincocloud.core;

import static info.scce.cincocloud.core.JsonUtils.createOrganizationJson;
import static info.scce.cincocloud.core.JsonUtils.createUserRegistrationJson;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.services.AuthService;
import info.scce.cincocloud.db.SettingsDB;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
public class RegistrationControllerTest extends AbstractCincoCloudTest {

  @Inject
  AuthService authService;

  @BeforeEach
  public void setup() {
    reset();
  }

  @Test
  public void register_userIsValid_200() {
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "test", "123456"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(200)
        .body("name", is("test"),
                "email", is("test@test.de"),
                "username", is("test"));
  }

  @Test
  public void register_registrationIsDisabled_403() {
    disablePublicUserRegistration();
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "test", "123456"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(403);
  }

  @Test
  public void register_passwordConfirmIsInvalid_400() {
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "test", "123456", "234567"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @ParameterizedTest(name = "Password {0} is too short.")
  @ValueSource(strings = {"", "1234"})
  public void register_passwordTooShort_400(String password) {
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "test", password))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @ParameterizedTest(name = "Email {0} is invalid.")
  @ValueSource(strings = {"", "test", "test@"})
  public void register_invalidEmail_400(String email) {
    given()
        .when()
        .body(createUserRegistrationJson("test", email, "test", "123345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_nameIsEmpty_400() {
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "", "123345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_usernameIsEmpty_400() {
    given()
        .when()
        .body(createUserRegistrationJson("", "test@test.de", "test", "123345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_duplicateUsername_400() {
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "test", "12345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(200);

    given()
        .when()
        .body(createUserRegistrationJson("TeSt", "test2@test.de", "test", "12345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_duplicateEmail_400() {
    given()
        .when()
        .body(createUserRegistrationJson("test", "test@test.de", "test", "12345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(200);

    given()
        .when()
        .body(createUserRegistrationJson("test2", "TeSt@test.de", "test", "12345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @Test
  public void register_duplicateOrganizationAndUsername_400() {
    createAndActivateUser("userA", "userA", "userA@cincocloud", "123456");

    String jwtUserA = authService.login("userA@cincocloud", "123456");

    given()
        .when()
        .body(createOrganizationJson("test", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organizations")
        .then()
        .statusCode(201);

    given()
        .when()
        .body(createUserRegistrationJson("TeSt", "test@test.de", "test", "12345"))
        .headers(defaultHeaders)
        .post("/api/register")
        .then()
        .statusCode(400);
  }

  @Transactional
  public void disablePublicUserRegistration() {
    var settings = (SettingsDB) SettingsDB.findAll().list().get(0);
    settings.allowPublicUserRegistration = false;
    settings.persistAndFlush();
  }
}
