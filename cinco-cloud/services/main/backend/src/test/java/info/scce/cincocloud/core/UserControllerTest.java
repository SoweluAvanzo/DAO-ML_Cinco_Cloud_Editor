package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserControllerTest extends AbstractCincoCloudTest {

  @Inject AuthService authService;

  @Inject ObjectMapper objectMapper;

  @Inject ObjectCache objectCache;

  String jwtAdminA;
  String jwtUserA;

  UserDB adminA;
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

    jwtAdminA = authService.login(new UserLoginInput("admin@cincocloud", "123456")).token;
    this.adminA = UserDB.find("username = 'admin'").firstResult();
  }

  @Test
  public void createUser_subjectIsAdmin_userIsCreated() throws Exception {
    final var testUser = createTestUser();

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
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

    final var bodyAsString =
        given()
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

  @Test
  public void deleteUser_userIsDeleted() throws Exception {
    final var testUser = createTestUser();

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
  }

  @Test
  public void deleteUser_userDeletesThemselves_userIsDeleted() throws Exception {
    final var testUser = createTestUser();

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    String createdUserJwt = authService.login(new UserLoginInput("test@cincocloud", "123456")).token;

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(createdUserJwt))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
  }

  @Test
  public void deleteUser_userIsOnlyAdmin_userIsNotDeleted() {
    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + adminA.id)
            .then()
            .statusCode(400)
            .extract()
            .asString();

    assertNotEquals("", bodyAsString);
    assertNotNull(UserDB.find("username = 'admin'").firstResult());
  }

  @Test
  public void deleteUser_otherAdminsExist_userIsDeleted() throws Exception {
    final var testUser = createTestUser();

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    UserDB user = UserDB.find("username = 'test'").firstResult();
    user = makeAdmin(user, jwtAdminA);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + user.id)
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
  }

  @Test
  public void deleteUser_userIsOrgMember_userIsDeleted() throws Exception {
    final var testUser = createTestUser();
    var organization = createOrganization("test_a", jwtUserA);
    var organization2 = createOrganization("test_b", jwtUserA);

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final UserDB user = UserDB.find("username = 'test'").firstResult();

    addOrganizationMember(organization, user, jwtUserA, 200);
    addOrganizationMember(organization2, user, jwtUserA, 200);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
    assertFalse(((OrganizationDB) OrganizationDB.find("name = 'test_a'").firstResult())
            .members.stream().anyMatch(u -> u.id.equals(user.id)));
    assertFalse(((OrganizationDB) OrganizationDB.find("name = 'test_b'").firstResult())
            .members.stream().anyMatch(u -> u.id.equals(user.id)));
  }

  @Test
  public void deleteUser_userIsOrgOwner_OrgHasNoOtherOwners_userIsNotDeleted() throws Exception {
    final var testUser = createTestUser();
    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var jwtCreatedUser = authService.login(new UserLoginInput("test@cincocloud", "123456")).token;

    createOrganization("test_a", jwtCreatedUser);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(400)
            .extract()
            .asString();

    assertNotEquals("", bodyAsString);
    assertNotNull(UserDB.find("username = 'test'").firstResult());
    assertTrue(((OrganizationDB) OrganizationDB.find("name = 'test_a'").firstResult())
            .owners.stream().anyMatch(u -> u.id == createdUser.getId()));
  }

  @Test
  public void deleteUser_userIsOrgOwner_OrgHasOtherOwners_userIsDeleted() throws Exception {
    final var testUser = createTestUser();
    var organization = createOrganization("test_a", jwtUserA);

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final UserDB user = UserDB.find("username = 'test'").firstResult();

    addOrganizationOwner(organization, user, jwtUserA, 200);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
    assertFalse(((OrganizationDB) OrganizationDB.find("name = 'test_a'").firstResult())
            .members.stream().anyMatch(u -> u.id.equals(user.id)));
  }

  @Test
  public void deleteUser_userIsProjectMember_userIsDeleted() throws Exception {
    final var testUser = createTestUser();
    var project = createProject("test_a", jwtUserA);
    var project2 = createProject("test_b", jwtUserA);

    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final UserDB user = UserDB.find("username = 'test'").firstResult();

    addProjectMember(project, user, jwtUserA, 200);
    addProjectMember(project2, user, jwtUserA, 200);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
    assertFalse(((ProjectDB) ProjectDB.find("name = 'test_a'").firstResult())
        .members.stream().anyMatch(u -> u.id.equals(user.id)));
    assertFalse(((ProjectDB) ProjectDB.find("name = 'test_b'").firstResult())
            .members.stream().anyMatch(u -> u.id.equals(user.id)));
  }

  @Test
  public void deleteUser_userIsProjectAndOrgMember_userIsDeleted() throws Exception {
    final var testUser = createTestUser();
    var project = createProject("test_a", jwtUserA);
    var organization = createOrganization("test_b", jwtUserA);

    final var createdUser =
            given()
                    .when()
                    .body(objectMapper.writeValueAsString(testUser))
                    .headers(getAuthHeaders(jwtAdminA))
                    .post("/api/users/private")
                    .then()
                    .statusCode(201)
                    .extract()
                    .body()
                    .as(UserTO.class);

    final UserDB user = UserDB.find("username = 'test'").firstResult();

    addProjectMember(project, user, jwtUserA, 200);
    addOrganizationMember(organization, user, jwtUserA, 200);

    final var bodyAsString =
            given()
                    .when()
                    .headers(getAuthHeaders(jwtAdminA))
                    .delete("/api/users/" + createdUser.getId())
                    .then()
                    .statusCode(200)
                    .extract()
                    .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
    assertFalse(((ProjectDB) ProjectDB.find("name = 'test_a'").firstResult())
            .members.stream().anyMatch(u -> u.id.equals(user.id)));
    assertFalse(((OrganizationDB) OrganizationDB.find("name = 'test_b'").firstResult())
            .members.stream().anyMatch(u -> u.id.equals(user.id)));
  }

  @Test
  public void deleteUser_userIsProjectOwner_projectHasOtherMembers_userIsNotDeleted() throws Exception {
    final var testUser = createTestUser();
    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var jwtCreatedUser = authService.login(new UserLoginInput("test@cincocloud", "123456")).token;

    var project = createProject("test_a", jwtCreatedUser);
    addProjectMember(project, userA, jwtCreatedUser, 200);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(400)
            .extract()
            .asString();

    assertNotEquals("", bodyAsString);
    assertNotNull(UserDB.find("username = 'test'").firstResult());
    assertEquals(((ProjectDB) ProjectDB.find("name = 'test_a'").firstResult()).owner.id, createdUser.getId());
  }

  @Test
  public void deleteUser_userIsProjectOwner_projectHasNoOtherMembers_userIsDeleted() throws Exception {
    final var testUser = createTestUser();
    final var createdUser =
        given()
            .when()
            .body(objectMapper.writeValueAsString(testUser))
            .headers(getAuthHeaders(jwtAdminA))
            .post("/api/users/private")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var jwtCreatedUser = authService.login(new UserLoginInput("test@cincocloud", "123456")).token;

    createProject("test_a", jwtCreatedUser);

    final var bodyAsString =
        given()
            .when()
            .headers(getAuthHeaders(jwtAdminA))
            .delete("/api/users/" + createdUser.getId())
            .then()
            .statusCode(200)
            .extract()
            .asString();

    assertEquals("", bodyAsString);
    assertNull(UserDB.find("username = 'test'").firstResult());
    assertNull(ProjectDB.find("name = 'test_a'").firstResult());
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

  private String createOrganizationJson(String name, String description) {
    return String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
  }

  private String createProjectJson(String name, String description) {
    return String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
  }

  private ProjectDB createProject(String name, String jwt) {
    given()
        .when()
        .body(createProjectJson(name, ""))
        .headers(getAuthHeaders(jwt))
        .post("/api/project/create/private")
        .then()
        .statusCode(200);

    return ProjectDB.find("name = ?1", name).firstResult();
  }

  private OrganizationDB createOrganization(String name, String jwt) {
    given()
        .when()
        .body(createOrganizationJson(name, ""))
        .headers(getAuthHeaders(jwt))
        .post("/api/organization")
        .then()
        .statusCode(200);

    return OrganizationDB.find("name = ?1", name).firstResult();
  }

  private void addOrganizationMember(OrganizationDB org, UserDB user, String jwt, int expectedStatus) throws Exception {
    given()
        .when()
        .body(objectMapper.writeValueAsString(UserTO.fromEntity(user, objectCache)))
        .headers(getAuthHeaders(jwt))
        .post("/api/organization/" + org.id + "/addMember")
        .then()
        .statusCode(expectedStatus);
  }


  private void addOrganizationOwner(OrganizationDB org, UserDB user, String jwt, int expectedStatus) throws Exception {
    given()
        .when()
        .body(objectMapper.writeValueAsString(UserTO.fromEntity(user, objectCache)))
        .headers(getAuthHeaders(jwt))
        .post("/api/organization/" + org.id + "/addOwner")
        .then()
        .statusCode(expectedStatus);
  }

  private void addProjectMember(ProjectDB project, UserDB user, String jwt, int expectedStatus) throws Exception {
    given()
        .when()
        .body(objectMapper.writeValueAsString(UserTO.fromEntity(user, objectCache)))
        .headers(getAuthHeaders(jwt))
        .post("/api/project/" + project.id + "/member/private")
        .then()
        .statusCode(expectedStatus);
  }

  private UserDB makeAdmin(UserDB user, String jwt) {
    given()
        .when()
        .headers(getAuthHeaders(jwt))
        .post("/api/users/" + user.id + "/roles/addAdmin")
        .then()
        .statusCode(200);

    return UserDB.find("name = ?1", user.name).firstResult();
  }
}
