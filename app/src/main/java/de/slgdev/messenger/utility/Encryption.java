package de.slgdev.messenger.utility;

public abstract class Encryption {
    private static final String key2 = "ABCD";

    /**
     * Verschlüsselt text mit key
     *
     * @param text Text, der verschlüsselt wird
     * @param key  Schlüssel zum Verschlüsseln
     * @return text mit key verschlüsselt
     */
    public static String encrypt(String text, String key) {
        assert key.matches("[A-Z]*");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            int textChar  = text.charAt(i);
            int keyChar   = key.charAt(i);
            int encrypted = textChar + (keyChar - 65);

            if (isCapitalLetter(textChar)) {

                if (!isCapitalLetter(encrypted)) {
                    encrypted -= 26;
                }
                builder.append((char) encrypted);

            } else if (isLowerCaseLetter(textChar)) {

                if (!isLowerCaseLetter(encrypted)) {
                    encrypted -= 26;
                }
                builder.append((char) encrypted);

            } else {

                builder.append((char) textChar);

            }
        }

        return builder.toString();
    }

    /**
     * Entschlüsselt text mit key
     *
     * @param text Text, der entschlüsselt wird
     * @param key  Schlüssel zum entschlüsseln
     * @return text mit key entschlüsselt
     */
    public static String decrypt(String text, String key) {
        assert key.matches("[A-Z]*");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            int textChar  = text.charAt(i);
            int keyChar   = key.charAt(i);
            int decrypted = textChar - (keyChar - 65);
            if (isCapitalLetter(textChar)) {

                if (!isCapitalLetter(decrypted)) {
                    decrypted += 26;
                }
                builder.append((char) decrypted);

            } else if (isLowerCaseLetter(textChar)) {

                if (!isLowerCaseLetter(decrypted)) {
                    decrypted += 26;
                }
                builder.append((char) decrypted);

            } else {

                builder.append((char) textChar);

            }
        }

        return builder.toString();
    }

    /**
     * Verschlüsselt key mit key2
     *
     * @param key Schlüssel, der verschlüsselt wird
     * @return key mit key2 verschlüsselt
     */
    public static String encryptKey(String key) {
        assert key2.matches("[A-Z]*");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < key.length(); i++) {
            int keyChar   = key.charAt(i);
            int key2Char  = key2.charAt(i % key2.length());
            int encrypted = keyChar + (key2Char - 65);

            if (!isCapitalLetter(encrypted)) {
                encrypted -= 26;
            }

            builder.append((char) encrypted);
        }

        return builder.toString();
    }

    /**
     * Entschlüsselt key mit key2
     *
     * @param key Schlüssel, der entschlüsselt wird
     * @return key mit key2 entschlüsselt
     */
    public static String decryptKey(String key) {
        assert key2.matches("[A-Z]*");

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < key.length(); i++) {
            int keyChar   = key.charAt(i);
            int key2Char  = key2.charAt(i % key2.length());
            int decrypted = keyChar - (key2Char - 65);

            if (!isCapitalLetter(decrypted)) {
                decrypted += 26;
            }

            builder.append((char) decrypted);
        }

        return builder.toString();
    }

    /**
     * Erzeugt einen zufälligen String, der der Länge des Textes entspricht
     *
     * @param text Nachricht
     * @return zufälliger Text aus Großbuchstaben
     */
    public static String createKey(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            builder.append((char) (65 + Math.random() * 26));
        }

        return builder.toString();
    }

    private static boolean isCapitalLetter(int c) {
        return c >= 65 && c <= 90;
    }

    private static boolean isLowerCaseLetter(int c) {
        return c >= 97 && c <= 122;
    }
}