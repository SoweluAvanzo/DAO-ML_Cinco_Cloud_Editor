package info.scce.cincocloud.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.inputs.UserRegistrationInput;
import info.scce.cincocloud.core.rest.tos.AuthResponseTO;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestErrorResponse;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class CurrentUserControllerTest extends AbstractCincoCloudTest {

    @Inject
    ObjectMapper objectMapper;

    UserDB user;

    @BeforeEach
    public void setup() {
        reset();

        final var userA = new UserRegistrationInput();
        userA.setEmail("userA@cincocloud");
        userA.setName("userA");
        userA.setUsername("userA");
        userA.setPassword("123456");
        userA.setPasswordConfirm("123456");

        user = registrationService.registerUser(userA);
    }

    @Test
    public void login_userUsesEmail_userLoggedIn() throws Exception {
        final var credentials = new UserLoginInput(user.email, "123456");

        final var token =
                given()
                        .when()
                        .headers(defaultHeaders)
                        .body(objectMapper.writeValueAsString(credentials))
                        .post("/api/user/current/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(AuthResponseTO.class);

        assertNotNull(token.token);
    }

    @Test
    public void login_userUsesUsername_userLoggedIn() throws Exception {
        final var credentials = new UserLoginInput(user.username, "123456");

        final var token =
                given()
                        .when()
                        .headers(defaultHeaders)
                        .body(objectMapper.writeValueAsString(credentials))
                        .post("/api/user/current/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(AuthResponseTO.class);

        assertNotNull(token.token);
    }

    @Test
    public void login_passwordIsWrong_userCannotLogin() throws Exception {
        final var credentials = new UserLoginInput(user.username, "invalid");

        final var res =
                given()
                        .when()
                        .headers(defaultHeaders)
                        .body(objectMapper.writeValueAsString(credentials))
                        .post("/api/user/current/login")
                        .then()
                        .statusCode(401)
                        .extract()
                        .body()
                        .as(RestErrorResponse.class);

        assertNotNull(res.message);
    }

    @Test
    public void login_emailOrUsernameDoesNotExist_userCannotLogin() throws Exception {
        final var credentials = new UserLoginInput("unknown", "123456");

        final var res =
                given()
                        .when()
                        .headers(defaultHeaders)
                        .body(objectMapper.writeValueAsString(credentials))
                        .post("/api/user/current/login")
                        .then()
                        .statusCode(401)
                        .extract()
                        .body()
                        .as(RestErrorResponse.class);

        assertNotNull(res.message);
    }
}
