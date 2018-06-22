package de.slgdev.vertretungsplan.utility;

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

public class VertretungListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;


    public VertretungListAdapter(Context pContext, List<String> pListDataHeader, HashMap<String, List<String>> pListHashMap)    {
        context = pContext;
        listDataHeader = pListDataHeader;
        listHashMap = pListHashMap;
    }

    public void update (List<String> pListDataHeader, HashMap<String, List<String>> pListHashMap)   {
        listDataHeader = pListDataHeader;
        listHashMap = pListHashMap;
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
        if (view==null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group_vertretung, null);
        }
        TextView klasse = (TextView)view.findViewById(R.id.lvHeaderKlasse);
        TextView stunde = (TextView)view.findViewById(R.id.lvHeaderStunde);
        TextView lehrer = (TextView)view.findViewById(R.id.lvHeaderLehrer);
        TextView vertretung = (TextView)view.findViewById(R.id.lvHeaderVertretung);
        klasse.setTypeface(null, Typeface.BOLD);
        stunde.setTypeface(null, Typeface.BOLD);
        klasse.setText(headerTitle.substring(0, headerTitle.indexOf("|")));
        stunde.setText(headerTitle.substring(headerTitle.indexOf("|")+1, headerTitle.indexOf("*")));
        lehrer.setText(headerTitle.substring(headerTitle.indexOf("*")+1, headerTitle.indexOf("+")));
        vertretung.setText(headerTitle.substring(headerTitle.indexOf("+")+1));
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String)getChild(i,i1);
        if (view==null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item_vertretung, null);
        }
        TextView lvChild = (TextView)view.findViewById(R.id.lvItem);
        lvChild.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
