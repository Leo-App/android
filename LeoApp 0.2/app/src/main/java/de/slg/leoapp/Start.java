package de.slg.leoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.startseite.MainActivity;

public class Start extends Activity {
    public static SharedPreferences pref;

    public static void initPref(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.context = getApplicationContext();
        initPref(getApplicationContext());

        int days = 15;
        try {
            Date     d = new SimpleDateFormat("dd.MM.yyyy").parse(pref.getString("valid_until", "null"));
            Calendar c = new GregorianCalendar();
            for (int i = 1; i <= 14; i++) {
                c.add(Calendar.DAY_OF_MONTH, 1);
                if (c.getTime().after(d)) {
                    days = i;
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Intent main = new Intent(getApplicationContext(), MainActivity.class)
                .putExtra("show_dialog", Utils.showVoteOnStartup())
                .putExtra("days", days);

        startService(new Intent(getApplicationContext(), ReceiveService.class));
        startService(new Intent(getApplicationContext(), NotificationService.class));

        startActivity(main);
        finish();
    }
}