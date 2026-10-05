package test.api.tests.positive;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import test.api.service.AuthService;
import test.api.tests.BaseApiTest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.equalTo;
import static test.api.config.AppConfig.VALID_TOKEN_SYMBOLS;
import static test.api.utils.TokenGenerator.generateFromAlphabet;

@Epic("API тесты")
@Feature("Аутентификация")
public class AuthPositiveTest extends BaseApiTest {

    private final AuthService authService = new AuthService();

    @Tag("positive")
    @DisplayName("Успешный вход в систему")
    @Description("Проверка входа с валидными токеном, API-ключом и корректными данными")
    @Story("Вход в систему")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void loginSuccessTest() {
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);
        performSuccessfulLogin(token);

        wireMockServer.verify(postRequestedFor(urlPathEqualTo("/auth"))
                .withRequestBody(containing("token=" + token)));
    }

    @Tag("positive")
    @DisplayName("Успешное выполнение действия после входа")
    @Description("Проверка, что ранее залогиненный пользователь может выполнить действие ACTION")
    @Story("Выполнение действия")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void actionSuccessTest() {
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);
        performSuccessfulLogin(token);
        stubMockServer("/doAction", 200);

        authService.sendAuthRequest(token, "ACTION")
                .then()
                .body("result", equalTo("OK"))
                .statusCode(200);

        wireMockServer.verify(postRequestedFor(urlPathEqualTo("/doAction"))
                .withRequestBody(containing("token=" + token)));
    }

    @Tag("positive")
    @DisplayName("Успешный выход из системы")
    @Description("Проверка, что ранее залогиненный пользователь может корректно завершить сессию (LOGOUT)")
    @Story("Выход из системы")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void logoutSuccessTest() {
        String token = generateFromAlphabet(VALID_TOKEN_SYMBOLS);
        performSuccessfulLogin(token);

        authService.sendAuthRequest(token, "LOGOUT")
                .then()
                .body("result", equalTo("OK"))
                .statusCode(200);
    }

    @Step("Выполняем успешный вход с токеном: {token}")
    public void performSuccessfulLogin(String token) {
        stubMockServer("/auth", 200);

        authService.sendAuthRequest(token, "LOGIN")
                .then()
                .statusCode(200);
    }
}