package de.slg.messenger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class Message {
    public final  int     mid;
    public final  String  mtext;
    public final  int     cid;
    public final  String  cname;
    public final  String  uname;
    final         Date    mdate;
    final         int     uid;
    final         boolean mread;
    private final boolean sending;

    /**
     * Für Nachrichten in der Warteschlange
     *
     * @param mid   relevant zum Löschen
     * @param mtext Text der Nachricht
     * @param cid   ChatID
     */
    Message(int mid, String mtext, int cid) {
        this.mid = mid;
        this.mtext = mtext;
        this.mdate = null;
        this.cid = cid;
        this.cname = null;
        this.uid = Utils.getUserID();
        this.uname = null;
        this.mread = false;
        this.sending = false;
    }

    /**
     * Für neu gesendete Nachrichten
     *
     * @param mtext Text der Nachricht
     */
    Message(String mtext) {
        this.mid = 0;
        this.mtext = mtext;
        this.mdate = new Date();
        this.cid = 0;
        this.cname = null;
        this.uid = Utils.getUserID();
        this.uname = null;
        this.mread = false;
        this.sending = true;
    }

    /**
     * Standard Nachricht
     *
     * @param mid   MessageID
     * @param mtext Text der Nachricht
     * @param mdate Absendedatum
     * @param cid   ChatID
     * @param uid   UserID des Absenders
     */
    public Message(int mid, String mtext, long mdate, int cid, int uid) {
        this.mid = mid;
        this.mtext = mtext;
        this.mdate = new Date(mdate);
        this.cid = cid;
        this.cname = null;
        this.uid = uid;
        this.uname = null;
        this.mread = false;
        this.sending = false;
    }

    /**
     * Nachricht mit dem Namen des Absenders
     *
     * @param mid   MessageID
     * @param mtext Text der Nachricht
     * @param mdate Absendedatum
     * @param cid   ChatID
     * @param uid   UserID des Absenders
     * @param mread Nachricht bereits vom Empfänger gesehen
     * @param uname Name des Absenders
     */
    Message(int mid, String mtext, long mdate, int cid, int uid, boolean mread, String uname) {
        this.mid = mid;
        this.mtext = mtext;
        this.mdate = new Date(mdate);
        this.uid = uid;
        this.uname = uname;
        this.cid = cid;
        this.cname = null;
        this.mread = mread;
        this.sending = false;
    }

    /**
     * Für die Benachrichtigungen
     *
     * @param mtext Wird auf 30 Zeichen + '...' gekürzt, falls notwendig
     *              Absätze werden entfernt
     * @param cname Wird auf 15 Zeichen + '...' gekürzt, falls notwendig
     * @param uname Bleibt unverändert
     */
    Message(String mtext, int cid, String cname, String uname) {
        mtext = mtext.replace(System.getProperty("line.separator"), "");
        if (mtext.length() > 33)
            mtext = mtext.substring(0, 30) + "...";
        if (cname.length() > 18)
            cname = cname.substring(0, 15) + "...";
        this.mid = 0;
        this.mtext = mtext;
        this.mdate = null;
        this.cid = cid;
        this.cname = cname;
        this.uid = 0;
        this.uname = uname;
        this.mread = false;
        this.sending = false;
    }

    private static boolean gleichesJahr(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    private static boolean gleicherTag(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private static boolean vorherigerTag(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 1);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    String getDate() {
        if (mdate.getTime() == 0)
            return Utils.getString(R.string.queue);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        if (gleicherTag(mdate))
            return Utils.getString(R.string.today);
        else if (vorherigerTag(mdate))
            return Utils.getString(R.string.yesterday);
        else if (gleichesJahr(mdate))
            simpleDateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
        return simpleDateFormat.format(mdate);
    }

    String getTime() {
        if (mdate.getTime() == 0 || sending)
            return "";
        return new SimpleDateFormat("HH:mm:ss", Locale.GERMANY).format(mdate);
    }
}