package de.slgdev.stundenplan.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorStundenplan;
import de.slgdev.leoapp.utility.datastructure.List;
import de.slgdev.stundenplan.utility.Fach;

public class KursAdapter extends ArrayAdapter<Fach> {

    public final List<String> ausgewaehlteFaecher;
    public final boolean[][]  ausgewaehlteStunden;

    private final LayoutInflater             inflater;
    private final KursViewWrapper[]          views;
    private final SQLiteConnectorStundenplan database;

    public KursAdapter(SQLiteConnectorStundenplan database, Context context, Fach[] fachArray) {
        super(context, R.layout.list_item_kurs, fachArray);

        this.database = database;

        views = new KursViewWrapper[fachArray.length];

        ausgewaehlteFaecher = new List<>();
        ausgewaehlteStunden = new boolean[5][15];

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        Fach current = getItem(position);

        if (views[position] == null) {
            views[position] = new KursViewWrapper(
                    inflater.inflate(
                            R.layout.list_item_kurs,
                            parent,
                            false
                    ),
                    getItem(position),
                    database.gibStunden(current.id)
            );
            views[position].setGewaehlt(database.istGewaehlt(getItem(position).id));
        }

        views[position].setEnabled(ausgewaehlteFaecher, ausgewaehlteStunden);

        return views[position].v;
    }

    public void refresh() {
        for (KursViewWrapper view : views) {
            if (view != null) {
                view.setEnabled(ausgewaehlteFaecher, ausgewaehlteStunden);
            }
        }
    }

    public int gibAnzahlAusgewaehlte() {
        int anzahl = 0;
        for (KursViewWrapper v : views) {
            if (v != null && v.istGewaehlt()) {
                anzahl++;
            }
        }
        return anzahl;
    }

    public int[] gibMarkierteIds() {
        int[] ids = new int[gibAnzahlAusgewaehlte()];
        int   i   = 0;
        for (int j = 0; j < getCount(); j++) {
            if (views[j] != null && views[j].istGewaehlt()) {
                ids[i] = getItem(j).id;
                i++;
            }
        }

        return ids;
    }

    public void toggleGewaehlt(int position) {
        views[position].toggleGewaehlt();
        setGewaehlt(position, views[position].istGewaehlt());
    }

    private void setGewaehlt(int position, boolean b) {
        for (float f : views[position].stunden) {
            ausgewaehlteStunden[(int) (f - 1)][(int) (f * 100) % 100 - 1] = b;
        }
        if (b)
            ausgewaehlteFaecher.append(views[position].gibKuerzel());
        else {
            ausgewaehlteFaecher.contains(views[position].gibKuerzel());
            ausgewaehlteFaecher.remove();
        }
    }
}
