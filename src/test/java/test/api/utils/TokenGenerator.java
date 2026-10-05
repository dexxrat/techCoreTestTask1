package test.api.utils;

import java.util.Random;

import static test.api.AppConfig.TOKEN_LENGTH;

public class TokenGenerator {

    private static final Random RANDOM = new Random();

    private TokenGenerator() {
    }

    public static String generateFromAlphabet(String alphabet) {
        char[] chars = alphabet.toCharArray();
        char[] tokenChars = new char[TOKEN_LENGTH];
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            tokenChars[i] = chars[RANDOM.nextInt(chars.length)];
        }
        return new String(tokenChars);
    }
}