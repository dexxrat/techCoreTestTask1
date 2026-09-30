package test.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static test.api.AppConfig.*;

public class BaseApiTest {
    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWithWireMock() {
        wireMockServer = new WireMockServer(
                WireMockConfiguration.options().port(8888)
        );
        wireMockServer.start();
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void resetStubs() {
        wireMockServer.resetAll();
    }

    protected static RequestSpecification baseRequest() {
        return given()
                .contentType(CONTENT_TYPE)
                .header("X-Api-Key", X_API_KEY)
                .baseUri(BASE_URI)
                .basePath(BASE_PATH);
    }

    protected static void stubMockServer(String urlPath, int status) {
        wireMockServer
                .stubFor(post(urlPathEqualTo(urlPath))
                        .willReturn(aResponse().withStatus(status)));
    }
}
