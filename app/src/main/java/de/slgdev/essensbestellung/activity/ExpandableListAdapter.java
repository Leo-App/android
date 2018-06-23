package de.slgdev.essensbestellung.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import org.w3c.dom.Text;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;

/**
 * Created by Florian on 22.04.2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;

    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_expandable_child_bestellung, null);
        }

        SharedPreferences prefs = Utils.getController().getPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        //editor.
        final View view = convertView;

        final RadioGroup rdGroupListChild = (RadioGroup) convertView.findViewById(R.id.radioBestellung);
        //TextView tvSubmit = (TextView) convertView.findViewById(R.id.tvSubmitlListItem);

        //for(int i=0;i<=2;i++) {
            RadioButton radioBtn = new RadioButton(_context);
            //radioBtn.setText(_listDataChild.get(_listDataHeader.get(groupPosition)).get(i));

            rdGroupListChild.addView(radioBtn);

            /*radioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        prefs.edit().putInt(_listDataHeader.get(groupPosition),rdGroupListChild.getCheckedRadioButtonId()).apply();
                    }
                }
            });*/
        //}

        /*tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int radioButtonID = rdGroupListChild.getCheckedRadioButtonId();
                RadioButton radioMenue = (RadioButton) view.findViewById(radioButtonID);
                if(radioMenue!=null) {
                    String radioMsg = radioMenue.getText().toString();
                    Toast.makeText(_context, radioMsg, Toast.LENGTH_LONG).show();
                }
            }
        });*/

        /*TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);*/
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_expandable_bestellung, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /*public Integer[] onSubmitClicked() {
        Integer[] retList = new Integer[4];
        ArrayList<RadioGroup> radioGroups = new ArrayList<RadioGroup>();

        for (int i = 0; i <= 3; i++) {

            radioGroups.add(_listDataChild)



        }
        return retList;
    }*/
}