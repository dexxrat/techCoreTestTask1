package test.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.Matchers.equalTo;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;

public class ApiTest extends BaseApiTest {

    @DisplayName("Успешный вход в систему")
    @Description("Проверка входа с валидными токеном, API-ключом и корректными данными")
    @Feature("Аутентификация")
    @Story("Вход в систему")
    @Test
 public void loginTest(){


     String token = generateValidToken();

     wireMockServer
             .stubFor(post(urlPathEqualTo("/auth"))
                     .willReturn(aResponse().withStatus(200)));

     given()
             .contentType("application/x-www-form-urlencoded")
             .header("X-Api-Key","qazWSXedc")
             .formParam("token",token)
             .formParam("action" ,"LOGIN")
             .when()
             .post("http://localhost:8080/endpoint")
             .then()
             .body("result", equalTo("OK"))
             .statusCode(200);
 }

    @DisplayName("Успешное выполнение действия после входа")
    @Description("Проверка, что ранее залогиненный пользователь может выполнить действие ACTION")
    @Feature("Аутентификация")
    @Story("Выполнение действия")
    @Test
    public void actionTest(){

     String token = generateValidToken();

        performLogin(token);

        wireMockServer
                .stubFor(post(urlPathEqualTo("/doAction"))
                        .willReturn(aResponse().withStatus(200)));

        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key","qazWSXedc")
                .formParam("token",token)
                .formParam("action" ,"ACTION")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("result", equalTo("OK"))
                .statusCode(200);
    }


    @DisplayName("Успешный выход из системы")
    @Description("Проверка, что ранее залогиненный пользователь может корректно завершить сессию (LOGOUT)")
    @Feature("Аутентификация")
    @Story("Выход из системы")
    @Test
    public void logOutTest(){

     String token = generateValidToken();

        performLogin(token);

        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key","qazWSXedc")
                .formParam("token",token)
                .formParam("action" ,"LOGOUT")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("result", equalTo("OK"))
                .statusCode(200);
    }

    @DisplayName("Отклонение запроса без API-ключа")
    @Description("Проверка, что запрос без заголовка X-Api-Key отклоняется с кодом 401")
    @Feature("Валидация входных данных")
    @Story("Проверка API-ключа")
    @Test
    public void negativeloginWithoutApiKeyTest(){

        String token = generateValidToken();
        given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("token",token)
                .formParam("action", "LOGIN")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("message", equalTo("Missing or invalid API Key"))
                .statusCode(401);
    }

    @DisplayName("Отклонение запроса с невалидным токеном")
    @Description("Проверка, что токен, не соответствующий формату 32 символов A-F0-9, отклоняется с кодом 400")
    @Feature("Валидация входных данных")
    @Story("Проверка формата токена")
    @Test
    public void negativeloginNotValidToken(){

        String token = generateNotValidToken();
        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key","qazWSXedc")
                .formParam("token",token)
                .formParam("action", "LOGIN")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("message", equalTo("token: must match \"^[0-9A-F]{32}$\""))
                .statusCode(400);
    }


    @DisplayName("Отклонение запроса с недопустимым действием")
    @Description("Проверка, что значение action, не входящее в список LOGIN/LOGOUT/ACTION, отклоняется с кодом 400")
    @Feature("Валидация входных данных")
    @Story("Проверка допустимых действий")
    @Test
    public void negativeloginNotValidAction(){

        String token = generateValidToken();
        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key","qazWSXedc")
                .formParam("token",token)
                .formParam("action", "LOGIN1+1")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("message", equalTo("action: invalid action 'LOGIN1+1'. Allowed: LOGIN, LOGOUT, ACTION"))
                .statusCode(400);
    }

    @DisplayName("Отклонение ACTION без предварительного входа")
    @Description("Проверка, что действие ACTION с токеном, не прошедшим LOGIN, отклоняется с кодом 403")
    @Feature("Валидация входных данных")
    @Story("Проверка последовательности действий")
    @Test
    public void negatibveActionWithoutLogin(){
        String token = generateValidToken();


        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key","qazWSXedc")
                .formParam("token",token)
                .formParam("action" ,"ACTION")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("message", equalTo("Token '"+token+"' not found"))
                .statusCode(403);
    }



    @DisplayName("Обработка сбоя сервиса аутентификации")
    @Description("Проверка поведения приложения, когда внешний сервис /auth возвращает 500 при попытке LOGIN")
    @Feature("Обработка сбоев внешнего сервиса")
    @Story("Сбой сервиса аутентификации")
    @Test
    public void negativeloginTestWithoutMockService(){

        wireMockServer
                .stubFor(post(urlPathEqualTo("/auth"))
                        .willReturn(aResponse().withStatus(500)));

        String token = generateValidToken();

        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key","qazWSXedc")
                .formParam("token",token)
                .formParam("action" ,"LOGIN")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .body("message", equalTo("Internal Server Error"))
                .statusCode(500);
    }





    @Step("Подтвержаем логин ")
    private void performLogin(String token) {
        wireMockServer.stubFor(post(urlPathEqualTo("/auth"))
                .willReturn(aResponse().withStatus(200)));

        given()
                .contentType("application/x-www-form-urlencoded")
                .header("X-Api-Key", "qazWSXedc")
                .formParam("token", token)
                .formParam("action", "LOGIN")
                .when()
                .post("http://localhost:8080/endpoint")
                .then()
                .statusCode(200);
    }


    @Step("Генерируем валидный токен  ")
    private String generateValidToken(){
        Random random = new Random();
        String text = "0123456789ABCDEF";
        char[] chars = text.toCharArray();
        char[] tokenChars = new char[32];

        for (int i = 0;i<=31;i++){
            tokenChars[i] = chars[random.nextInt(chars.length)];
        }
        String token = new String(tokenChars);

        System.out.println(token);

        return token;
    }

    @Step("Генерируем не валидный токен  ")
    private String generateNotValidToken(){
        Random random = new Random();
        String text = "GHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя!@#$%^&*()_+-=[]{}|;:',.<>/?`~ ";
        char[] chars = text.toCharArray();
        char[] tokenChars = new char[32];

        for (int i = 0;i<=31;i++){
            tokenChars[i] = chars[random.nextInt(chars.length)];
        }
        String token = new String(tokenChars);
        System.out.println(token);

        return token;
    }

}



