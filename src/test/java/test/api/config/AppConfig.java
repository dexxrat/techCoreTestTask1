package test.api.config;

public class AppConfig {
    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String X_API_KEY = "qazWSXedc";
    public static final String BASE_URI = "http://localhost:8080";
    public static final String BASE_PATH = "/endpoint";
    public static final String VALID_TOKEN_SYMBOLS = "0123456789ABCDEF";
    public static final String INVALID_TOKEN_SYMBOLS =
            "GHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public static final int TOKEN_LENGTH = 32;


    private AppConfig() {
    }
}