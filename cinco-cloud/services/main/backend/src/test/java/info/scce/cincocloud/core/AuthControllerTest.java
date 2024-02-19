package info.scce.cincocloud.core;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.scce.cincocloud.AbstractCincoCloudTest;
import info.scce.cincocloud.core.rest.inputs.UserLoginInput;
import info.scce.cincocloud.core.rest.tos.AuthResponseTO;
import info.scce.cincocloud.db.UserDB;
import info.scce.cincocloud.exeptions.RestErrorResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class AuthControllerTest extends AbstractCincoCloudTest {

    @Inject
    ObjectMapper objectMapper;

    UserDB user;

    @BeforeEach
    public void setup() {
        reset();

        user = createAndActivateUser("userA", "userA", "userA@cincocloud", "123456");
    }

    @Test
    public void login_userUsesEmail_userLoggedIn() throws Exception {
        final var credentials = new UserLoginInput(user.email, "123456");

        final var token =
                given()
                        .when()
                        .headers(defaultHeaders)
                        .body(objectMapper.writeValueAsString(credentials))
                        .post("/api/auth")
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
                        .post("/api/auth")
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
                        .post("/api/auth")
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
                        .post("/api/auth")
                        .then()
                        .statusCode(401)
                        .extract()
                        .body()
                        .as(RestErrorResponse.class);

        assertNotNull(res.message);
    }
}
