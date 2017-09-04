package de.slg.klausurplan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

class KlausurenAdapter extends ArrayAdapter<Klausur> {

    private final Context        context;
    private final int            resId;
    private final List<Klausur>  klausuren;
    private final LayoutInflater layoutInflater;
    private final long           markieren;

    KlausurenAdapter(Context context, List<Klausur> objects, long markieren) {
        super(context, R.layout.list_item_klausur, objects.fill(new Klausur[objects.size()]));
        this.context = context;
        resId = R.layout.list_item_klausur;
        this.markieren = markieren / 1000;
        klausuren = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        if (v == null) {
            v = layoutInflater.inflate(resId, null);
        }

        Klausur current = klausuren.getObjectAt(position);

        if (position == 0 || !current.istGleicheWoche(klausuren.getObjectAt(position - 1))) {
            TextView woche = (TextView) v.findViewById(R.id.textViewWoche);
            woche.setVisibility(View.VISIBLE);
            woche.setText(current.getWoche());
        } else {
            v.findViewById(R.id.textViewWoche).setVisibility(View.GONE);
        }

        String[]       parts    = current.getFach().split(" ");
        final TextView fach     = (TextView) v.findViewById(R.id.textView);
        final TextView kursinfo = (TextView) v.findViewById(R.id.textViewKursInfo);
        final TextView stufe    = (TextView) v.findViewById(R.id.textViewStufe);
        final TextView datum    = (TextView) v.findViewById(R.id.textViewDate);

        if (!Utils.getUserStufe().equals("")) {
            stufe.setVisibility(View.GONE);
        }

        if (matchesStandardLayout(parts)) {
            fach.setText(parts[0]);
            kursinfo.setVisibility(View.VISIBLE);
            kursinfo.setText(getFinalText(parts[1]) + " " + parts[2]);
            stufe.setText(parts[3]);
            datum.setText(current.getDatum(true));
        } else {
            fach.setText(current.getFach());
            kursinfo.setVisibility(View.GONE);
            datum.setText(current.getDatum(true));
            stufe.setText("-");
        }

        if (current.datum.getTime() / 1000 == markieren) {
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