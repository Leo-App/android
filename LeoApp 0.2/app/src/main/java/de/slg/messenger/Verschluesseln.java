package de.slg.messenger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public abstract class Verschluesseln {
    private static final String key2 = "ABCD";

    static String encrypt(String text, String key) {
        StringBuilder builder = new StringBuilder();
        assert key.matches("[A-Z]*");
        for (int i = 0, skipped = 0; i < text.length(); i++) {
            char textChar = text.charAt(i),
                    keyChar = key.charAt(i - skipped),
                    encrypted = (char) (textChar + (keyChar - 65));
            if (textChar == '%') {
                for (int j = 0; j <= 2; j++) {
                    builder.append(text.charAt(i + j));
                }
                i += 2;
                skipped += 2;
            } else {
                if (isCapitalLetter(textChar)) {
                    if (encrypted > 90)
                        encrypted -= 26;
                    builder.append(encrypted);
                } else if (isLowerCaseLetter(textChar)) {
                    if (encrypted > 122)
                        encrypted -= 26;
                    builder.append(encrypted);
                } else {
                    builder.append(textChar);
                }
            }
        }
        return builder.toString();
    }

    public static String decrypt(String text, String key) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        assert key.matches("[A-Z]*");
        text = URLEncoder.encode(text, "UTF-8");
        for (int i = 0, skipped = 0; i < text.length(); i++) {
            char textChar = text.charAt(i),
                    keyChar = key.charAt(i - skipped),
                    decrypted = (char) (textChar - (keyChar - 65));
            if (textChar == '%') {
                for (int j = 0; j <= 2; j++) {
                    builder.append(text.charAt(i + j));
                }
                i += 2;
                skipped += 2;
            } else {
                if (isCapitalLetter(textChar)) {
                    if (decrypted < 65)
                        decrypted += 26;
                    builder.append(decrypted);
                } else if (isLowerCaseLetter(textChar)) {
                    if (decrypted < 97)
                        decrypted += 26;
                    builder.append(decrypted);
                } else {
                    builder.append(textChar);
                }
            }
        }
        return URLDecoder.decode(builder.toString(), "UTF-8");
    }

    public static String decryptKey(String key) {
        StringBuilder builder = new StringBuilder();
        assert key2.matches("[A-Z]*");
        for (int i = 0; i < key.length(); i++) {
            char keyChar = key.charAt(i),
                    key2Char = key2.charAt(i % key2.length()),
                    decrypted = (char) (keyChar - (key2Char - 65));
            if (decrypted < 65)
                decrypted += 26;
            builder.append(decrypted);
        }
        return builder.toString();
    }

    static String encryptKey(String key) {
        StringBuilder builder = new StringBuilder();
        assert key2.matches("[A-Z]*");
        for (int i = 0; i < key.length(); i++) {
            char keyChar = key.charAt(i),
                    key2Char = key2.charAt(i % key2.length()),
                    encrypted = (char) (keyChar + (key2Char - 65));
            if (encrypted > 90)
                encrypted -= 26;
            builder.append(encrypted);
        }
        return builder.toString();
    }

    static String createKey(String text) {
        int length = text.length();
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) == '%')
                length -= 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append((char) (65 + Math.random() * 26));
        }
        return builder.toString();
    }

    private static boolean isCapitalLetter(char c) {
        return c >= 65 && c <= 90;
    }

    private static boolean isLowerCaseLetter(char c) {
        return c >= 97 && c <= 122;
    }
}