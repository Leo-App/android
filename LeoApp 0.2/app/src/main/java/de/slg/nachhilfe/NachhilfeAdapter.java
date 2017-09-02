package de.slg.nachhilfe;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.slg.leoapp.R;

class NachhilfeAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private final String[]       data;
    private final int            resId;

    NachhilfeAdapter(Context context, String[] data) {
        super(context, R.layout.list_item_nachhilfe, data);
        this.data = data;
        this.resId = R.layout.list_item_nachhilfe;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(resId, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(data[position]);
        return view;
    }
}
