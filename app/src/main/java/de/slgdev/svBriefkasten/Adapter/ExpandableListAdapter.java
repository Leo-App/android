package de.slgdev.svBriefkasten.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by sili- on 14.04.2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private List<Boolean> geliked;
    private List<Integer> id;

    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, List<Boolean> checked, List<Integer> id) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        geliked = checked;
        this.id=id;
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

    public Object getChecked(int i) {return geliked.get(i);}

    public int getText(int i){return id.get(i);}

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
            view= inflater.inflate(R.layout.list_group_briefkasten, null);
        }

        

        TextView lbl = (TextView)view.findViewById(R.id.lblListHeader);
        lbl.setTypeface(null, Typeface.BOLD);
        lbl.setText(headerTitle);

        CheckBox ch = view.findViewById(R.id.check);
        ch.setChecked((Boolean) getChecked(i));
        ch.setTag(headerTitle);

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorSv(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String topic = ch.getTag().toString();
                Utils.logDebug(topic);
                ContentValues values = new ContentValues();
                values.put(SQLiteConnectorSv.LIKED_TOPIC, topic);
                values.put(SQLiteConnectorSv.LIKED_CHECKED, ch.isChecked());

                Cursor cursor;

                int likes;

                cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES},SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + topic + "'", null, null, null, null);
                Utils.logDebug(cursor.getCount());
                cursor.moveToFirst();
                if(cursor.getCount()>=1) {
                    likes = Integer.parseInt(cursor.getString(6));
                    if (ch.isChecked())
                        likes++;
                    else
                        likes--;
                    values = new ContentValues();
                    values.put(SQLiteConnectorSv.LETTERBOX_TOPIC, cursor.getString(0));
                    values.put(SQLiteConnectorSv.LETTERBOX_PROPOSAL1, cursor.getString(1));
                    values.put(SQLiteConnectorSv.LETTERBOX_PROPOSAL2, cursor.getString(2));
                    values.put(SQLiteConnectorSv.LETTERBOX_DateOfCreation, cursor.getString(3));
                    values.put(SQLiteConnectorSv.LETTERBOX_CREATOR, cursor.getString(4));
                    values.put(SQLiteConnectorSv.LETTERBOX_LIKES, likes);


                    sqLiteDatabase.update(SQLiteConnectorSv.TABLE_LETTERBOX, values, SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + topic + "'", null);
                }
            }
        });


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
