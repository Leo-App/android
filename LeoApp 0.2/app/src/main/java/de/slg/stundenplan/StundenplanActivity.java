package de.slg.stundenplan;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.R;

public class StundenplanActivity extends AppCompatActivity {
    private StundenplanView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stundenplan_image);

        view = (StundenplanView) findViewById(R.id.image);
        initToolbar();
    }

    private void saveImage() throws IOException {
        Bitmap bitmap = view.bitmap;

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        String filename = Environment.getExternalStorageDirectory()
                + File.separator + "LeoApp" + File.separator +
                "stundenplan_" + c.get(Calendar.YEAR) + '_' +
                c.get(Calendar.MONTH) + '_' + c.get(Calendar.DAY_OF_MONTH) +
                ".jpg";
        Log.e("Filename", filename);

        File directory = new File(Environment.getExternalStorageDirectory()
                + File.separator + "LeoApp");
        directory.mkdirs();

        File f = new File(filename);
        f.createNewFile();

        FileOutputStream outputStream = new FileOutputStream(f);
        outputStream.write(bytes.toByteArray());

        outputStream.close();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.yourTimeTable));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stundenplan_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_save) {
            try {
                saveImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}