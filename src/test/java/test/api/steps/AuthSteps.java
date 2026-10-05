package test.api.steps;

import io.qameta.allure.Step;
import test.api.service.AuthService;

public class AuthSteps {

    private final AuthService authService = new AuthService();

    @Step("Логин с токеном {token}")
    public void login(String token) {
        authService.sendAuthRequest(token, "LOGIN")
                .then()
                .statusCode(200);
    }
}