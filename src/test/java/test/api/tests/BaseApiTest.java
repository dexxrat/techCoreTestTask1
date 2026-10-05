package test.api.tests;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


public class BaseApiTest {

    protected static final int WIREMOCK_PORT = 8888;
    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));
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

    protected static void stubMockServer(String urlPath, int status) {
        wireMockServer.stubFor(post(urlPathEqualTo(urlPath))
                .willReturn(aResponse().withStatus(status)));
    }
}