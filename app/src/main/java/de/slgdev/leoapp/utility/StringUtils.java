package de.slgdev.leoapp.utility;

import android.support.annotation.NonNull;

public abstract class StringUtils {

    @NonNull
    public static <T> String join(CharSequence delimiter, Iterable<T> elements) {
        StringBuilder ret = new StringBuilder();
        for (T t : elements) {
            ret.append(t.toString()).append(delimiter);
        }

        return ret.length() > delimiter.length() ? ret.substring(0, ret.length() - delimiter.length()) : "";
    }

    @NonNull
    public static <T> String join(CharSequence delimiter, T[] elements) {
        StringBuilder ret = new StringBuilder();
        for (T t : elements) {
            ret.append(t.toString()).append(delimiter);
        }

        return ret.length() > delimiter.length() ? ret.substring(0, ret.length() - delimiter.length()) : "";
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
