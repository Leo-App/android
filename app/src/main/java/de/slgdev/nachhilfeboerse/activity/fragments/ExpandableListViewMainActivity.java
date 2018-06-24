package de.slgdev.nachhilfeboerse.activity.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorNachhilfeboerse;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.nachhilfeboerse.activity.Adapter.ExpendableListViewAdapter;
import de.slgdev.nachhilfeboerse.activity.Nachhilfeboerse_nachhilfegebenActivity;
import de.slgdev.nachhilfeboerse.activity.Nachhilfeboerse_profil;

/**
 * Created by Benno on 08.04.2018.
 */

public class ExpandableListViewMainActivity extends Fragment implements TaskStatusListener {

    private ExpendableListViewAdapter listAdapter;
    private ExpandableListView listView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;

    public ExpandableListViewMainActivity() {

    }

    SQLiteConnectorNachhilfeboerse sqLiteConnector;
    SQLiteDatabase sqLiteDatabase;

    protected void verbinden() {
        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorNachhilfeboerse(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();
    }

    public View onCreateView(LayoutInflater inflator, ViewGroup container, Bundle savedInstance) {


        View rootView = inflator.inflate(R.layout.activity_nachhilfeboerse_expendablelistview, container, false);
        verbinden();
        listView = rootView.findViewById(R.id.lvExp);

        return rootView;
    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();

        Cursor cursor1 = sqLiteDatabase.query(true,
                SQLiteConnectorNachhilfeboerse.TABLE_NACHHILFEBOERSE,
                new String[]{SQLiteConnectorNachhilfeboerse.NACHHILFE_FAECHER},
<<<<<<< HEAD
                null, null,
                SQLiteConnectorNachhilfeboerse.NACHHILFE_FAECHER, null, null, null);
        Utils.logDebug(cursor1.getCount());
        ArrayList faecher = new ArrayList();
        cursor1.moveToFirst();
        for(int f = 0 ; f < cursor1.getCount() ; f++) {
            String[] aufteilen = (cursor1.getString(0)).split(",");
            for (int a = 0; a < aufteilen.length; a++) {
                Boolean istinfaecherschon = false;
                for (int g = 0; g < faecher.size(); g++) {
                    if (faecher.get(g).equals(aufteilen[a])) {
                        istinfaecherschon = true;
                    }
                }
                if (!istinfaecherschon) {
                    faecher.add(aufteilen[a]);
=======
                null, null, null, null, null, null);

        Utils.logDebug("CURSOR SIZE: " + cursor1.getCount());

        ArrayList daten = new ArrayList();
        cursor1.moveToFirst();
        for(int a = 0 ; a < cursor1.getCount(); a++) {
            String[] fach = cursor1.getString(0).split(",");
            int l = 0;
            while (fach.length > l) {
                if(!daten.contains(fach[l])) {
                    daten.add(fach[l]);
                    listDataHeader.add(fach[l]);
                    Cursor cursor2 = sqLiteDatabase.query(false,
                            SQLiteConnectorNachhilfeboerse.TABLE_NACHHILFEBOERSE,
                            new String[]{SQLiteConnectorNachhilfeboerse.NACHHILFE_VORNAME},
                            SQLiteConnectorNachhilfeboerse.NACHHILFE_FAECHER + " like '%" + fach[l] + "%'",
                            null, null, null, null, null);
                    cursor2.moveToFirst();
                    Utils.logDebug(cursor2.getString(0));
                    List<String> name = new ArrayList<>();
                    for (int p = 0; p < cursor2.getCount(); p++) {
                        name.add(cursor2.getString(0));
                        cursor2.moveToNext();
                    }
                    listHash.put(listDataHeader.get(a), name);
                    cursor2.close();
                    cursor1.moveToNext();
>>>>>>> 0240f1c253650c68beb8bc708ba2a8fa7e44c2df
                }
                l++;
            }
            cursor1.moveToNext();
        }

            for(int c = 0 ; c < faecher.size() ; c++){
                listDataHeader.add(faecher.get(c).toString());
                List<String> fach = new ArrayList<>();
                Cursor cursor2 = sqLiteDatabase.query(SQLiteConnectorNachhilfeboerse.TABLE_NACHHILFEBOERSE,
                        new String[]{SQLiteConnectorNachhilfeboerse.NACHHILFE_VORNAME},
                        "Faecher" + " lIKE '%" + faecher.get(c).toString() + "%'",
                        null,null,null,null);
                cursor2.moveToFirst();
                for(int n = 0 ; n < cursor2.getCount() ; n++){
                    fach.add(cursor2.getString(0));
                    cursor2.moveToNext();
                }
                listHash.put(listDataHeader.get(c),fach);
                cursor2.close();
            }

        cursor1.close();

    }

    @Override
    public void taskFinished(Object... params) {
        initData();
        listAdapter = new ExpendableListViewAdapter(Utils.getContext(), listDataHeader, listHash);
        listView.setAdapter(listAdapter);
<<<<<<< HEAD
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                final String selected = (String) listAdapter.getChild(
                        groupPosition, childPosition);
                Intent intentProfil = new Intent(Utils.getContext(), Nachhilfeboerse_profil.class);
                intentProfil.putExtra("Name",selected);
                startActivity(intentProfil);
                return true;
            }
=======
        listView.setOnChildClickListener((expandableListView, view, i, i1, l) -> {
            Toast.makeText(
                    Utils.getContext(),
                    listDataHeader.get(i)
                            + " : "
                            + listHash.get(
                            listDataHeader.get(i)).get(
                            i1), Toast.LENGTH_SHORT)
                    .show();
            return false;
>>>>>>> 0240f1c253650c68beb8bc708ba2a8fa7e44c2df
        });
    }
}




