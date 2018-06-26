package de.slgdev.svBriefkasten.Adapter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorSv;
import de.slgdev.leoapp.task.general.TaskStatusListener;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.svBriefkasten.task.AddProposalTask;
import de.slgdev.svBriefkasten.task.SyncTopicTask;
import de.slgdev.svBriefkasten.task.UpdateLikes;

/**
 * Created by sili- on 14.04.2018.
 *
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter implements TaskStatusListener {


    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;
    private List<Boolean> geliked;
    private int tmpint;
    private String tmpst;
    private SharedPreferences sharedPref;

    private static SQLiteConnectorSv sqLiteConnector;
    private static SQLiteDatabase sqLiteDatabase;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, List<Boolean> checked) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        geliked = checked;
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

    private Object getChecked(int i) {return geliked.get(i);}

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

        sharedPref = context.getSharedPreferences("Briefkasten", Context.MODE_PRIVATE);

        TextView lbl = view.findViewById(R.id.lblListHeader);
        lbl.setTypeface(null, Typeface.BOLD);
        lbl.setText(headerTitle);

        CheckBox ch = view.findViewById(R.id.check);
        ch.setChecked((Boolean) getChecked(i));
        ch.setTag(headerTitle);

        TaskStatusListener tmp = this;

        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorSv(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();

            Button vorschlag = view.findViewById(R.id.proposal);                                                       //Wenn der Button vorschlag gedrückt wird, soll ein Dialog angezeigt werden,
            vorschlag.setTag(headerTitle);                                                                              //In dem der Benutzer einen neuen Vorschlag erstellen kann
            vorschlag.setOnClickListener(view1 -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                final EditText et = new EditText(context);

                alertDialogBuilder.setView(et);                                                                         //Dem Dialog wird eine TextView hinzugefügt

                alertDialogBuilder.setCancelable(false).setPositiveButton(R.string.send, (dialog, id) -> {              //SendenButton
                    tmpint=i;
                    tmpst=et.getText().toString();
                    new SyncTopicTask().addListener(tmp).execute();
                });

                alertDialogBuilder.setCancelable(false).setNegativeButton(R.string.cancel, (dialogInterface, i1) -> {   //Abbrechen-Button
                });

                AlertDialog alertDialog = alertDialogBuilder.create();                                                  //Dialog wird erstellt und gezeigt
                alertDialog.show();
            });

        ch.setOnCheckedChangeListener((compoundButton, b1) -> {                                                 //Wenn die Checkbox gewechelt wird und eine Internetverbindung besteht soll die Änderung in der
            if (Utils.isNetworkAvailable()) {                                                                   //Datenbank gespeichert werden

                String topic = ch.getTag().toString();

                Cursor cursor;

                int likes;
                ContentValues values;
                cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES}, SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + topic + "'", null, null, null, null);
                cursor.moveToFirst();                                                                           //Zur Sicherheit: Nur wenn es ein passendes Thema dazu gibt
                if (cursor.getCount() >= 1) {
                    String tmp1 = cursor.getString(5);
                    tmp1 = tmp1.replace(" ", "");
                    likes = Integer.parseInt(tmp1);
                    Utils.logDebug(likes);
                    if (ch.isChecked())
                        likes++;
                    else
                        likes--;
                    values = new ContentValues();                                                               //Die Daten für die Datenbank werden gespeichert
                    values.put(SQLiteConnectorSv.LETTERBOX_TOPIC, cursor.getString(0));
                    values.put(SQLiteConnectorSv.LETTERBOX_PROPOSAL1, cursor.getString(1));
                    values.put(SQLiteConnectorSv.LETTERBOX_PROPOSAL2, cursor.getString(2));
                    values.put(SQLiteConnectorSv.LETTERBOX_DateOfCreation, cursor.getString(3));
                    values.put(SQLiteConnectorSv.LETTERBOX_CREATOR, cursor.getString(4));
                    values.put(SQLiteConnectorSv.LETTERBOX_LIKES, likes);
                    cursor.close();

                    sqLiteDatabase.update(SQLiteConnectorSv.TABLE_LETTERBOX, values, SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + topic + "'", null);


                    SharedPreferences.Editor ed = sharedPref.edit();                                            //Der Status der Checkobx wird gespeichert, um später darauf zuzugreifen
                    ed.putBoolean(topic, ch.isChecked());
                    ed.apply();
                    Utils.logDebug(String.valueOf(sharedPref.getBoolean(topic, true)) + ch.isChecked());

                    new UpdateLikes().execute(topic, likes);                                                     //Die Anzahl der Likes in der Datenbank werden erhöht
                }
            }
            else{
                Toast.makeText(context, R.string.connection, Toast.LENGTH_LONG).show();
                ch.setChecked(!ch.isChecked());
            }
        });
        if(Utils.isNetworkAvailable())
            new SyncTopicTask().addListener(this).execute();
        else
            Toast.makeText(context, R.string.connection, Toast.LENGTH_LONG).show();

        return view;

    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String) getChild(i,i1);
        if(view==null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view= inflater.inflate(R.layout.list_item_briefkasten, null);
        }

        TextView txtListChild = view.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void taskFinished(Object... params) {
        Cursor cursor = sqLiteDatabase.query(SQLiteConnectorSv.TABLE_LETTERBOX, new String[]{SQLiteConnectorSv.LETTERBOX_TOPIC, SQLiteConnectorSv.LETTERBOX_PROPOSAL1, SQLiteConnectorSv.LETTERBOX_PROPOSAL2, SQLiteConnectorSv.LETTERBOX_DateOfCreation, SQLiteConnectorSv.LETTERBOX_CREATOR, SQLiteConnectorSv.LETTERBOX_LIKES}, SQLiteConnectorSv.LETTERBOX_TOPIC + "='" + getGroup(tmpint) + "'", null, null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount()>=1 && cursor.getString(2).equals(""))
            new AddProposalTask().execute(tmpst, getGroup(tmpint));
        cursor.close();
    }
}
