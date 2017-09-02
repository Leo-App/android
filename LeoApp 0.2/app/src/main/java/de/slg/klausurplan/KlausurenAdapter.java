package de.slg.klausurplan;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        if (v == null)
            v = layoutInflater.inflate(resId, null);

        Klausur current = klausuren.getObjectAt(position);

        if (position == 0 || !current.istGleicheWoche(klausuren.getObjectAt(position - 1))) {
            TextView woche = (TextView) v.findViewById(R.id.textViewWoche);
            woche.setText(current.getWoche());
        } else {
            v.findViewById(R.id.textViewWoche).setVisibility(View.GONE);
        }

        String[]       parts = current.toString().replace(System.getProperty("line.separator"), " ").split(" ");
        final TextView tv    = (TextView) v.findViewById(R.id.textView);
        final TextView tv2   = (TextView) v.findViewById(R.id.textViewKursInfo);
        final TextView tv3   = (TextView) v.findViewById(R.id.textViewStufe);
        final TextView tv4   = (TextView) v.findViewById(R.id.textViewDate);

        if (!Utils.getUserStufe().equals(""))
            tv3.setVisibility(View.GONE);

        if (matchesStandardLayout(parts)) {
            tv.setText(parts[0]);
            tv2.setText(getFinalText(parts[1]) + " " + parts[2]);
            tv3.setText(parts[3]);
            tv4.setText(parts[4] + " " + parts[5]);
        } else {
            tv2.setVisibility(View.GONE);
            tv4.setText(parts[parts.length - 1]);
            tv3.setText("-");
            tv.setText(current.toString().substring(0, current.toString().length() - parts[parts.length - 1].length()));
        }

        if (current.datum.getTime() / 1000 == markieren) {
            tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }

        return v;
    }

    private String getFinalText(String s) {
        return s.matches("[G]K?") ? "GK" : s.matches("[L]K?") ? "LK" : s;
    }

    private boolean matchesStandardLayout(String[] parts) {
        return parts.length == 6 &&
                parts[4].matches("[a-zA-Z]{2},") &&
                parts[3].matches(".?[0-9F]") &&
                parts[2].matches("[A-Z]{3}") &&
                parts[1].length() <= 4 &&
                parts[0].matches("[A-Z]{1,3}") &&
                parts[5].matches("[0-9]{2}.[0-9]{2}.[0-9]{2}");
    }
}