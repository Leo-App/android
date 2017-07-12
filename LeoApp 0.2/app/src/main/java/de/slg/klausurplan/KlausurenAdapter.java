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

class KlausurenAdapter extends ArrayAdapter<Klausur> {

    private final Context context;
    private final int resId;
    private final List<Klausur> klausuren;
    private final LayoutInflater layoutInflater;
    private final int markieren;

    KlausurenAdapter(Context context, List<Klausur> objects, int markieren) {
        super(context, R.layout.list_item_klausur, objects.fill(new Klausur[objects.length()]));
        this.context = context;
        resId = R.layout.list_item_klausur;
        this.markieren = markieren;
        klausuren = objects;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View v, @NonNull ViewGroup parent) {
        Klausur current = klausuren.getObjectAt(position);
        v = layoutInflater.inflate(resId, null);
        if (position == 0 || !current.istGleicheWoche(klausuren.getObjectAt(position - 1))) {
            TextView woche = (TextView) v.findViewById(R.id.textViewWoche);
            woche.setText(current.getWoche());
        } else {
            v.findViewById(R.id.textViewWoche).setVisibility(View.GONE);
        }
        TextView tv = (TextView) v.findViewById(R.id.textView);
        if (klausuren.getObjectAt(position) != null) {
            tv.setText(klausuren.getObjectAt(position).toString());
            if (position == markieren) {
                tv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            } else {
                tv.setTextColor(ContextCompat.getColor(context, android.R.color.black));
            }
        }
        return v;
    }
}