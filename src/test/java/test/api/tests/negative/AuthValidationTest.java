package test.api.tests.negative;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.api.service.AuthService;
import test.api.tests.BaseApiTest;

import static org.hamcrest.Matchers.equalTo;
import static test.api.config.AppConfig.INVALID_TOKEN_SYMBOLS;
import static test.api.config.AppConfig.VALID_TOKEN_SYMBOLS;
import static test.api.utils.TokenGenerator.generateFromAlphabet;

@Epic("API тесты")
@Feature("Валидация входных данных")
public class AuthValidationTest extends BaseApiTest {

    private final AuthService authService = new AuthService();

    @Tag("negative")
    @DisplayName("Отклонение запроса без API-ключа")
    @Description("Проверка, что запрос без заголовка X-Api-Key отклоняется с кодом 401")
    @Story("Проверка API-ключа")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void loginWithoutApiKeyReturns401Test() {
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);

        authService.sendAuthRequestWithoutApiKey(token, "LOGIN")
                .then()
                .body("message", equalTo("Missing or invalid API Key"))
                .statusCode(401);
    }

    @Tag("negative")
    @DisplayName("Отклонение запроса с невалидным токеном")
    @Description("Проверка, что токен, не соответствующий формату 32 символов A-F0-9, отклоняется с кодом 400")
    @Story("Проверка формата токена")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void loginWithInvalidTokenFormatReturns400Test() {
        String token = generateFromAlphabet(INVALID_TOKEN_SYMBOLS);

        authService.sendAuthRequest(token, "LOGIN")
                .then()
                .body("message", equalTo("token: must match \"^[0-9A-F]{32}$\""))
                .statusCode(400);
    }

    @Tag("negative")
    @DisplayName("Отклонение запроса с недопустимым действием")
    @Description("Проверка, что значение action, не входящее в список LOGIN/LOGOUT/ACTION, отклоняется с кодом 400")
    @Story("Проверка допустимых действий")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void loginWithInvalidActionReturns400Test() {
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);
        String invalidAction = "WRONG_ACTION";

        authService.sendAuthRequest(token, invalidAction)
                .then()
                .body("message", equalTo("action: invalid action '" + invalidAction
                        + "'. Allowed: LOGIN, LOGOUT, ACTION"))
                .statusCode(400);
    }
}