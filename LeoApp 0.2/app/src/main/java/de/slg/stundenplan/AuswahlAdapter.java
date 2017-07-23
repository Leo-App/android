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

import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

class AuswahlAdapter extends ArrayAdapter<Fach> {
    final Fach[] fachArray;
    private final View[] views;
    private final CheckBox[] cbs;
    private final StundenplanDB db;
    List<String> ausgewaehlteFaecher;
    boolean[][] ausgewaehlteStunden;

    AuswahlAdapter(Context context, Fach[] array) {
        super(context, R.layout.list_item_kurs, array);
        db = Utils.getStundDB();
        fachArray = array;
        views = new View[array.length];
        cbs = new CheckBox[array.length];
        ausgewaehlteFaecher = new List<>();
        ausgewaehlteStunden = new boolean[5][10];
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            double[] stunden = db.gibStunden(current.id);
            for (double d : stunden) {
                ausgewaehlteStunden[(int) (d) - 1][(int) (d * 10 % 10) - 1] = true;
            }
            tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        } else if (ausgewaehlteStunden[current.gibTag() - 1][current.gibStunde() - 1] || ausgewaehlteFaecher.contains(current.gibKurz().substring(0, 2))) {
            view.setEnabled(false);
            tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
            tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
            tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
        }
        cbs[position] = checkBox;
        views[position] = view;
        return view;
    }

    void refresh() {
        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                Fach current = fachArray[i];

                CheckBox c = cbs[i];
                TextView tvFach = (TextView) views[i].findViewById(R.id.fach_auswahl);
                TextView tvKuerzel = (TextView) views[i].findViewById(R.id.kürzel_auswahl);
                TextView tvLehrer = (TextView) views[i].findViewById(R.id.lehrer_auswahl);

                if (c.isChecked()) {
                    views[i].setEnabled(true);
                    tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                } else if (ausgewaehlteStunden[current.gibTag() - 1][current.gibStunde() - 1] || ausgewaehlteFaecher.contains(current.gibKurz().substring(0, 2))) {
                    views[i].setEnabled(false);
                    tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                    tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                    tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreyed));
                } else {
                    views[i].setEnabled(true);
                    tvFach.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                    tvKuerzel.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                    tvLehrer.setTextColor(ContextCompat.getColor(getContext(), R.color.colorText));
                }
            }
        }
    }

    int gibAnzahlAusgewaehlte() {
        int anzahl = 0;
        for (CheckBox c : cbs)
            if (c != null && c.isChecked())
                anzahl++;
        return anzahl;
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

    boolean toggleCheck(int position) {
        if (cbs[position] != null) {
            cbs[position].setChecked(!cbs[position].isChecked());
            return cbs[position].isChecked();
        }
        return false;
    }
}