package de.slg.essensqr;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import de.slg.leoapp.R;

class QRReadTask extends AsyncTask<String, Integer, Boolean> {

    private final WrapperQRActivity act;
    private int orderedMenu;

    QRReadTask(WrapperQRActivity act) {

        this.act = act;

    }

    @Override
    protected Boolean doInBackground(String... params) {

        if (checkValid(params[0])) {

            String[] parts = params[0].split("-");

            String day = parts[2].substring(0, 2);
            String month = parts[2].substring(2, 4);
            String year = parts[2].substring(4, 7);
            orderedMenu = Integer.parseInt(String.valueOf(params[0].charAt(7)));

            SQLiteDatabase db = WrapperQRActivity.sqlh.getReadableDatabase();

            String[] projection = {
                    SQLiteHandler.ScanEntry.COLUMN_NAME_DATE,
            };

            String selection = SQLiteHandler.ScanEntry.COLUMN_NAME_DATE + " = ? AND " + SQLiteHandler.ScanEntry.COLUMN_NAME_CUSTOMERID + " = ?";
            String[] selectionArgs = {"2" + year + "-" + month + "-" + day, parts[0]};

            Cursor cursor = db.query(
                    SQLiteHandler.ScanEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor.getCount() == 0) {

                cursor.close();

                db = WrapperQRActivity.sqlh.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(SQLiteHandler.ScanEntry.COLUMN_NAME_CUSTOMERID, params[0].split("-")[0]);
                values.put(SQLiteHandler.ScanEntry.COLUMN_NAME_DATE, "2" + year + "-" + month + "-" + day);
                db.insert(SQLiteHandler.ScanEntry.TABLE_NAME, null, values);

                return true;

            } else {
                cursor.close();
                return false;
            }
        } else
            return false;
    }

    private boolean checkValid(String s) {

        String[] parts = s.split("-");

        Log.d("LeoApp", "passed no test yet");

        if (parts.length != 4)
            return false;

        Log.d("LeoApp", "passed module test");

        if (parts[1].length() != 2 || parts[1].charAt(0) != 'M' || (parts[1].charAt(1) != '1' && parts[1].charAt(1) != '2'))
            return false;

        Log.d("LeoApp", "passed menu-format test");

        if (parts[2].length() != 7)
            return false;

        Log.d("LeoApp", "passed date size test");

        try {
            int day = Integer.parseInt(parts[2].substring(0, 2));
            int month = Integer.parseInt(parts[2].substring(2, 4));

            if (day > 31 || day < 1)
                return false;

            if (month > 12 || month < 1)
                return false;

        } catch (NumberFormatException e) {

            return false;

        }

        Log.d("LeoApp", "passed logic date test");

        String subsum = "" + parts[2].substring(0, 2) + "" + parts[2].substring(4);

        Log.d("LeoApp", subsum);

        try {
            int menu = Integer.parseInt(String.valueOf(parts[1].charAt(1)));
            int customerid = Integer.parseInt(parts[0]);
            customerid = (menu == 1) ? customerid / 3 : customerid / 2;
            int checksum = Integer.parseInt(subsum) + customerid;

            if (!String.valueOf(checksum).equals(parts[3]))
                return false;

        } catch (NumberFormatException e) {
            return false;
        }
        Log.d("LeoApp", "passed checksum test");

        return true;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    protected void onPostExecute(Boolean result) {

        final AlertDialog builder = new AlertDialog.Builder(act).create();
        LayoutInflater inflater = act.getLayoutInflater();
        View v;
        long[] interval;
        if (result) {
            v = inflater.inflate(R.layout.dialog_valid, null);
            ((TextView) v.findViewById(R.id.textView4)).setText(act.getString(R.string.dialog_desc_valid) + "\t" + orderedMenu);
            interval = new long[]{0, 200, 100, 200};
        } else {
            v = inflater.inflate(R.layout.dialog_invalid, null);
            interval = new long[]{0, 1000, 500, 1000};
        }

        Vibrator vb = (Vibrator) act.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(interval, -1);

        builder.setView(v);


        if (WrapperQRActivity.sharedPref.getBoolean("pref_key_qr_autofade", false)) {

            int duration = WrapperQRActivity.sharedPref.getInt("pref_key_qr_autofade_time", 3);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    builder.dismiss();

                }
            }, duration * 1000);

        }


        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                act.scV.startCamera(0);
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                act.scV.setResultHandler(act);
                act.scV.startCamera(0);
            }
        });

        builder.show();


    }

}