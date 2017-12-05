package de.slg.klausurplan;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.R;

/**
 * Utils.
 * <p>
 * Abstrakte Klasse mit allgemeinen Methoden für den Klausurplan. Erweitert {@link de.slg.leoapp.utility.Utils} lokal.
 *
 * @author Gianni
 * @version 2017.1711
 * @since 0.6.1
 */

public abstract class Utils {
    static long findeNächsteKlausur(Klausur[] klausuren) {
        Date heute = new Date();
        for (Klausur aKlausuren : klausuren) {
            if (!heute.after(aKlausuren.getDatum()))
                return aKlausuren.getDatum().getTime();
        }
        return -1;
    }

    static int findeNächsteWoche(Klausur[] klausuren) {
        Date heute = new Date();
        for (int i = 0; i < klausuren.length; i++) {
            if (isSameWeek(heute, klausuren[i].getDatum()))
                return i;
        }
        return 0;
    }

    static String getWeek(Date d) {
        Calendar c = new GregorianCalendar();
        c.setTime(d);
        return de.slg.leoapp.utility.Utils.getString(R.string.week) + ' ' + c.get(Calendar.WEEK_OF_YEAR);
    }

    static boolean isSameWeek(Date d1, Date d2) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.WEEK_OF_YEAR) == c2.get(Calendar.WEEK_OF_YEAR);
    }
}