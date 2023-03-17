package info.scce.cincocloud.core;

import static info.scce.cincocloud.core.JsonUtils.createOrganizationJson;
import static io.restassured.RestAssured.given;

import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.services.AuthService;
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

    createAndActivateUser("userA", "userA", "userA@cincocloud", "123456");

    jwtUserA = authService.login("userA@cincocloud", "123456");
  }

  @Test
  public void create_201() {
    given()
        .when()
        .body(createOrganizationJson("test", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organizations")
        .then()
        .statusCode(201);
  }

  @Test
  public void create_duplicateOrganizationName_400() {
    given()
        .when()
        .body(createOrganizationJson("test", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organizations")
        .then()
        .statusCode(201);

    given()
        .when()
        .body(createOrganizationJson("TeSt", ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/organizations")
        .then()
        .statusCode(400);
  }
}
