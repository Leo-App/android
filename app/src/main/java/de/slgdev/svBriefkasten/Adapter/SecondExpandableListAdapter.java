package de.slgdev.svBriefkasten.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;

/**
 * Created by sili- on 14.04.2018.
 */

public class SecondExpandableListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private List<String> listLikeHeader;

    public SecondExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, List<String> listLikeHeader) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.listLikeHeader = listLikeHeader;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listDataHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listDataHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String)getGroup(i);
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.list_group_briefkasten2, null);
        }



        TextView lbl = (TextView)view.findViewById(R.id.secondListItem);
        lbl.setTypeface(null, Typeface.BOLD);
        lbl.setText(headerTitle);

        TextView like = (TextView) view.findViewById(R.id.likeListItem);
        String likeTitle = listLikeHeader.get(i);
        like.setTypeface(null, Typeface.BOLD);
        like.setText("Likes: " + likeTitle);
        return view;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String) getChild(i,i1);
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.list_item_briefkasten, null);
        }

        TextView txtListChild = (TextView)view.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public String getTopicAtPosition(int position) {
        return listDataHeader.get(position);

    }
}
