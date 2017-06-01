package de.slg.schwarzes_brett;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import de.slg.leoapp.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> schwarzesBrett;
    private List<String> eintraege;

    public ExpandableListAdapter(Activity context, List<String> eintraege,
                                 Map<String, List<String>> schwarzesBrett) {
        this.context = context;
        this.schwarzesBrett = schwarzesBrett;
        this.eintraege = eintraege;
    }

    @Override
    public int getGroupCount() {
        return eintraege.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return schwarzesBrett.get(eintraege.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return eintraege.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return schwarzesBrett.get(eintraege.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_expandable_title,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.laptop);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String laptop = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_expandable_child, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.laptop);
        item.setText(laptop);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
