package de.slgdev.schwarzes_brett.utility;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;

public class Entry {

    public final String title;
    public final String content;
    private final Date createdate;
    private final Date expirydate;
    public final String to;
    public final String file;
    public int views;

    public Entry(String to, String title, String content, int views, Date createdate, Date expirydate, String file) {
        this.to = to;
        this.title = title;
        this.content = content;
        this.createdate = createdate;
        this.expirydate = expirydate;
        this.file = file.equals("null") ? null : file;
        this.views = views;
    }

    public String getFormattedDates() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        return simpleDateFormat.format(createdate) + " - " + simpleDateFormat.format(expirydate);
    }

}