package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectUsersInput;
import info.scce.cincocloud.core.rest.inputs.UpdateUserRolesInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.core.services.AuthService;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserControllerTest extends AbstractCincoCloudTest {

  @Inject
  AuthService authService;

  @Inject ObjectMapper objectMapper;

  @Inject ObjectCache objectCache;

  String jwtAdminA;
  String jwtUserA;

  UserDB adminA;
  UserDB userA;

  @BeforeEach
  @Transactional
  public void setup() {
    reset();

    final var userA = registrationService.registerUser("userA", "userA", "userA@cincocloud", "123456");
    userService.activateUser(userA, false);

    jwtUserA = authService.login("userA@cincocloud", "123456");
    this.userA = UserDB.find("username = 'userA'").firstResult();

    jwtAdminA = authService.login("admin@cincocloud", "123456");
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
            .post("/api/users")
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
            .post("/api/users")
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
            .post("/api/users")
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
            .post("/api/users")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    String createdUserJwt = authService.login("test@cincocloud", "123456");

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
            .post("/api/users")
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
            .post("/api/users")
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
            .post("/api/users")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var jwtCreatedUser = authService.login("test@cincocloud", "123456");

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
            .post("/api/users")
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
            .post("/api/users")
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
                    .post("/api/users")
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
            .post("/api/users")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var jwtCreatedUser = authService.login("test@cincocloud", "123456");

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
            .post("/api/users")
            .then()
            .statusCode(201)
            .extract()
            .body()
            .as(UserTO.class);

    final var jwtCreatedUser = authService.login("test@cincocloud", "123456");

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
        .post("/api/projects")
        .then()
        .statusCode(201);

    return ProjectDB.find("name = ?1", name).firstResult();
  }

  private OrganizationDB createOrganization(String name, String jwt) {
    given()
        .when()
        .body(createOrganizationJson(name, ""))
        .headers(getAuthHeaders(jwt))
        .post("/api/organizations")
        .then()
        .statusCode(201);

    return OrganizationDB.find("name = ?1", name).firstResult();
  }

  private void addOrganizationMember(OrganizationDB org, UserDB user, String jwt, int expectedStatus) throws Exception {
    final var input = new UpdateProjectUsersInput();
    input.setUserId(user.id);
    given()
        .when()
        .body(objectMapper.writeValueAsString(input))
        .headers(getAuthHeaders(jwt))
        .post("/api/organizations/" + org.id + "/members")
        .then()
        .statusCode(expectedStatus);
  }


  private void addOrganizationOwner(OrganizationDB org, UserDB user, String jwt, int expectedStatus) throws Exception {
    final var input = new UpdateProjectUsersInput();
    input.setUserId(user.id);
    given()
        .when()
        .body(objectMapper.writeValueAsString(input))
        .headers(getAuthHeaders(jwt))
        .post("/api/organizations/" + org.id + "/owners")
        .then()
        .statusCode(expectedStatus);
  }

  private void addProjectMember(ProjectDB project, UserDB user, String jwt, int expectedStatus) throws Exception {
    final var input = new UpdateProjectUsersInput();
    input.setUserId(user.id);
    given()
        .when()
        .body(objectMapper.writeValueAsString(input))
        .headers(getAuthHeaders(jwt))
        .post("/api/projects/" + project.id + "/members")
        .then()
        .statusCode(expectedStatus);
  }

  private UserDB makeAdmin(UserDB user, String jwt) throws Exception {
    final var input = new UpdateUserRolesInput();
    input.setAdmin(true);
    given()
        .when()
        .body(objectMapper.writeValueAsString(input))
        .headers(getAuthHeaders(jwt))
        .put("/api/users/" + user.id + "/roles")
        .then()
        .statusCode(200);

    return UserDB.find("name = ?1", user.name).firstResult();
  }
}
