package de.slg.stundenplan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

class AuswahlAdapter extends ArrayAdapter<Fach> {
    private final Context context;
    private final Fach[] fachArray;
    private final View[] views;
    private final Stundenplanverwalter sv;
    private final StundenplanDB db;
    List<String> ausgewaehlteFaecher;
    boolean[][] ausgewaehlteStunden;

    AuswahlAdapter(Context context, Fach[] pFacher, Stundenplanverwalter psv) {
        super(context, R.layout.list_item_kurs, pFacher);
        this.context = context;
        fachArray = pFacher;
        views = new View[fachArray.length];
        sv = psv;
        db = Utils.getStundDB();
        ausgewaehlteFaecher = new List<>();
        ausgewaehlteStunden = new boolean[5][10];
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.list_item_kurs, null);
        }
        Fach current = fachArray[position];
        view.setEnabled(true);
        TextView tvFach = (TextView) view.findViewById(R.id.fach_auswahl);
        TextView tvKuerzel = (TextView) view.findViewById(R.id.kürzel_auswahl);
        TextView tvLehrer = (TextView) view.findViewById(R.id.lehrer_auswahl);

        tvFach.setText(current.gibName());
        tvKuerzel.setText(current.gibKurz());
        tvLehrer.setText(current.gibLehrer());

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setChecked(db.istGewaehlt(current.id));

        if (checkBox.isChecked()) {
            ausgewaehlteFaecher.append(current.gibKurz().substring(0, 2));
            Fach[] stunden = db.gibStunden(current.id);
            for (Fach f : stunden) {
                ausgewaehlteStunden[f.gibTag() - 1][f.gibStunde() - 1] = true;
            }
            tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else if (ausgewaehlteStunden[current.gibTag() - 1][current.gibStunde() - 1] || ausgewaehlteFaecher.contains(current.gibKurz().substring(0, 2))) {
            view.setEnabled(false);
            tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
            tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
            tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
        }
        views[position] = view;
        return view;
    }

    void refresh() {
        ausgewaehlteFaecher = new List<>();
        int[] selected = getSelectedIndices();
        for (int i : selected) {
            ausgewaehlteFaecher.append(fachArray[i].gibKurz().substring(0, 2));
        }
        ausgewaehlteStunden = new boolean[5][10];
        int[] ausgewaehlteIds = gibMarkierteIds();
        for (int id : ausgewaehlteIds) {
            Fach[] stunden = db.gibStunden(id);
            for (Fach f : stunden) {
                ausgewaehlteStunden[f.gibTag() - 1][f.gibStunde() - 1] = true;
            }
        }
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                CheckBox c = (CheckBox) views[i].findViewById(R.id.checkBox);
                Fach current = fachArray[i];
                if (c.isChecked()) {
                    views[i].setEnabled(true);
                    TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                } else if (ausgewaehlteStunden[current.gibTag() - 1][current.gibStunde() - 1] || ausgewaehlteFaecher.contains(current.gibKurz().substring(0, 2))) {
                    views[i].setEnabled(false);
                    TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
                    tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
                    tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorTextGreyed));
                } else {
                    views[i].setEnabled(true);
                    TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                    TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                    TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);
                    tvFach.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                    tvKuerzel.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                    tvLehrer.setTextColor(ContextCompat.getColor(context, R.color.colorText));
                }
            }
        }
    }

    private int[] getSelectedIndices() {
        List<Integer> list = new List<>();
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                CheckBox c = (CheckBox) views[i].findViewById(R.id.checkBox);
                if (c.isChecked()) {
                    list.append(i);
                }
            }
        }
        int[] indices = new int[list.length()];
        list.toFirst();
        for (int i = 0; i < indices.length; i++, list.next()) {
            indices[i] = list.getContent();
        }
        return indices;
    }

    boolean isOneSelected() {
        for (View v : views) {
            if (v != null && ((CheckBox) v.findViewById(R.id.checkBox)).isChecked())
                return true;
        }
        return false;
    }

    Fach[] gibAlleMarkierten() {
        Fach[] mark = new Fach[gibAnzahlMarkierte()];
        int c = 0;
        for (int i = 0; i < fachArray.length; i++) {
            if (views[i] != null) {
                if (((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked()) {
                    ArrayList<Fach> f = sv.gibFaecherMitKuerzel(fachArray[i].gibKurz());
                    for (int x = 0; x < f.size(); x++) {
                        mark[c] = f.get(x);
                        c++;
                    }
                }
            }
        }
        return mark;
    }

    private int gibAnzahlMarkierte() {
        int markierte = 0;
        for (int i = 0; i < fachArray.length; i++) {
            if (views[i] != null) {
                if (((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked()) {
                    ArrayList<Fach> f = sv.gibFaecherMitKuerzel(fachArray[i].gibKurz());
                    markierte = markierte + f.size();
                }
            }
        }
        return markierte;
    }

    int[] gibMarkierteIds() {
        List<Integer> liste = new List<>();
        for (int i = 0; i < fachArray.length; i++)
            if (views[i] != null && ((CheckBox) views[i].findViewById(R.id.checkBox)).isChecked())
                liste.append(fachArray[i].id);
        int[] ids = new int[liste.length()];
        liste.toFirst();
        for (int i = 0; i < ids.length; i++, liste.next()) {
            ids[i] = liste.getContent();
        }
        return ids;
    }
}