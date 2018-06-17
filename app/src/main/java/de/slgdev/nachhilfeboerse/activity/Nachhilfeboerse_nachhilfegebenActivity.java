package de.slgdev.nachhilfeboerse.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorNachhilfeboerse;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
//import values.strings;
import de.slgdev.nachhilfeboerse.activity.Adapter.ExpendableListViewAdapter;

import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse;
import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse_nachhilfegeben;

/**
 * Created by Benno on 08.04.2018.
 */

public class Nachhilfeboerse_nachhilfegebenActivity extends LeoAppNavigationActivity {


    public static final  String TABLE_NACHHILFEBOERSE        = "NachhilfeBoerse";
    public static final  String NACHHILFE_VORNAME        = "vorname";
    public static final  String NACHHILFE_NACHNAME        = "nachname";
    public static final String NACHHILFE_STUFE           = "stufe";
    public static final  String NACHHILFE_FAECHER       = "faecher";


    @Override
    protected String getActivityTag() {
        return "NachhilfeboerseActivity";
    }

    @Override
    protected int getContentView() {
        return activity_nachhilfeboerse;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawerLayout;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_tutoring;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.nachhilfeboerse;
    }

    SQLiteConnectorNachhilfeboerse sqLiteConnector;
    SQLiteDatabase sqLiteDatabase;

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(activity_nachhilfeboerse_nachhilfegeben);

        verbinden();

        Intent weiterIntent = new Intent(Nachhilfeboerse_nachhilfegebenActivity.this, NachhilfeboerseActivity.class);
        CheckBox checkBoxMathe = (CheckBox) findViewById(R.id.mathe);
        CheckBox checkBoxDeutsch = (CheckBox) findViewById(R.id.deutsch);
        CheckBox checkBoxEnglisch = (CheckBox) findViewById(R.id.englisch);
        CheckBox checkBoxSpanisch = (CheckBox) findViewById(R.id.spanisch);
        CheckBox checkBoxfranzoesisch = (CheckBox) findViewById(R.id.franzoesisch);
        CheckBox checkBoxphysik = (CheckBox) findViewById(R.id.physik);
        CheckBox checkBoxchemie = (CheckBox) findViewById(R.id.chemie);
        CheckBox checkBoxbiologie = (CheckBox) findViewById(R.id.biologie);
        CheckBox checkBoxsowi = (CheckBox) findViewById(R.id.sowi);
        Button weiter = (Button) findViewById(R.id.weiter);
        EditText vorname = (EditText) findViewById(R.id.vorname);
        EditText nachname = (EditText) findViewById(R.id.nachname);
        TextView anzeige = (TextView) findViewById(R.id.ansage);
        EditText geld = (EditText)findViewById(R.id.geld);

        anzeige.setText("");
        weiter.setOnClickListener((v)-> {
                if (!vorname.getText().toString().equals("") && !vorname.getText().toString().equals("Vorname") && !vorname.getText().toString().equals("First given name") && !nachname.getText().toString().equals("") && !nachname.getText().toString().equals("Nachname") && !nachname.getText().toString().equals("Surname") && !geld.getText().toString().equals("") ) {
                    Utils.logDebug("If");
                    String[] daten = new String[]{vorname.getText().toString(), nachname.getText().toString(), "", "", "", "","","","","",""};
                    int i = 2;
                    if (checkBoxMathe.isChecked()) {
                        daten[i] = "Mathe";
                        i++;
                    }
                    if (checkBoxDeutsch.isChecked()) {
                        daten[i] = "Deustch";
                        i++;
                    }
                    if (checkBoxEnglisch.isChecked()) {
                        daten[i] = "Englisch";
                        i++;
                    }
                    if (checkBoxSpanisch.isChecked()) {
                        daten[i] = "Spanisch";
                        i++;
                    }
                    if (checkBoxfranzoesisch.isChecked()) {
                        daten[i] = "Französisch";
                        i++;
                    }
                    if (checkBoxphysik.isChecked()) {
                        daten[i] = "Physik";
                        i++;
                    }
                    if (checkBoxchemie.isChecked()) {
                        daten[i] = "Chemie";
                        i++;
                    }
                    if (checkBoxbiologie.isChecked()) {
                        daten[i] = "Biologie";
                        i++;
                    }
                    if (checkBoxsowi.isChecked()) {
                        daten[i] = "Sozialwissenschaften";
                    }

                    ContentValues values = new ContentValues();
                    values.put(NACHHILFE_VORNAME, daten[0]);
                    values.put(NACHHILFE_NACHNAME, daten[1]);
                    values.put(NACHHILFE_STUFE, Utils.getUserStufe());
                    String faecher = "";
                    int a = 2;
                    if(daten[a].equals("")){
                        anzeige.setVisibility(View.VISIBLE);
                        anzeige.setText("Bitte wähle ein Fach aus");
                    }else {
                        while (a != daten.length && !daten[a].equals("")) {
                            faecher += daten[a] + ",";
                            a++;
                        }
                        values.put(NACHHILFE_FAECHER, faecher);
                        Utils.logDebug(values.size());
                        long insert = sqLiteDatabase.insert(TABLE_NACHHILFEBOERSE, null, values);
                        Utils.logDebug(insert);
                        startActivity(weiterIntent);
                    }
                } else { Utils.logDebug("else");
                    String ausgabe = "";
                    if (vorname.getText().toString().equals("") || vorname.getText().toString().equals("Vorname")) {
                        ausgabe = "Bitte gebe deinen Vornamen an! ";
                    }
                    if (vorname.getText().toString().equals("") || vorname.getText().toString().equals("Nachname")) {
                        ausgabe += "Bitte gebe deinen Nachnamen an! ";
                    }
                    if (geld.getText().toString().equals("")) {
                        ausgabe += "Bitte gebe eine Anzahl an Euros an die du nehmen willst für eine Stunde!";
                    }
                    anzeige.setVisibility(View.VISIBLE);
                    anzeige.setText(ausgabe);
                }

        });

    }

    protected void verbinden() {
        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorNachhilfeboerse(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getWritableDatabase();
    }
}




