package de.slg.messenger;

abstract class Verschluesseln {
    static String encrypt(String text, String key) {
        StringBuilder erg = new StringBuilder();
        assert key.matches("[A-Z]*");
        for (int iT = 0, iK = 0; iT < text.length(); iT++, iK++) {
            if (iK >= key.length())
                iK = 0;
            int cT = text.charAt(iT);
            int cK = key.charAt(iK);
            cK -= 65;
            cT += cK;
            if (cT > 127)
                cT -= 128;
            erg.append((char) cT);
        }
        return erg.toString();
    }

    static String encryptKey(String text) {
        String key = "ABCD";
        StringBuilder erg = new StringBuilder();
        assert key.matches("[A-Z]*");
        for (int iT = 0, iK = 0; iT < text.length(); iT++, iK++) {
            if (iK >= key.length())
                iK = 0;
            int cT = text.charAt(iT);
            int cK = key.charAt(iK);
            cK -= 65;
            cT += cK;
            if (cT > 90)
                cT -= 26;
            erg.append((char) cT);
        }
        return erg.toString();
    }

    static String decrypt(String text, String key) {
        StringBuilder erg = new StringBuilder();
        assert key.matches("[A-Z]*");
        for (int iT = 0, iK = 0; iT < text.length(); iT++, iK++) {
            if (iK >= key.length())
                iK = 0;
            int cT = text.charAt(iT);
            int cK = key.charAt(iK);
            cK -= 65;
            cT -= cK;
            if (cT < 0)
                cT += 128;
            erg.append((char) cT);
        }
        return erg.toString();
    }

    static String createKey(String text) {
        int length = text.length();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append((char) (65 + Math.random() * 26));
        }
        return s.toString();
    }
}