package test.api.service;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static test.api.AppConfig.*;

public class AuthService {

    public Response sendAuthRequest(String token, String action) {
        return given()
                .contentType(CONTENT_TYPE)
                .header("X-Api-Key", X_API_KEY)
                .baseUri(BASE_URI)
                .basePath(BASE_PATH)
                .formParam("token", token)
                .formParam("action", action)
                .when()
                .post("");
    }

    public Response sendAuthRequestWithoutApiKey(String token, String action) {
        return given()
                .contentType(CONTENT_TYPE)
                .baseUri(BASE_URI)
                .basePath(BASE_PATH)
                .formParam("token", token)
                .formParam("action", action)
                .when()
                .post("");
    }
}