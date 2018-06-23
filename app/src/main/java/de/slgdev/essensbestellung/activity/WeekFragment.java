package de.slgdev.essensbestellung.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import java.util.List;

import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFragment extends Fragment {

    BestellAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<String> listDataHeader;
    HashMap<String, ArrayList<String>> listDataChild;
    private RadioGroup radioGroup;
    private Button btnDisplay;
    private RadioButton radioMenu;
    private View rootView;
    public Integer woche;
    private SharedPreferences prefs = Utils.getController().getPreferences();
    SharedPreferences.Editor editor;

    public WeekFragment() {
        // Required empty public constructor
    }

    //Konstantins Mensa :)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_subst_weekfragment, container, false);

        Bundle bundle = getArguments();
        HashMap<String, String> gerichteData = (HashMap<String, String>)bundle.getSerializable("Gerichte");




        //get the listview
        expListView = (ExpandableListView) rootView.findViewById(R.id.expandable_subst);

        TextView tvWoche = rootView.findViewById(R.id.woche);
        tvWoche.setText("Woche: " + String.valueOf(woche));

        // preparing list data
        prepareListData();

        listAdapter = new BestellAdapter(getContext(), listDataHeader, listDataChild, woche);

        //set adapter
        expListView.setAdapter(listAdapter);

        addListenerButton();

        return rootView;

    }

    public void addListenerButton() {
        Button btnSubmit = (Button) rootView.findViewById(R.id.button1);
        TextView tvMontag = (TextView) rootView.findViewById(R.id.montag);
        TextView tvDienstag = (TextView) rootView.findViewById(R.id.dienstag);
        TextView tvMittwoch = (TextView) rootView.findViewById(R.id.mittwoch);
        TextView tvDonnerstag = (TextView) rootView.findViewById(R.id.donnerstag);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String montag = "Montag: " + prefs.getString(String.valueOf(woche) + "Montag", "Fehler");
                String dienstag = "Dienstag: " + prefs.getString(String.valueOf(woche) + "Dienstag","Fehler");
                String mittwoch = "Mittwoch: " + prefs.getString(String.valueOf(woche) + "Mittwoch","Fehler");
                String donnerstag = "Donnerstag: " + prefs.getString(String.valueOf(woche) + "Donnerstag", "Fehler");
                tvMontag.setText(montag);
                tvDienstag.setText(dienstag);
                tvMittwoch.setText(mittwoch);
                tvDonnerstag.setText(donnerstag);
            }

        });
    }

    public void setWoche(int pWoche) {
        woche = pWoche;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<String>>();


        // Adding child data
        listDataHeader.add("Montag");
        listDataHeader.add("Dienstag");
        listDataHeader.add("Mittwoch");
        listDataHeader.add("Donnerstag");

        // Adding child data
        ArrayList<String> mon = new ArrayList<String>();
        mon.add("Montag1");
        mon.add("Montag2");

        ArrayList<String> die = new ArrayList<String>();
        die.add("Dienstag1");
        die.add("Dienstag2");

        ArrayList<String> mit = new ArrayList<String>();
        mit.add("Mittwoch1");
        mit.add("Mittwoch2");

        ArrayList<String> don = new ArrayList<String>();
        don.add("Donnerstag1");
        don.add("Donnerstag2");



        listDataChild.put(listDataHeader.get(0), mon);
        listDataChild.put(listDataHeader.get(1), die);
        listDataChild.put(listDataHeader.get(2), mit);
        listDataChild.put(listDataHeader.get(3), don);
    }

        // https://www.androidhive.info/2013/07/android-expandable-list-view-tutorial/


    /*Anpassung für mehrere Wochen:
        Ich brauche 3 SharedPreferences Sets à jeweils 4 Einträge um zu speichern, welche Menüs ausgewählt wurden.
        Die Namen der Einträge sollten ungefähr [Woche: 1/2/3][Tag: Montag/Dienstag/...][Menü: 1/2/0] sein.
    */

    public class BestellAdapter extends BaseExpandableListAdapter {

        private LayoutInflater mInflater;
        private Context _context;
        SharedPreferences prefs;
        SharedPreferences.Editor editor;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, ArrayList<String>> _listDataChild;
        private Integer woche;

        BestellAdapter(Context context, List<String> listHeaderData, HashMap<String, ArrayList<String>> listChildData, Integer pWoche) {
            this._context = context;
            this._listDataHeader = listHeaderData;
            this._listDataChild = listChildData;
            this.woche = pWoche;
            this.mInflater = LayoutInflater.from(context);
            prefs = Utils.getController().getPreferences();
            editor = prefs.edit();
        }

        //Anzahl der Gruppen (Sollte immer 4 sein, da 4 ListHeader
        @Override
        public int getGroupCount() {
            return _listDataHeader.size();
        }

        //Anzahl der Children in einer bestimmten Gruppe
        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return _listDataHeader.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String logAusgabe = _listDataChild.get(_listDataHeader.get(groupPosition)).get(0);
            logAusgabe = logAusgabe + _listDataChild.get(_listDataHeader.get(groupPosition)).get(1);
            Log.d("getChild", logAusgabe);
            return _listDataHeader.get(groupPosition);

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
            return true;
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
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
            Log.d("getChildView", "getChildView wurde aufgerufen mit groupPosition = " + getGroup(groupPosition));
            view = mInflater.inflate(R.layout.list_item_expandable_child_bestellung, viewGroup, false);
            RadioGroup rdGroup = view.findViewById(R.id.radioBestellung);


            //HashMap<String,ArrayList<String>> child = (HashMap<String,ArrayList<String>>) getChild(groupPosition, 1);
            ArrayList<String> childData = _listDataChild.get(getGroup(groupPosition));
            Log.d("getChildView", childData.get(0));
            Log.d("getChildView", childData.get(1));

            RadioButton rdButtonMenu1 = rdGroup.findViewById(R.id.radioButton1);
            rdButtonMenu1.setText(childData.get(0));

            RadioButton rdButtonMenu2 = rdGroup.findViewById(R.id.radioButton2);
            rdButtonMenu2.setText(childData.get(1));

            RadioButton rdButtonNichts = rdGroup.findViewById(R.id.radioButton0);

            String checkedButtonText = prefs.getString(String.valueOf(woche) + _listDataHeader.get(groupPosition), "Fehler");
            Log.d("getChildView", checkedButtonText);
            if (checkedButtonText.equals("Fehler")) {
                Log.e("getChildView", "Fehler in Shared Preferences");
            } else if (rdButtonMenu1.getText().equals(checkedButtonText)) {
                rdButtonMenu1.setChecked(true);
            } else if (rdButtonMenu2.getText().equals(checkedButtonText)) {
                rdButtonMenu2.setChecked(true);
            } else if (checkedButtonText.equals("Keine Bestellung")) {
                rdButtonNichts.setChecked(true);
            }

            rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    Log.d("onCheckedChanged", String.valueOf(i));
                    RadioButton selectedButton = (RadioButton) radioGroup.findViewById(i);
                    //String btnText = selectedButton.getText().toString();
                    Log.d("onCheckedChanged", "radioGroup changed");
                    Log.d("Shared Preferences Key", String.valueOf(woche) + _listDataHeader.get(groupPosition));
                    if (selectedButton.getText().equals("Keine Bestellung")) {
                        Log.d("onCheckedChanged", "Button 3");
                        editor.putString(String.valueOf(woche) + _listDataHeader.get(groupPosition), "Keine Bestellung");
                    } else {
                        //editor.putString(_listDataHeader.get(groupPosition),_listDataChild.get(_listDataHeader.get(groupPosition)).get(0));
                        editor.putString(String.valueOf(woche) + _listDataHeader.get(groupPosition), String.valueOf(selectedButton.getText()));
                        Log.d("onCheckedChanged", String.valueOf(selectedButton.getText()));
                    }

                    editor.apply();
                }
            });

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}