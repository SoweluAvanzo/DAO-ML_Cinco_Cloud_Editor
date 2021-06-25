package info.scce.pyro;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;




import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.json.JSONObject;

@QuarkusTest
public class RegistrationTest {

    @Test
    public void testDefaultStyle() {
        given()
                .when().get("/settings/public")
                .then()
                .statusCode(200);
    }

    @Test
    public void testRegistrationAndLogin() {
        given().auth()
        .basic("philip@me.com", "12345")
                .when().get("/user/current/private")
                .then()
                .statusCode(401);

        JSONObject parmas = new JSONObject();
        parmas.put("username", "Philip");
        parmas.put("name", "Philip Zweihoff");
        parmas.put("email", "philip@me.com");
        parmas.put("password", "12345");
        given().headers("Content-Type", ContentType.JSON)
                .body(parmas.toString())
                .when().post("/register/new/public")
                .then()
                .statusCode(200).body(is("Activation mail send"));

        given().auth()
        .basic("philip@me.com", "12345")
                .when().get("/user/current/private")
                .then()
                .statusCode(200);

        
    }
}