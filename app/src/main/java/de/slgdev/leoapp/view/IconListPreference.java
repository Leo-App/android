package de.slgdev.leoapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

public class IconListPreference extends ListPreference {

    private class CustomListPreferenceAdapter extends ArrayAdapter<IconItem> {

        private Context context;
        private List<IconItem> icons;
        private int resource;

        CustomListPreferenceAdapter(Context context, int resource,
                                           List<IconItem> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.icons = objects;
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);

                holder = new ViewHolder();
                holder.iconName    = convertView.findViewById(R.id.iconName);
                holder.iconImage   = convertView.findViewById(R.id.iconImage);
                holder.radioButton = convertView.findViewById(R.id.iconRadio);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iconName.setText(icons.get(position).name);

            int identifier = context.getResources().getIdentifier(
                    icons.get(position).file, "drawable",
                    context.getPackageName());
            holder.iconImage.setImageResource(identifier);

            holder.radioButton.setChecked(icons.get(position).isChecked);

            convertView.setOnClickListener(v -> {
                for (int i = 0; i < icons.size(); i++) {
                    icons.get(i).isChecked = i == position;
                }
                getDialog().dismiss();
            });

            return convertView;
        }

    }

    private static class IconItem {

        private String file;
        private boolean isChecked;
        private String name;

        IconItem(CharSequence name, CharSequence file, boolean isChecked) {
            this(name.toString(), file.toString(), isChecked);
        }

        IconItem(String name, String file, boolean isChecked) {
            this.name = name;
            this.file = file;
            this.isChecked = isChecked;
        }

    }

    private static class ViewHolder {
        protected ImageView iconImage;
        protected TextView iconName;
        protected RadioButton radioButton;
    }

    private Context context;

    private CharSequence[] iconName;
    private List<IconItem> icons;
    private SharedPreferences preferences;
    private Resources resources;
    private String defaultIconFile;
    private TextView summary;

    public IconListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        resources = context.getResources();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.attrs_icon, 0, 0);

        try {
            defaultIconFile = a.getString(R.styleable.attrs_icon_iconFile);
        } finally {
            a.recycle();
        }
    }

    private String getEntry(String value) {
        String[] entries = resources.getStringArray(R.array.entryName);
        String[] values = resources.getStringArray(R.array.entryIcon);
        int index = Arrays.asList(values).indexOf(value);

        if (index == -1)
            return Utils.getString(R.string.summary_language);

        return entries[index];
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        String selectedIconFile = preferences.getString("pref_key_locale", defaultIconFile);

        summary = view.findViewById(android.R.id.summary);
        summary.setText(getEntry(selectedIconFile));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (icons != null) {
            for (int i = 0; i < iconName.length; i++) {
                IconItem item = icons.get(i);
                if (item.isChecked) {

                    preferences.edit().putString("pref_key_locale", item.file).apply();
                    summary.setText(item.name);

                    break;
                }
            }
        }

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton(null, null);

        iconName = getEntries();
        CharSequence[] iconFile = getEntryValues();

        if (iconName == null || iconFile == null || iconName.length != iconFile.length)
            return;


        String selectedIcon = preferences.getString("pref_key_locale", Utils.getString(R.string.icon_default));

        icons = new ArrayList<>();

        for (int i = 0; i < iconName.length; i++) {
            boolean isSelected = selectedIcon.contentEquals(iconFile[i]);
            IconItem item = new IconItem(iconName[i], iconFile[i], isSelected);
            icons.add(item);
        }

        CustomListPreferenceAdapter customListPreferenceAdapter = new CustomListPreferenceAdapter(context, R.layout.preference_icon_list, icons);
        builder.setAdapter(customListPreferenceAdapter, null);

    }

}