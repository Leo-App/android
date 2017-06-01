package de.slg.stimmungsbarometer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.slg.leoapp.R;

public class ListAdapterGrund extends ArrayAdapter <String> {

    private Context context;
    private String[] gruende;

    public ListAdapterGrund(Context context, String[] gruende) {
        super(context, R.layout.list_item_grund, gruende);
        this.gruende = gruende;
        this.context = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup group) {
        if (v == null)
            v = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.list_item_grund, null);
        TextView grund = (TextView) v.findViewById(R.id.textViewGrund);
        grund.setText(gruende[position]);
        return v;
    }
}