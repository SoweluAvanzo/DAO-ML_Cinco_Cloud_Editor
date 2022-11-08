package info.scce.cincocloud.core;

import static info.scce.cincocloud.core.JsonUtils.createOrganizationJson;
import static io.restassured.RestAssured.given;

import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class OrganizationControllerTest extends AbstractCincoCloudTest {

  @Inject
  AuthService authService;

  String jwtUserA;

  @BeforeEach
  public void setup() {
    reset();

    final var userA = new UserRegistrationInput();
    userA.setEmail("userA@cincocloud");
    userA.setName("userA");
    userA.setUsername("userA");
    userA.setPassword("123456");
    userA.setPasswordConfirm("123456");
    registrationService.registerUser(userA);

    final var userALogin = new UserLoginInput();
    userALogin.emailOrUsername = "userA@cincocloud";
    userALogin.password = "123456";

    jwtUserA = authService.login(userALogin).token;
  }

  @Test
  public void create_200() {
    given()
        .when()
        .body(createOrganizationJson("test", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organization")
        .then()
        .statusCode(200);
  }

  @Test
  public void create_duplicateOrganizationName_400() {
    given()
        .when()
        .body(createOrganizationJson("test", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organization")
        .then()
        .statusCode(200);

    given()
        .when()
        .body(createOrganizationJson("TeSt", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organization")
        .then()
        .statusCode(400);
  }
}
