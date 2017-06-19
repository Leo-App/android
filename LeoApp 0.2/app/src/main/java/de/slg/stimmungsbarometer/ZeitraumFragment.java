package de.slg.stimmungsbarometer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.List;

public class ZeitraumFragment extends Fragment {

    public int zeitraum;
    private Ergebnis[][] data;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        fillData();
        return new StatistikView(getContext(), data);
    }

    private void fillData() {
        Ergebnis[][] allData = StimmungsbarometerActivity.empfangeDaten();
        data = new Ergebnis[4][];
        for (int i = 0; i < allData.length; i++) {
            List<Ergebnis> list = new List<>();
            if (zeitraum == 3) {
                list.adapt(allData[i]);
            } else if (zeitraum == 2) {
                for (Ergebnis e : allData[i])
                    if (vorherigesJahr(e.date))
                        list.append(e);
            } else if (zeitraum == 1) {
                for (Ergebnis e : allData[i])
                    if (vorherigerMonat(e.date))
                        list.append(e);
            } else if (zeitraum == 0) {
                for (Ergebnis e : allData[i])
                    if (vorherigeWoche(e.date))
                        list.append(e);
            }
            for (list.toFirst(); list.hasAccess() && list.hasNext(); list.next()) {
                Ergebnis current = list.getContent(), next = list.getNext();
                if (!vorherigerTag(current.date, next.date)) {
                    Calendar c = new GregorianCalendar();
                    c.setTime(new Date(current.date.getTime()));
                    c.add(Calendar.DAY_OF_MONTH, -1);
                    list.insertBehind(new Ergebnis(c.getTime(), -1, current.ich, current.schueler, current.lehrer, current.alle));
                }
            }
            data[i] = list.fill(new Ergebnis[list.length()]);
        }
    }

    private boolean vorherigerTag(Date pDate1, Date pDate2) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(pDate1);
        c2.setTime(pDate2);
        c2.add(Calendar.DAY_OF_MONTH, 1);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean vorherigeWoche(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 7);
        return c2.get(Calendar.YEAR) > c1.get(Calendar.YEAR) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) > c1.get(Calendar.MONTH)) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) >= c1.get(Calendar.DAY_OF_MONTH));
    }

    private boolean vorherigerMonat(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 30);
        return c2.get(Calendar.YEAR) > c1.get(Calendar.YEAR) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) > c1.get(Calendar.MONTH)) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) >= c1.get(Calendar.DAY_OF_MONTH));
    }

    private boolean vorherigesJahr(Date pDate) {
        Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
        c1.setTime(new Date());
        c2.setTime(pDate);
        c2.add(Calendar.DAY_OF_MONTH, 365);
        return c2.get(Calendar.YEAR) > c1.get(Calendar.YEAR) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) > c1.get(Calendar.MONTH)) ||
                (c2.get(Calendar.YEAR) == c1.get(Calendar.YEAR) && c2.get(Calendar.MONTH) == c1.get(Calendar.MONTH) && c2.get(Calendar.DAY_OF_MONTH) >= c1.get(Calendar.DAY_OF_MONTH));
    }
}