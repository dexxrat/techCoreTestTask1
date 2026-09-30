package test.api;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static test.api.AppConfig.*;

public class RefactoringApiTest extends BaseApiTest {


    @DisplayName("Успешный вход в систему")
    @Description("Проверка входа с валидными токеном, API-ключом и корректными данными")
    @Feature("Аутентификация")
    @Story("Вход в систему")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void loginTest() {

        String token = generateToken(VALID_TOKEN_SYMBOLS);
        performLogin(token);

    }

    @DisplayName("Успешное выполнение действия после входа")
    @Description("Проверка, что ранее залогиненный пользователь может выполнить действие ACTION")
    @Feature("Аутентификация")
    @Story("Выполнение действия")
    @Test
    public void actionTest() {

        String token = generateToken(VALID_TOKEN_SYMBOLS);
        performLogin(token);

        stubMockServer("/doAction", 200);

        baseRequest()
                .formParam("token", token)
                .formParam("action", "ACTION")
                .when()
                .post("")
                .then()
                .body("result", equalTo("OK"))
                .statusCode(200);
    }


    @DisplayName("Успешный выход из системы")
    @Description("Проверка, что ранее залогиненный пользователь может корректно завершить сессию (LOGOUT)")
    @Feature("Аутентификация")
    @Story("Выход из системы")
    @Test
    public void logOutTest() {

        String token = generateToken(VALID_TOKEN_SYMBOLS);
        performLogin(token);
        baseRequest()
                .formParam("token", token)
                .formParam("action", "LOGOUT")
                .when()
                .post("")
                .then()
                .body("result", equalTo("OK"))
                .statusCode(200);
    }

    @DisplayName("Отклонение запроса без API-ключа")
    @Description("Проверка, что запрос без заголовка X-Api-Key отклоняется с кодом 401")
    @Feature("Валидация входных данных")
    @Story("Проверка API-ключа")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void negativeLoginWithoutApiKeyTest() {

        String token = generateToken(VALID_TOKEN_SYMBOLS);
        given()
                .contentType(CONTENT_TYPE)
                .baseUri(BASE_URI)
                .basePath(BASE_PATH)
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .when()
                .post("")
                .then()
                .body("message", equalTo("Missing or invalid API Key"))
                .statusCode(401);
    }

    @DisplayName("Отклонение запроса с невалидным токеном")
    @Description("Проверка, что токен, не соответствующий формату 32 символов A-F0-9, отклоняется с кодом 400")
    @Feature("Валидация входных данных")
    @Story("Проверка формата токена")
    @Test
    public void negativeLoginNotValidToken() {

        String token = generateToken(NOT_VALID_TOKEN_SYMBOLS);
        baseRequest()
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .when()
                .post("")
                .then()
                .body("message", equalTo("token: must match \"^[0-9A-F]{32}$\""))
                .statusCode(400);
    }


    @DisplayName("Отклонение запроса с недопустимым действием")
    @Description("Проверка, что значение action, не входящее в список LOGIN/LOGOUT/ACTION, отклоняется с кодом 400")
    @Feature("Валидация входных данных")
    @Story("Проверка допустимых действий")
    @Test
    public void negativeLoginNotValidAction() {

        String token = generateToken(VALID_TOKEN_SYMBOLS);
        baseRequest()
                .formParam("token", token)
                .formParam("action", "LOGIN1+1")
                .when()
                .post("")
                .then()
                .body("message", equalTo("action: invalid action 'LOGIN1+1'. Allowed: LOGIN, LOGOUT, ACTION"))
                .statusCode(400);
    }

    @DisplayName("Отклонение ACTION без предварительного входа")
    @Description("Проверка, что действие ACTION с токеном, не прошедшим LOGIN, отклоняется с кодом 403")
    @Feature("Валидация входных данных")
    @Story("Проверка последовательности действий")
    @Test
    public void negativeActionWithoutLogin() {
        String token = generateToken(VALID_TOKEN_SYMBOLS);

        baseRequest()
                .formParam("token", token)
                .formParam("action", "ACTION")
                .when()
                .post("")
                .then()
                .body("message", equalTo("Token '" + token + "' not found"))
                .statusCode(403);
    }

    @DisplayName("Обработка сбоя сервиса аутентификации")
    @Description("Проверка поведения приложения, когда внешний сервис /auth возвращает 500 при попытке LOGIN")
    @Feature("Обработка сбоев внешнего сервиса")
    @Story("Сбой сервиса аутентификации")
    @Test
    public void loginWhenAuthServiceReturns500Test() {

        stubMockServer("/auth", 500);

        String token = generateToken(VALID_TOKEN_SYMBOLS);

        baseRequest()
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .when()
                .post("")
                .then()
                .body("message", equalTo("Internal Server Error"))
                .statusCode(500);
    }

    @Step("Подтверждаем логин ")
    private void performLogin(String token) {

        stubMockServer("/auth", 200);

        baseRequest()
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .when()
                .post("")
                .then()
                .statusCode(200);
    }


    @Step("Генерируем токен  ")
    private String generateToken(String text) {
        int arrayLength = 32;
        Random random = new Random();

        char[] chars = text.toCharArray();
        char[] tokenChars = new char[arrayLength];
        for (int i = 0; i < arrayLength; i++) {
            tokenChars[i] = chars[random.nextInt(chars.length)];
        }
        String token = new String(tokenChars);
        System.out.println(token);
        return token;
    }

}




