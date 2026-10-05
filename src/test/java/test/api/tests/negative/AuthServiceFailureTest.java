package test.api.tests.negative;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.api.BaseApiTest;
import test.api.service.AuthService;

import static org.hamcrest.Matchers.equalTo;
import static test.api.AppConfig.VALID_TOKEN_SYMBOLS;
import static test.api.utils.TokenGenerator.generateFromAlphabet;

@Epic("API тесты")
@Feature("Обработка сбоев внешнего сервиса")
public class AuthServiceFailureTest extends BaseApiTest {

    private final AuthService authService = new AuthService();

    @Tag("negative")
    @DisplayName("Обработка сбоя сервиса аутентификации")
    @Description("Проверка поведения приложения, когда внешний сервис /auth возвращает 500 при попытке LOGIN")
    @Story("Сбой сервиса аутентификации")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void loginWhenAuthServiceReturns500Test() {
        stubMockServer("/auth", 500);
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);

        authService.sendAuthRequest(token, "LOGIN")
                .then()
                .body("message", equalTo("Internal Server Error"))
                .statusCode(500);
    }
}