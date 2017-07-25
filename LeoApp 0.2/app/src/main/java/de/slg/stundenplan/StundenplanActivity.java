package de.slg.stundenplan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class StundenplanActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 42;
    private StundenplanView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stundenplan_image);

        view = (StundenplanView) findViewById(R.id.image);
        initToolbar();
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
            saveImage();
        }
        return true;
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.yourTimeTable));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void saveImage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            Bitmap bitmap = view.bitmap;

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            try {
                new File(getDirectory()).mkdirs();

                final File image = new File(getDirectory() + getFilename());
                image.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(image);
                outputStream.write(bytes.toByteArray());

                outputStream.close();

                final Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "Saved as " + getFilename(), Snackbar.LENGTH_LONG);
                snackbar.setAction("Open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(image), "image/jpeg");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                snackbar.show();
            } catch (IOException e) {
                e.printStackTrace();
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

    public class StundenplanView extends View {

        Bitmap bitmap;
        private final Canvas canvas;
        private boolean isInitialized;
        private final Paint paint;
        private int height, width, baseLineY, baseLineX, abstandX, abstandY, paddingX, paddingY, baseline2Y;

        @Override
        public void onDraw(Canvas canvas) {
            if (!isInitialized)
                init();
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }

        public StundenplanView(Context context) {
            super(context);
            canvas = new Canvas();
            paint = new Paint();
            isInitialized = false;
        }

        public StundenplanView(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            canvas = new Canvas();
            paint = new Paint();
            isInitialized = false;
        }

        public StundenplanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            canvas = new Canvas();
            paint = new Paint();
            isInitialized = false;
        }

        private void init() {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            canvas.setBitmap(bitmap);
            canvas.drawColor(ContextCompat.getColor(getContext(), android.R.color.white));

            height = getHeight();
            width = getWidth();

            baseLineX = width / 20;
            paddingX = width / 100;
            abstandX = (width - baseLineX * 2) / 5;

            baseLineY = height / 10;
            paddingY = height / 100;
            baseline2Y = baseLineY + 3 * paddingY;
            abstandY = (height - baseline2Y - baseLineY) / 10;

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            paint.setTextSize(2 * paddingY);

            drawTimetable();

            isInitialized = true;
        }

        private void drawTimetable() {
            canvas.drawLine(baseLineX, baseLineY, width - baseLineX, baseLineY, paint);
            canvas.drawLine(baseLineX, height - baseLineY, width - baseLineX, height - baseLineY, paint);
            canvas.drawLine(baseLineX, baseLineY, baseLineX, height - baseLineY, paint);
            canvas.drawLine(width - baseLineX, baseLineY, width - baseLineX, height - baseLineY, paint);

            canvas.drawText(getContext().getString(R.string.montag), baseLineX + paddingX, baseLineY + paddingY * 2, paint);
            canvas.drawLine(baseLineX + abstandX, baseLineY, baseLineX + abstandX, height - baseLineY, paint);
            canvas.drawText(getContext().getString(R.string.dienstag), baseLineX + abstandX + paddingX, baseLineY + paddingY * 2, paint);
            canvas.drawLine(baseLineX + abstandX * 2, baseLineY, baseLineX + abstandX * 2, height - baseLineY, paint);
            canvas.drawText(getContext().getString(R.string.mittwoch), baseLineX + abstandX * 2 + paddingX, baseLineY + paddingY * 2, paint);
            canvas.drawLine(baseLineX + abstandX * 3, baseLineY, baseLineX + abstandX * 3, height - baseLineY, paint);
            canvas.drawText(getContext().getString(R.string.donnerstag), baseLineX + abstandX * 3 + paddingX, baseLineY + paddingY * 2, paint);
            canvas.drawLine(baseLineX + abstandX * 4, baseLineY, baseLineX + abstandX * 4, height - baseLineY, paint);
            canvas.drawText(getContext().getString(R.string.freitag), baseLineX + abstandX * 4 + paddingX, baseLineY + paddingY * 2, paint);
            canvas.drawLine(baseLineX, baseline2Y, width - baseLineX, baseline2Y, paint);

            Fach[][] gewaehlteFaecher = new Fach[5][];
            for (int i = 0; i < gewaehlteFaecher.length; i++) {
                gewaehlteFaecher[i] = Utils.getStundDB().gewaehlteFaecherAnTag(i + 1);
            }
            for (int i = 1; i < 10; i++) {
                int yValue = baseline2Y + (i - 1) * abstandY;
                for (int j = 0; j < 5; j++) {
                    Fach[] tag = gewaehlteFaecher[j];
                    if (i - 1 < tag.length) {
                        Fach f = tag[i - 1];
                        String text;
                        if (f.gibName().equals("") && !f.gibNotiz().equals("")) {
                            text = f.gibNotiz().split(" ")[0];
                        } else {
                            text = f.gibName().split(" ")[0];
                        }
                        canvas.drawText(text, baseLineX + abstandX * j + paddingX, yValue + paddingY * 2, paint);
                    }
                }
                canvas.drawLine(baseLineX, yValue + abstandY, width - baseLineX, yValue + abstandY, paint);
            }
        }
    }
}