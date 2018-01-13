package de.slg.messenger.utility;

public abstract class Utils {
    private static int currentlyDisplayedChatId = -1;

    public static int currentlyDisplayedChat() {
        return currentlyDisplayedChatId;
    }

    public static void setCurrentlyDisplayedChat(int cid) {
        currentlyDisplayedChatId = cid;
    }

    public abstract static class Encryption {
        private static final String key2 = "ABCD";

        /**
         * Verschlüsselt text mit key
         *
         * @param text Text, der verschlüsselt wird
         * @param key  Schlüssel zum Verschlüsseln
         * @return text mit key verschlüsselt
         */
        public static String encrypt(String text, String key) {
            StringBuilder builder = new StringBuilder();
            assert key.matches("[A-Z]*");
            for (int i = 0; i < text.length(); i++) {
                char textChar = text.charAt(i),
                        keyChar = key.charAt(i),
                        encrypted = (char) (textChar + (keyChar - 65));
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
            StringBuilder builder = new StringBuilder();
            assert key.matches("[A-Z]*");
            for (int i = 0; i < text.length() && i < key.length(); i++, i++) {
                char textChar = text.charAt(i),
                        keyChar = key.charAt(i),
                        decrypted = (char) (textChar - (keyChar - 65));
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
            return builder.toString();
        }

        /**
         * Verschlüsselt key mit key2
         *
         * @param key Schlüssel, der verschlüsselt wird
         * @return key mit key2 verschlüsselt
         */
        public static String encryptKey(String key) {
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

        /**
         * Entschlüsselt key mit key2
         *
         * @param key Schlüssel, der entschlüsselt wird
         * @return key mit key2 entschlüsselt
         */
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

        private static boolean isCapitalLetter(char c) {
            return c >= 65 && c <= 90;
        }

        private static boolean isLowerCaseLetter(char c) {
            return c >= 97 && c <= 122;
        }
    }
}
