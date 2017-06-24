package de.slg.messenger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Message {
    final int mid;
    public final String mtext;
    final Date mdate;
    public final int cid;
    final int uid;
    boolean mread;
    String uname = null;

    public Message(int mid, String mtext, long mdate, int cid, int uid, boolean mread) {
        this.mid = mid;
        this.mtext = "" + mtext;
        this.uid = uid;
        this.mdate = new Date(mdate);
        this.cid = cid;
        this.mread = mread;
    }

    @Override
    public String toString() {
        if (uname != null)
            return uname + ": " + mtext;
        return mtext;
    }

    void setUname(String uname) {
        this.uname = uname;
    }

    public String getDate() {
        if (mdate.getTime() == 0)
            return "Senden...";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        if (gleicherTag(mdate))
            return "Heute";
        else if (vorherigerTag(mdate))
            return "Gestern";
        else if (gleichesJahr(mdate))
            simpleDateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
        return simpleDateFormat.format(mdate);
    }

    private boolean gleichesJahr(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    private boolean gleicherTag(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean vorherigerTag(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 1);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    String getTime() {
        if (mdate.getTime() == 0)
            return "";
        return new SimpleDateFormat("HH:mm:ss", Locale.GERMANY).format(mdate);
    }
}