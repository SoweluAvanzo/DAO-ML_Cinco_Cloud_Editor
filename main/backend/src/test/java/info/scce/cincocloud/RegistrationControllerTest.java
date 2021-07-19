package info.scce.cincocloud;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@QuarkusTest
@QuarkusTestResource(PostgresResource.class)
@QuarkusTestResource(ArtemisResource.class)
public class RegistrationControllerTest {

    @Test
    public void register_userIsValid_200() {
        given()
                .when()
                .body(createUserJson("test", "test@test.de", "test", "123456"))
                .headers(Map.of("content-type", "application/json"))
                .post("/api/register/new/public")
                .then()
                .statusCode(200)
                .body(is("Activation mail send"));
    }

    @ParameterizedTest(name = "Password {0} is too short.")
    @ValueSource(strings = {"", "1234"})
    public void register_passwordTooShort_400(String password) {
        given()
                .when()
                .body(createUserJson("test", "test@test.de", "test", password))
                .headers(Map.of("content-type", "application/json"))
                .post("/api/register/new/public")
                .then()
                .statusCode(400);
    }

    @ParameterizedTest(name = "Email {0} is invalid.")
    @ValueSource(strings = {"", "test", "test@"})
    public void register_invalidEmail_400(String email) {
        given()
                .when()
                .body(createUserJson("test", email, "test", "123345"))
                .headers(Map.of("content-type", "application/json"))
                .post("/api/register/new/public")
                .then()
                .statusCode(400);
    }

    @Test
    public void register_nameIsEmpty_400() {
        given()
                .when()
                .body(createUserJson("test", "test@test.de", "", "123345"))
                .headers(Map.of("content-type", "application/json"))
                .post("/api/register/new/public")
                .then()
                .statusCode(400);
    }

    @Test
    public void register_usernameIsEmpty_400() {
        given()
                .when()
                .body(createUserJson("", "test@test.de", "test", "123345"))
                .headers(Map.of("content-type", "application/json"))
                .post("/api/register/new/public")
                .then()
                .statusCode(400);
    }

    private String createUserJson(String username, String email, String name, String password) {
        return String.format(
                "{\"username\":\"%s\", \"email\": \"%s\", \"name\": \"%s\", \"password\": \"%s\"}",
                username, email, name, password);
    }
}
