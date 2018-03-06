package de.slgdev.stundenplan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.stundenplan.StundenplanView;

public class StundenplanBildActivity extends ActionLogActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 42;
    private StundenplanView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stundenplan_image);
        Utils.getController().registerStundenplanBildActivity(this);
        view = findViewById(R.id.image);
        initFabSAVE();
    }

    @Override
    protected String getActivityTag() {
        return "StundenplanBildActivity";
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerStundenplanBildActivity(null);
    }

    public void initFabSAVE() {
        FloatingActionButton fabSAVE = findViewById(R.id.fabSAVE);
        fabSAVE.setOnClickListener(view -> saveImage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage();
                }
            }
        }
    }

    public void saveImage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            Bitmap                bitmap = view.bitmap;
            ByteArrayOutputStream bytes  = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            try {
                new File(getDirectory()).mkdirs();
                final File image = new File(getDirectory() + getFilename());
                image.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(image);
                outputStream.write(bytes.toByteArray());
                outputStream.close();
                final Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), getString(R.string.saved_as) + getFilename(), Snackbar.LENGTH_LONG);
                snackbar.setAction(R.string.open, v -> {
                    snackbar.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(image), "image/jpeg");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                });
                snackbar.show();
            } catch (IOException e) {
                Utils.logError(e);
            }
        }
    }

    private String getFilename() {
        Calendar c = new GregorianCalendar();
        c.setTime(new Date());
        return "stundenplan_" +
                c.get(Calendar.YEAR) + '_' +
                c.get(Calendar.MONTH) + '_' +
                c.get(Calendar.DAY_OF_MONTH) + '_' +
                c.get(Calendar.HOUR_OF_DAY) + '_' +
                c.get(Calendar.MINUTE) + ".jpg";
    }

    private String getDirectory() {
        return Environment.getExternalStorageDirectory()
                + File.separator
                + "LeoApp"
                + File.separator;
    }
}