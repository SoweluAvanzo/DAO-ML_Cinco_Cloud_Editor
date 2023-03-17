package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectTransferToOrganizationInput;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectTransferToUserInput;
import info.scce.cincocloud.core.rest.inputs.UpdateProjectUsersInput;
import info.scce.cincocloud.core.rest.tos.OrganizationTO;
import info.scce.cincocloud.core.rest.tos.ProjectTO;
import info.scce.cincocloud.core.rest.tos.UserTO;
import info.scce.cincocloud.core.services.AuthService;
import info.scce.cincocloud.db.OrganizationDB;
import info.scce.cincocloud.db.ProjectDB;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestErrorResponse;
import info.scce.cincocloud.rest.ObjectCache;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class ProjectControllerTest extends AbstractCincoCloudTest {

  @Inject
  AuthService authService;

  @Inject
  ObjectMapper objectMapper;

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

    createAndActivateUser("userA", "userA", "userA@cincocloud", "123456");
    createAndActivateUser("userB", "userB", "userB@cincocloud", "123456");
    createAndActivateUser("userC", "userC", "userC@cincocloud", "123456");
    createAndActivateUser("userD", "userD", "userD@cincocloud", "123456");

    jwtUserA = authService.login("userA@cincocloud", "123456");
    jwtUserB = authService.login("userB@cincocloud", "123456");
    jwtUserC = authService.login("userC@cincocloud", "123456");
    jwtUserD = authService.login("userD@cincocloud", "123456");

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
        .post("/api/projects")
        .then()
        .statusCode(201);

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
    final var project = createProjectInOrganization(projectTO, jwtUserA);

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
    final var project = createProjectInOrganization(projectTO, jwtUserA);

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

    final var project = createProjectInOrganization(projectTO, jwtUserA);

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

    final var project = createProjectInOrganization(projectTO, jwtUserA);
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

    final var project = createProjectInOrganization(projectTO, jwtUserA);
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

    final var project = createProjectInOrganization(projectTO, jwtUserA);
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
    final var project = createProjectInOrganization(projectTO, jwtUserA);

    final var error = transferProjectToOrganization(project, orgB, jwtUserA, 403)
        .extract()
        .body()
        .as(RestErrorResponse.class);

    assertEquals(403, error.statusCode);
    assertFalse(error.message.isEmpty());
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

  private void removeProjectMember(ProjectDB project, UserDB user, String jwt, int expectedStatus) {
    given()
        .when()
        .body("")
        .headers(getAuthHeaders(jwt))
        .delete("/api/projects/" + project.id + "/members/" + user.id)
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
        .post("/api/projects")
        .then()
        .statusCode(201);

    return ProjectDB.find("name = ?1", name).firstResult();
  }

  private ProjectDB createProject(ProjectTO project, String jwt) throws Exception {
    given()
        .when()
        .body(objectMapper.writeValueAsString(project))
        .headers(getAuthHeaders(jwt))
        .post("/api/projects")
        .then()
        .statusCode(201);

    return ProjectDB.find("name = ?1", project.getname()).firstResult();
  }

  private ProjectDB createProjectInOrganization(ProjectTO project, String jwt) throws Exception {
    given()
        .when()
        .body(objectMapper.writeValueAsString(project))
        .headers(getAuthHeaders(jwt))
        .post("/api/organizations/" + project.getorganization().getId() + "/projects")
        .then()
        .statusCode(201);

    return ProjectDB.find("name = ?1", project.getname()).firstResult();
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

  private ValidatableResponse transferProjectToUser(ProjectDB project, UserDB targetUser, int status) throws Exception {
    final var input = new UpdateProjectTransferToUserInput();
    input.setUserId(targetUser.id);
    return given()
        .when()
        .body(objectMapper.writeValueAsString(input))
        .headers(getAuthHeaders(jwtUserA))
        .put("/api/projects/" + project.id + "/rpc/transfer-to-user")
        .then()
        .statusCode(status);
  }

  private ValidatableResponse transferProjectToOrganization(ProjectDB project, OrganizationDB targetOrganization, String jwt, int status) throws Exception {
    final var input = new UpdateProjectTransferToOrganizationInput();
    input.setOrgId(targetOrganization.id);
    return given()
        .when()
        .body(objectMapper.writeValueAsString(input))
        .headers(getAuthHeaders(jwt))
        .put("/api/projects/" + project.id + "/rpc/transfer-to-organization")
        .then()
        .statusCode(status);
  }
}
