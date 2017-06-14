package de.slg.essensqr;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

class MCrypt {

    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;

    MCrypt() {
        String iv = "15p60peADF4tT8u8";
        ivspec = new IvParameterSpec(iv.getBytes());

        String secretKey = "jHsj1C4XyXpEh7L9m0cVTLPgLU5QfXvh";
        keyspec = new SecretKeySpec(secretKey.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        byte[] encrypted;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

            encrypted = cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encrypted;
    }

    byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0)
            throw new Exception("Empty string");

        byte[] decrypted;

        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            decrypted = cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }


    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }

        String str = "";
        for (byte aData : data) {
            if ((aData & 0xFF) < 16)
                str = str + "0" + Integer.toHexString(aData & 0xFF);
            else
                str = str + Integer.toHexString(aData & 0xFF);
        }
        return str;
    }


    private static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }


    private static String padString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }
}