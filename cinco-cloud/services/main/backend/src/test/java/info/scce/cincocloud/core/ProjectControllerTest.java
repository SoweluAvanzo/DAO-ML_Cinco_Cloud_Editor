package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProjectControllerTest extends AbstractCincoCloudTest {

  @Inject
  AuthService authService;

  @Inject
  ObjectMapper objectMapper;

  @Inject
  ObjectCache objectCache;

  String jwtUserA;
  String jwtUserB;

  UserDB userA;
  UserDB userB;

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

    final var userB = new UserRegistrationInput();
    userB.setEmail("userB@cincocloud");
    userB.setName("userB");
    userB.setUsername("userB");
    userB.setPassword("123456");
    userB.setPasswordConfirm("123456");
    registrationService.registerUser(userB);

    final var userALogin = new UserLoginInput();
    userALogin.email = "userA@cincocloud";
    userALogin.password = "123456";

    final var userBLogin = new UserLoginInput();
    userBLogin.email = "userB@cincocloud";
    userBLogin.password = "123456";

    jwtUserA = authService.login(userALogin).token;
    jwtUserB = authService.login(userBLogin).token;

    this.userA = UserDB.find("username = 'userA'").firstResult();
    this.userB = UserDB.find("username = 'userB'").firstResult();
  }

  @Test
  public void createProject_createWithoutOrganization_userOwnsProject() {
    final var projectName = "test";

    given()
        .when()
        .body(createProjectJson(projectName, ""))
        .headers(getAuthHeaders(jwtUserA))
        .post("/api/project/create/private")
        .then()
        .statusCode(200);

    final var project = getProjectByName(projectName);
    assertNotNull(project);
    assertNull(project.organization);
    assertEquals(userA, project.owner);
  }

  @Test
  public void addMember_userCanBeAdded_projectHasUserAsMember() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    addProjectMember(project, userB, jwtUserA, 200);

    final var updatedProject = getProjectByName(projectName);
    assertEquals(1, updatedProject.members.size());
    assertTrue(updatedProject.members.contains(userB));
  }

  @Test
  public void addMember_userIsUnknown_404() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    final var unknownUser = new UserDB();
    unknownUser.id = -1L;

    addProjectMember(project, unknownUser, jwtUserA, 404);

    assertEquals(0,  getProjectByName(projectName).members.size());
  }

  @Test
  public void addMember_userIsAddedTwice_400() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    addProjectMember(project, userB, jwtUserA, 200);
    addProjectMember(project, userB, jwtUserA, 400);

    final var updatedProject = getProjectByName(projectName);
    assertEquals(1, updatedProject.members.size());
    assertTrue(updatedProject.members.contains(userB));
  }

  @Test
  public void addMember_ownerIsAdded_400() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    addProjectMember(project, userA, jwtUserA, 400);

    assertEquals(0,  getProjectByName(projectName).members.size());
  }

  @Test
  public void removeMember_userIsMember_userIsRemoved() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    addProjectMember(project, userB, jwtUserA, 200);
    removeProjectMember(project, userB, jwtUserA, 200);

    assertEquals(0,  getProjectByName(projectName).members.size());
  }

  @Test
  public void removeMember_userIsUnknown_404() {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    final var unknownUser = new UserDB();
    unknownUser.id = -1L;

    removeProjectMember(project, unknownUser, jwtUserA, 404);

    assertEquals(0,  getProjectByName(projectName).members.size());
  }

  @Test
  public void removeMember_userIsNotAMember_400() {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    removeProjectMember(project, userB, jwtUserA, 400);

    assertEquals(0,  getProjectByName(projectName).members.size());
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

  private void removeProjectMember(ProjectDB project, UserDB user, String jwt, int expectedStatus) {
    given()
        .when()
        .body("")
        .headers(getAuthHeaders(jwt))
        .delete("/api/project/" + project.id + "/member/" + user.id + "/private")
        .then()
        .statusCode(expectedStatus);
  }

  private String createProjectJson(String name, String description) {
    return String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
  }

  private ProjectDB getProjectByName(String name) {
    return ProjectDB.find("name = ?1", name).firstResult();
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
}
