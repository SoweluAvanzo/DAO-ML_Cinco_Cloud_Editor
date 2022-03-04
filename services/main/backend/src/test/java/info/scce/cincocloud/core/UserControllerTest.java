package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.UserDB;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserControllerTest extends AbstractCincoCloudTest {

  @Inject
  AuthService authService;

  @Inject
  ObjectMapper objectMapper;

  String jwtAdmin;
  String jwtUserA;

  UserDB admin;
  UserDB userA;

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

    jwtUserA = authService.login(new UserLoginInput("userA@cincocloud", "123456")).token;
    this.userA = UserDB.find("username = 'userA'").firstResult();

    jwtAdmin = authService.login(new UserLoginInput("admin@cincocloud", "123456")).token;
    this.admin = UserDB.find("username = 'admin'").firstResult();
  }

  @Test
  public void createUser_subjectIsAdmin_userIsCreated() throws Exception {
    final var testUser = createTestUser();

    final var createdUser = given()
        .when()
        .body(objectMapper.writeValueAsString(testUser))
        .headers(getAuthHeaders(jwtAdmin))
        .post("/api/users/private")
        .then()
        .statusCode(201)
        .extract()
        .body()
        .as(UserTO.class);

    assertNotNull(createdUser);
    assertEquals(testUser.getName(), createdUser.getname());
    assertEquals(testUser.getUsername(), createdUser.getusername());
    assertEquals(testUser.getEmail(), createdUser.getemail());
    assertNotNull(UserDB.find("username = 'test'").firstResult());
  }

  @Test
  public void createUser_subjectIsNotAdmin_userIsNotCreated() throws Exception {
    final var testUser = createTestUser();

    final var bodyAsString = given()
        .when()
        .body(objectMapper.writeValueAsString(testUser))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/users/private")
        .then()
        .statusCode(403)
        .extract()
        .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
  }

  private UserRegistrationInput createTestUser() {
    final var testUser = new UserRegistrationInput();
    testUser.setName("test");
    testUser.setUsername("test");
    testUser.setEmail("test@cincocloud");
    testUser.setPassword("123456");
    testUser.setPasswordConfirm("123456");
    return testUser;
  }
}
