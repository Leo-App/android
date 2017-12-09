package de.slg.essensbons.task;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.slg.essensbons.activity.EssensQRActivity;
import de.slg.essensbons.activity.fragment.QRFragment;
import de.slg.essensbons.utility.MCrypt;
import de.slg.essensbons.utility.Order;
import de.slg.leoapp.R;
import de.slg.leoapp.sqlite.SQLiteConnectorEssensbons;
import de.slg.leoapp.utility.Utils;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class QRWriteTask extends AsyncTask<View, Integer, Bitmap> {

    private final Fragment qr;
    private final boolean  onAppStart;
    private       View     target;
    private       short    menu;
    private       boolean  connection;
    private       String   descr;

    public QRWriteTask(Fragment a, boolean startedOnAppStart) {
        qr = a;
        connection = true;
        onAppStart = startedOnAppStart;
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    protected Bitmap doInBackground(View... params) {
        target = params[0];
        connection = hasActiveInternetConnection();

        if (!(EssensQRActivity.sharedPref.getBoolean("pref_key_status_loggedin", false)))
            return null;

        if (connection) {
            if (onAppStart) {
                if (EssensQRActivity.sharedPref.getBoolean("pref_key_qr_sync", false))
                    saveNewestEntries();
            } else
                saveNewestEntries();
        }

        Order act = getRecentEntry();
        if (act == null)
            return null;

        menu = act.getMenu();
        descr = act.getDescr();

        String customerString = EssensQRActivity.sharedPref.getString("pref_key_qr_id", "00000");
        if (customerString.equals(""))
            return null;

        int        customerid = Integer.parseInt(customerString);
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyy");
        Date       date       = new Date();
        String     dateS      = dateFormat.format(date);
        dateS = dateS.substring(0, 4) + dateS.substring(5);
        String code = customerString + "-M" + act.getMenu() + "-" + dateS + "-";
        int    c1   = Integer.valueOf(dateS.substring(0, 2) + dateS.substring(4));
        customerid = (act.getMenu() == 2) ? customerid / 2 : customerid / 3;
        c1 += customerid;
        code += String.valueOf(c1);

        return createNewQR(code);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        ProgressBar spinner = (ProgressBar) target.findViewById(R.id.progressBar1);
        spinner.setVisibility(INVISIBLE);
        boolean loggedin = Utils.getController().getPreferences().getBoolean("pref_key_status_loggedin", false);
        if (!connection)
            ((QRFragment) qr).showSnackBarNoConnection();

        if (result != null) {
            ((ImageView) target.findViewById(R.id.imageView)).setImageBitmap(result);
            ((TextView) target.findViewById(R.id.textViewMenu)).setText(qr.getString(R.string.qr_display_menu) + "  " + menu);
            ((TextView) target.findViewById(R.id.textViewMenuDetails)).setText(descr);
            target.findViewById(R.id.textViewMenu).setVisibility(VISIBLE);
            target.findViewById(R.id.imageView).setVisibility(VISIBLE);
            target.findViewById(R.id.textViewMenuDetails).setVisibility(VISIBLE);
        } else {
            ((ImageView) target.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_qrcode_crossedout);
            if (loggedin) {
                ((TextView) target.findViewById(R.id.textViewMenu)).setText(Utils.getString(R.string.qr_display_not_ordered));
                ((TextView) target.findViewById(R.id.textViewDatum)).setText(Utils.getString(R.string.no_order));
            } else {
                ((TextView) target.findViewById(R.id.textViewMenu)).setText(Utils.getString(R.string.qr_display_not_loggedin));
                ((TextView) target.findViewById(R.id.textViewDatum)).setText(Utils.getString(R.string.not_loggedin));
            }
            target.findViewById(R.id.textViewMenu).setVisibility(VISIBLE);
            target.findViewById(R.id.textView).setVisibility(INVISIBLE);
            target.findViewById(R.id.imageViewError).setVisibility(VISIBLE);
            target.findViewById(R.id.textViewMenuDetails).setVisibility(INVISIBLE);
        }

        EssensQRActivity.runningSync = false;
        target.findViewById(R.id.progressBar1).setVisibility(GONE);
    }

    private boolean hasActiveInternetConnection() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.lunch.leo-ac.de").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return urlc.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    private Order getRecentEntry() {
        SQLiteDatabase db = EssensQRActivity.sqlh.getReadableDatabase();
        String[] projection = {
                SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_DATE,
                SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_MENU,
                SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_DESCR,
        };
        DateFormat dateFormat    = new SimpleDateFormat("yyyy-MM-dd");
        Date       date          = new Date();
        String     selection     = SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_DATE + " = ?";
        String[]   selectionArgs = {dateFormat.format(date)};
        Cursor cursor = db.query(
                SQLiteConnectorEssensbons.OrderEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.getCount() <= 0)
            return null;
        cursor.moveToNext();
        String desc = String.valueOf(cursor.getString(cursor.getColumnIndex(SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_DESCR)));
        String s    = String.valueOf(cursor.getShort(cursor.getColumnIndex(SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_MENU)));
        Order  o    = new Order(date, Short.parseShort(s), desc);
        cursor.close();
        return o;
    }

    private void saveNewestEntries() {
        BufferedReader in     = null;
        String         result = "";
        try {
            URL interfaceDB = new URL(Utils.BASE_URL_PHP + "essenqr/qr_database.php?id=" + EssensQRActivity.sharedPref.getString("pref_key_qr_id", "00000")
                    + "&auth=2SnDS7GBdHf5sd");
            Log.d("LeoApp", interfaceDB.toString());
            in = null;
            in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<"))
                    result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        MCrypt mCrypt = new MCrypt();
        try {
            result = new String(mCrypt.decrypt(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[]       data    = result.split("_next_");
        SQLiteDatabase db      = EssensQRActivity.sqlh.getWritableDatabase();
        DateFormat     df      = new SimpleDateFormat("yyyy-mm-dd");
        Date           highest = null;
        try {
            highest = df.parse("1900-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int amount = 0;
        for (String s : data) {
            if (!s.contains("_seperator_"))
                continue;
            ContentValues values = new ContentValues();
            amount++;
            try {
                Date d = df.parse(s.split("_seperator_")[0]);
                if (d.after(highest))
                    highest = d;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("LeoApp", "Date " + s.split("_seperator_")[0]);
            values.put(SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_DATE, s.split("_seperator_")[0]);
            values.put(SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_MENU, s.split("_seperator_")[1]);
            values.put(SQLiteConnectorEssensbons.OrderEntry.COLUMN_NAME_DESCR, s.split("_seperator_")[2]);
            try {
                db.insert(SQLiteConnectorEssensbons.OrderEntry.TABLE_NAME, null, values);
            } catch (Exception e) {
                Log.e("LeoApp", "Failed UNIQUE");
            }
        }
        db.close();
        db = EssensQRActivity.sqlh.getWritableDatabase();
        String dateString = df.format(highest);
        if (dateString == null) {
            db.close();
            return;
        }
        Log.d("LeoApp", dateString);
        Cursor c = db.rawQuery("SELECT ID FROM USERORDERS WHERE DATEU = ?", new String[]{dateString});
        c.moveToFirst();
        if (c.getCount() == 0)
            return;
        db.execSQL("INSERT INTO " + SQLiteConnectorEssensbons.StatisticsEntry.TABLE_NAME
                + " (SYNCDATE, AMOUNT, LASTORDER) VALUES ('" + df.format(new Date()) + "', " + amount + ", " + c.getInt(c.getColumnIndex("ID")) + ")");
        c.close();
        db.close();
    }

    private Bitmap createNewQR(String s) {
        MultiFormatWriter mFW = new MultiFormatWriter();
        Bitmap            bM  = null;
        Resources         r   = qr.getResources();
        int               px  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, r.getDisplayMetrics());
        try {
            BitMatrix bitM = mFW.encode(s, BarcodeFormat.QR_CODE, px, px);
            bM = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < px; i++) {
                for (int j = 0; j < px; j++) {
                    int c = (bitM.get(i, j)) ? Color.BLACK : Color.WHITE;
                    bM.setPixel(i, j, c);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bM;
    }
}
