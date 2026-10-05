package test.api.tests.negative;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.api.service.AuthService;
import test.api.tests.BaseApiTest;

import static org.hamcrest.Matchers.equalTo;
import static test.api.config.AppConfig.VALID_TOKEN_SYMBOLS;
import static test.api.utils.TokenGenerator.generateFromAlphabet;

@Epic("API тесты")
@Feature("Бизнес-логика")
public class AuthSequenceTest extends BaseApiTest {

    private final AuthService authService = new AuthService();

    @Tag("negative")
    @DisplayName("Отклонение ACTION без предварительного входа")
    @Description("Проверка, что действие ACTION с токеном, не прошедшим LOGIN, отклоняется с кодом 403")
    @Story("Проверка последовательности действий")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void actionWithoutLoginReturns403Test() {
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);

        authService.sendAuthRequest(token, "ACTION")
                .then()
                .body("message", equalTo("Token '" + token + "' not found"))
                .statusCode(403);
    }
}