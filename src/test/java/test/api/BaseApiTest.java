package test.api;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class BaseApiTest {
    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void startWithWireMock(){
        wireMockServer = new WireMockServer(
                WireMockConfiguration.options().port(8888)
        );
        wireMockServer.start();

    }

    @AfterAll
    static void stopWireMock(){
        wireMockServer.stop();
    }

    @BeforeEach
    void resetStubs() {
        wireMockServer.resetAll();
    }



}
