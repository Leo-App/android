package de.slgdev.nachhilfeboerse.activity.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;


/**
 * Created by Benno on 08.04.2018.
 */

public class ExpendableListViewAdapter extends BaseExpandableListAdapter {

    private Context context ;
    private List<String> listDataHeader ;
    private HashMap<String,List<String>> listHashMap;

    public ExpendableListViewAdapter(Context context , List<String> listDataHeader , HashMap<String,List<String>> listHashMap){
        this.context = context ;
        this.listDataHeader = listDataHeader ;
        this.listHashMap = listHashMap ;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listHashMap.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listHashMap.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listDataHeader.get(i)).size();
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
        String headertitle = (String)getGroup(i);
        if(view == null){
            LayoutInflater inflator = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.activity_nachhilfeboerse_list_group,null);
        }
        TextView lblListHeader = (TextView)view.findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headertitle);
        return view ;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String)getChild(i,i1);
        if(view == null){
            LayoutInflater inflator = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.activity_nachhilfeboerse_list_item,null);
        }

        TextView txtListChild = (TextView)view.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return view ;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
