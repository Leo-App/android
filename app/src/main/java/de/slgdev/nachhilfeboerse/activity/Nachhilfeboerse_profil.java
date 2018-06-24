package de.slgdev.nachhilfeboerse.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.jar.Attributes;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.sqlite.SQLiteConnectorNachhilfeboerse;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.nachhilfeboerse.task.SyncMaster;

import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse;
import static de.slgdev.leoapp.R.layout.activity_nachhilfeboerse_profil;

/**
 * Created by Benno on 19.06.2018.
 */

public class Nachhilfeboerse_profil extends ActionLogActivity {

    SQLiteConnectorNachhilfeboerse sqLiteConnector;
    SQLiteDatabase sqLiteDatabase;

    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(activity_nachhilfeboerse_profil);
        initToolbar();
        Intent intent = getIntent();
        TextView vorname = findViewById(R.id.vorname);
        TextView nachname = findViewById(R.id.nachname);
        TextView stufe = findViewById(R.id.stufe);
        TextView geld = findViewById(R.id.geld);
        verbinden();
        SyncMaster task = new SyncMaster();
        task.execute();
        Utils.logDebug(intent.getStringExtra("Name"));
        Cursor cursor = sqLiteDatabase.query(false,
                SQLiteConnectorNachhilfeboerse.TABLE_NACHHILFEBOERSE,
                new String[]{SQLiteConnectorNachhilfeboerse.NACHHILFE_VORNAME,SQLiteConnectorNachhilfeboerse.NACHHILFE_GELD,SQLiteConnectorNachhilfeboerse.NACHHILFE_NACHNAME,SQLiteConnectorNachhilfeboerse.NACHHILFE_STUFE},
                SQLiteConnectorNachhilfeboerse.NACHHILFE_VORNAME + "= " + "'" + intent.getStringExtra("Name") + "'", null,
                null, null, null, null);
        cursor.moveToFirst();
        vorname.setText("Vorname : " + cursor.getString(0));
        nachname.setText("Nachname : " + cursor.getString(2));
        stufe.setText("Stufe : " + cursor.getString(3));
        geld.setText("Euro/Stunde : " + cursor.getString(1));


        Button chatroom = findViewById(R.id.chatroom);
        Intent chatraum = new Intent(Utils.getContext(), NachhilfeboerseActivity.class);
        chatroom.setOnClickListener(View -> startActivity(chatraum));
    }

    private void initToolbar() {

    }

    protected void verbinden() {
        if (sqLiteConnector == null)
            sqLiteConnector = new SQLiteConnectorNachhilfeboerse(Utils.getContext());
        if (sqLiteDatabase == null)
            sqLiteDatabase = sqLiteConnector.getReadableDatabase();
    }

    @Override
    protected String getActivityTag() {
        return "Nachhilfeboerse_profil";
    }
}
