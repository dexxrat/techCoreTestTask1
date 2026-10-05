# TechCoreTask1 — API автотесты

Автотесты для эндпоинта `/endpoint` тестируемого Spring Boot приложения.

## Стек
- Java 17, JUnit 5, RestAssured, WireMock, Allure, Maven

## Требования
- Java 17+
- Maven 3.9+
- `internal-0.0.1-SNAPSHOT.jar` — тестируемое приложение
- `chromedriver` не нужен

## Запуск

### 1. Поднять WireMock для внешнего сервиса
WireMock поднимается автоматически в `BaseApiTest` на порту `8888`.

### 2. Запустить тестируемое приложение
```bash
java -jar -Dsecret=qazWSXedc -Dmock=http://localhost:8888/ internal-0.0.1-SNAPSHOT.jar
