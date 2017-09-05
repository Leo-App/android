package de.slg.schwarzes_brett;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.slg.leoapp.R;

class ExpandableListAdapter extends BaseExpandableListAdapter {

    private final Map<String, List<String>> eintraege;
    private final List<String>              titel;
    private final LayoutInflater            inflater;
    @Nullable
    private       ArrayList<Integer>        views;

    ExpandableListAdapter(LayoutInflater inflater, List<String> titel, Map<String, List<String>> eintraege) {
        this.inflater = inflater;
        this.eintraege = eintraege;
        this.titel = titel;
    }

    ExpandableListAdapter(LayoutInflater inflater, List<String> titel, Map<String, List<String>> eintraege, @Nullable ArrayList<Integer> views) {
        this.inflater = inflater;
        this.eintraege = eintraege;
        this.titel = titel;
        this.views = views;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_expandable_title, null);
        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText((String) getGroup(groupPosition));
        TextView textViewStufe = (TextView) convertView.findViewById(R.id.textViewStufe);
        textViewStufe.setText(eintraege.get(titel.get(groupPosition)).get(0));
        if (views != null) {
            TextView textViewViews = (TextView) convertView.findViewById(R.id.textViewViews);
            textViewViews.setVisibility(View.VISIBLE);
            String viewString = views.get(groupPosition) > 999 ? "999+" : String.valueOf(views.get(groupPosition));
            textViewViews.setText(viewString);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textViewStufe.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            textViewStufe.setLayoutParams(params);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (isLastChild) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_expandable_child_alt, null);
            }

            final TextView textViewDate = (TextView) convertView.findViewById(R.id.textView);
            textViewDate.setText(eintraege.get(titel.get(groupPosition)).get(2));

        } else if(childPosition == 0) {
            convertView = inflater.inflate(R.layout.list_item_expandable_child, null);

            final TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText(eintraege.get(titel.get(groupPosition)).get(1));

        } else {
            convertView = inflater.inflate(R.layout.list_item_expandable_child_alt, null);

            final String rawLocation = eintraege.get(titel.get(groupPosition)).get(3);

            final ImageView iv = (ImageView) convertView.findViewById(R.id.imageViewIcon);
            iv.setImageResource(R.drawable.ic_file_download_black_24dp);
            iv.setColorFilter(Color.rgb(0x00, 0x91, 0xea));
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FileDownloadTask().execute(rawLocation);
                }
            });

            final TextView textView = (TextView) convertView.findViewById(R.id.textView);
            textView.setText(rawLocation.substring(rawLocation.lastIndexOf('/')));
            textView.setPaintFlags(textView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new FileDownloadTask().execute(rawLocation);
                }
            });
        }
        return convertView;
    }

    @Override
    public int getGroupCount() {
        return titel.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return eintraege.get(titel.get(groupPosition)).size() - 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titel.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return eintraege.get(titel.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
