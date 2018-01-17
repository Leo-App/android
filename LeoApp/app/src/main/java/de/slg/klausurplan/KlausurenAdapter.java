package de.slg.klausurplan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.slg.klausurplan.utility.Klausur;
import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

public class KlausurenAdapter extends ArrayAdapter<Klausur> {
    private final Context        context;
    private final int            resId;
    private final Klausur[]      klausuren;
    private final LayoutInflater layoutInflater;
    private final long           markieren;

    public KlausurenAdapter(Context context, Klausur[] objects, long markieren) {
        super(context, R.layout.list_item_klausur, objects);
        this.context = context;
        this.resId = R.layout.list_item_klausur;
        this.markieren = markieren / 1000;
        this.klausuren = objects;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        if (v == null) {
            v = layoutInflater.inflate(resId, null);
        }

        Klausur current = klausuren[position];

        if (position == 0 || !de.slg.klausurplan.utility.Utils.isSameWeek(klausuren[position - 1].getDatum(), current.getDatum())) {
            TextView woche = v.findViewById(R.id.textViewWoche);
            woche.setVisibility(View.VISIBLE);
            woche.setText(de.slg.klausurplan.utility.Utils.getWeek(current.getDatum()));
        } else {
            v.findViewById(R.id.textViewWoche).setVisibility(View.GONE);
        }

        String[]       parts    = current.getTitel().split(" ");
        final TextView fach     = v.findViewById(R.id.textView);
        final TextView kursinfo = v.findViewById(R.id.textViewKursInfo);
        final TextView stufe    = v.findViewById(R.id.textViewStufe);
        final TextView datum    = v.findViewById(R.id.textViewDate);

        if (!Utils.getUserStufe().equals("")) {
            stufe.setVisibility(View.GONE);
        }

        if (matchesStandardLayout(parts)) {
            fach.setText(parts[0]);
            kursinfo.setVisibility(View.VISIBLE);
            kursinfo.setText(getFinalText(parts[1]) + " " + parts[2]);
            stufe.setText(parts[3]);
            datum.setText(Klausur.dateFormat.format(current.getDatum()));
        } else {
            fach.setText(current.getTitel());
            kursinfo.setVisibility(View.GONE);
            datum.setText(Klausur.dateFormat.format(current.getDatum()));
            stufe.setText("-");
        }

        if (current.getDatum().getTime() / 1000 == markieren) {
            fach.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            fach.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        return v;
    }

    private String getFinalText(String s) {
        return s.matches("[G]K?") ? "GK" : s.matches("[L]K?") ? "LK" : s;
    }

    private boolean matchesStandardLayout(String[] parts) {
        return parts.length == 4 &&
                parts[0].matches("[A-Z]{1,3}") && // Fach-Abkürzung
                parts[1].matches("[LG][0-9]?") && // Kurs
                parts[2].matches("[A-ZÄÖÜ]{3}") && // Lehrer-Kürzel
                parts[3].matches(".?[0-9F]");
    }
}