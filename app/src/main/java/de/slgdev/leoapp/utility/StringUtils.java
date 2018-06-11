package de.slgdev.leoapp.utility;

import android.support.annotation.NonNull;

public abstract class StringUtils {

    @NonNull
    public static <T> String join(CharSequence delimiter, Iterable<T> elements) {
        StringBuilder ret = new StringBuilder();
        for (T t : elements) {
            ret.append(t.toString()).append(delimiter);
        }

        return ret.length() > delimiter.length() ? ret.substring(0, ret.length()-delimiter.length()) : "";
    }

}
