package de.slgdev.messenger.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

/**
 * Message.
 * <p>
 * Verwaltungsklasse für Messenger-Nachrichten.
 *
 * @author Moritz
 * @version 2017.1811
 * @since 0.5.0
 */
public class Message {
    public final  int     mid;
    public final  String  mtext;
    public final  int     cid;
    public final  String  cname;
    public final  String  uname;
    public final  Date    mdate;
    public final  int     uid;
    public final  boolean mread;
    private final boolean sending;

    /**
     * Konstruktor für Nachrichten in der Warteschlange. Setzt konstante Nachrichtendaten.
     *
     * @param mid   ID der Nachricht, relevant zum Löschen
     * @param mtext Text der Nachricht
     * @param cid   ChatID
     */
    public Message(int mid, String mtext, int cid) {
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
     * Konstruktor für neu gesendete Nachrichten. Setzt den Nachrichtentext, Datum und die UserID des Absenders.
     *
     * @param mtext Text der Nachricht
     */
    public Message(String mtext) {
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
     * Konstruktor: Standard-Nachricht.
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
     * Konstruktor: Nachricht mit dem Namen des Absenders
     *
     * @param mid   MessageID
     * @param mtext Text der Nachricht
     * @param mdate Absendedatum
     * @param cid   ChatID
     * @param uid   UserID des Absenders
     * @param mread Nachricht bereits vom Empfänger gesehen
     * @param uname Name des Absenders
     */
    public Message(int mid, String mtext, long mdate, int cid, int uid, boolean mread, String uname) {
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
     * Konstruktor: Für die Benachrichtigungen
     *
     * @param mtext Text
     * @param cname Chatname
     * @param uname Benutzername des Absenders
     */
    public Message(String mtext, int cid, String cname, String uname) {
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

    /**
     * Liefert einen String, der abhängig vom aktuellen Datum das Datum der Nachricht repräsentiert.
     *
     * @return Heute; Gestern, DD.MM, DD.MM.YY, Warteschlange
     */
    public String getDate() {
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

    /**
     * Liefert einen String, der die Uhrzeit der Nachricht repräsentiert
     *
     * @return HH:mm:ss
     */
    public String getTime() {
        if (mdate.getTime() == 0 || sending)
            return "";
        return new SimpleDateFormat("HH:mm:ss", Locale.GERMANY).format(mdate);
    }
}