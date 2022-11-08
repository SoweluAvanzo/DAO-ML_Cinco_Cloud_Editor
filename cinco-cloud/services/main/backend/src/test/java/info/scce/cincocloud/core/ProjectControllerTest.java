package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.OrganizationTO;
import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestErrorResponse;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
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
  String jwtUserC;
  String jwtUserD;

  UserDB userA;
  UserDB userB;
  UserDB userC;
  UserDB userD;

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

    final var userC = new UserRegistrationInput();
    userC.setEmail("userC@cincocloud");
    userC.setName("userC");
    userC.setUsername("userC");
    userC.setPassword("123456");
    userC.setPasswordConfirm("123456");
    registrationService.registerUser(userC);

    final var userD = new UserRegistrationInput();
    userD.setEmail("userD@cincocloud");
    userD.setName("userD");
    userD.setUsername("userD");
    userD.setPassword("123456");
    userD.setPasswordConfirm("123456");
    registrationService.registerUser(userD);

    final var userALogin = new UserLoginInput();
    userALogin.emailOrUsername = "userA@cincocloud";
    userALogin.password = "123456";

    final var userBLogin = new UserLoginInput();
    userBLogin.emailOrUsername = "userB@cincocloud";
    userBLogin.password = "123456";

    final var userCLogin = new UserLoginInput();
    userCLogin.emailOrUsername = "userC@cincocloud";
    userCLogin.password = "123456";

    final var userDLogin = new UserLoginInput();
    userDLogin.emailOrUsername = "userD@cincocloud";
    userDLogin.password = "123456";

    jwtUserA = authService.login(userALogin).token;
    jwtUserB = authService.login(userBLogin).token;
    jwtUserC = authService.login(userCLogin).token;
    jwtUserD = authService.login(userDLogin).token;

    this.userA = UserDB.find("username = 'userA'").firstResult();
    this.userB = UserDB.find("username = 'userB'").firstResult();
    this.userC = UserDB.find("username = 'userC'").firstResult();
    this.userD = UserDB.find("username = 'userD'").firstResult();
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

  @Test
  public void transferProjectFromUserToUser_projectIsTransferred_newUserIsOwner() throws Exception {
    final var projectName = "test";
    var project = createProject(projectName, jwtUserA);

    final var updatedProject = transferProjectToUser(project, userB, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    assertEquals(userB.id, updatedProject.getowner().getId());
    assertEquals(1, updatedProject.getmembers().size());
  }

  @Test
  public void transferProjectFromUserToUser_projectHasMembers_membersStayInProject() throws Exception {
    final var projectName = "test";
    var project = createProject(projectName, jwtUserA);

    addProjectMember(project, userB, jwtUserA, 200);
    addProjectMember(project, userC, jwtUserA, 200);

    final var updatedProject = transferProjectToUser(project, userB, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    // assert: new user is owner
    assertEquals(userB.id, updatedProject.getowner().getId());
    // assert: og owner is member
    assertTrue(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userA.id));
    // assert: project members stay the same
    assertTrue(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userC.id));
    assertEquals(2, updatedProject.getmembers().size());
  }

  @Test
  public void transferProjectFromUserToUser_projectDoesNotExist_404() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);
    project.id = -1L;

    final var error = transferProjectToUser(project, userB, 404)
        .extract()
        .body()
        .as(RestErrorResponse.class);

    assertEquals(404, error.statusCode);
    assertFalse(error.message.isEmpty());
  }

  @Test
  public void transferProjectFromUserToUser_userDoesNotExist_404() throws Exception {
    final var projectName = "test";
    final var project = createProject(projectName, jwtUserA);

    userB.id = -1L;
    final var error = transferProjectToUser(project, userB, 404)
        .extract()
        .body()
        .as(RestErrorResponse.class);

    assertEquals(404, error.statusCode);
    assertFalse(error.message.isEmpty());
  }

  @Test
  public void transferProjectFromUserToOrganization_projectIsTransferred_organizationIsOwner() throws Exception {
    var project = createProject("test_A", jwtUserA);
    var organization = createOrganization("test_a", jwtUserA);

    final var updatedProject = transferProjectToOrganization(project, organization, jwtUserA, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    assertNull(updatedProject.getowner());
    assertNotNull(updatedProject.getorganization());
    assertEquals(organization.id, updatedProject.getorganization().getId());
  }

  @Test
  public void transferProjectFromUserToOrganization_projectHasMembers_membersNotInOrganizationAreRemoved() throws Exception {
    var project = createProject("projectA", jwtUserA);
    addProjectMember(project, userC, jwtUserA, 200);

    var organization = createOrganization("orgA", jwtUserA);
    addOrganizationMember(organization, userB, jwtUserA, 200);

    final var updatedProject = transferProjectToOrganization(project, organization, jwtUserA, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    // assert: project has no owner
    assertNull(updatedProject.getowner());
    assertNotNull(updatedProject.getorganization());
    assertEquals(organization.id, updatedProject.getorganization().getId());
    // assert: member of org are transferred to member of project
    assertTrue(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userB.id));
    // assert: project members not in org are removed from project
    assertFalse(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userC.id));
  }

  @Test
  public void transferProjectFromOrganizationToOrganization_projectIsTransferred_newOrganizationIsOwner() throws Exception {
    var orgA = createOrganization("orgA", jwtUserA);
    var orgB = createOrganization("orgB", jwtUserA);

    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));
    final var project = createProject(projectTO, jwtUserA);

    final var updatedProject = transferProjectToOrganization(project, orgB, jwtUserA, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    assertNull(updatedProject.getowner());
    assertEquals(orgB.id, updatedProject.getorganization().getId());
  }

  @Test
  public void transferProjectFromOrganizationToOrganization_projectHasMembers_usersAreReplaced() throws Exception {
    var orgA = createOrganization("orgA", jwtUserA);
    addOrganizationMember(orgA, userC, jwtUserA, 200);
    var orgB = createOrganization("orgB", jwtUserB);
    addOrganizationMember(orgB, userA, jwtUserB, 200);
    addOrganizationMember(orgB, userD, jwtUserB, 200);

    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));
    final var project = createProject(projectTO, jwtUserA);

    final var updatedProject = transferProjectToOrganization(project, orgB, jwtUserA, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    // assert: members of old org are removed
    assertFalse(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userC.id));
    // assert: members of new org are added
    assertTrue(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userA.id));
    assertTrue(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userB.id));
    assertTrue(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userD.id));
  }

  @Test
  public void transferProjectFromOrganizationToOrganization_organizationDoesNotExist_404() throws Exception {
    final var orgA = createOrganization("orgA", jwtUserA);
    final var orgB = createOrganization("orgB", jwtUserA);

    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));

    final var project = createProject(projectTO, jwtUserA);

    orgB.id = -1L;
    final var error = transferProjectToOrganization(project, orgB, jwtUserA, 404)
        .extract()
        .body()
        .as(RestErrorResponse.class);

    assertEquals(404, error.statusCode);
    assertFalse(error.message.isEmpty());
  }

  @Test
  public void transferProjectFromOrganizationToOrganization_projectDoesNotExist_404() throws Exception {
    final var orgA = createOrganization("orgA", jwtUserA);
    final var orgB = createOrganization("orgB", jwtUserA);

    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));

    final var project = createProject(projectTO, jwtUserA);
    project.id = -1L;

    final var error = transferProjectToOrganization(project, orgB, jwtUserA, 404)
        .extract()
        .body()
        .as(RestErrorResponse.class);

    assertEquals(404, error.statusCode);
    assertFalse(error.message.isEmpty());
  }

  @Test
  public void transferProjectFromOrganizationToUser_projectIsTransferred_newUserIsOwner() throws Exception {
    final var orgA = createOrganization("orgA", jwtUserA);

    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));

    final var project = createProject(projectTO, jwtUserA);
    final var updatedProject = transferProjectToUser(project, userA, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    assertNull(updatedProject.getorganization());
    assertEquals(userA.id, updatedProject.getowner().getId());
  }

  @Test
  public void transferProjectFromOrganizationToUser_projectHasMembers_membersAreRemoved() throws Exception {
    final var orgA = createOrganization("orgA", jwtUserA);
    addOrganizationMember(orgA, userB, jwtUserA, 200);
    addOrganizationMember(orgA, userC, jwtUserA, 200);
    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));

    final var project = createProject(projectTO, jwtUserA);
    addProjectMember(project, userB, jwtUserA, 200);
    addProjectMember(project, userC, jwtUserA, 200);

    final var updatedProject = transferProjectToUser(project, userD, 200)
        .extract()
        .body()
        .as(ProjectTO.class);

    assertNull(updatedProject.getorganization());
    assertEquals(userD.id, updatedProject.getowner().getId());
    // assert: members of old org are removed
    assertFalse(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userA.id));
    assertFalse(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userB.id));
    assertFalse(updatedProject.getmembers().stream().anyMatch(u -> u.getId() == userC.id));
  }

  @Test
  public void transferProjectFromOrganizationToUser_userIsNotInOrganization_403() throws Exception {
    final var orgA = createOrganization("orgA", jwtUserA);
    final var orgB = createOrganization("orgB", jwtUserB);

    final var projectTO = new ProjectTO();
    projectTO.setname("test");
    projectTO.setorganization(OrganizationTO.fromEntity(orgA, new ObjectCache()));
    final var project = createProject(projectTO, jwtUserA);

    final var error = transferProjectToOrganization(project, orgB, jwtUserA, 403)
        .extract()
        .body()
        .as(RestErrorResponse.class);

    assertEquals(403, error.statusCode);
    assertFalse(error.message.isEmpty());
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

  private String createOrganizationJson(String name, String description) {
    return String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
  }

  private String createProjectJson(String name, String description) {
    return String.format("{\"name\":\"%s\", \"description\":\"%s\"}", name, description);
  }

  private ProjectDB getProjectByName(String name) {
    return ProjectDB.find("name = ?1", name).firstResult();
  }

  private OrganizationDB getOrganizationByName(String name) {
    return OrganizationDB.find("name = ?1", name).firstResult();
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

  private ProjectDB createProject(ProjectTO project, String jwt) throws Exception {
    given()
        .when()
        .body(objectMapper.writeValueAsString(project))
        .headers(getAuthHeaders(jwt))
        .post("/api/project/create/private")
        .then()
        .statusCode(200);

    return ProjectDB.find("name = ?1", project.getname()).firstResult();
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

  private ValidatableResponse transferProjectToUser(ProjectDB project, UserDB targetUser, int status) throws Exception {
    return given()
        .when()
        .body(objectMapper.writeValueAsString(UserTO.fromEntity(targetUser, objectCache)))
        .headers(getAuthHeaders(jwtUserA))
        .put("/api/project/" + project.id + "/owner/private")
        .then()
        .statusCode(status);
  }

  private ValidatableResponse transferProjectToOrganization(ProjectDB project, OrganizationDB targetOrganization, String jwt, int status) throws Exception {
    return given()
        .when()
        .body(objectMapper.writeValueAsString(OrganizationTO.fromEntity(targetOrganization, objectCache)))
        .headers(getAuthHeaders(jwt))
        .put("/api/project/" + project.id + "/organization/private")
        .then()
        .statusCode(status);
  }
}
