package de.slg.essensbons.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import de.slg.essensbons.dialog.FeedbackDialog;
import de.slg.essensbons.task.QRReadTask;
import de.slg.essensbons.utility.EssensbonUtils;
import de.slg.essensbons.utility.EssensbonCaptureManager;
import de.slg.leoapp.R;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;

public class ScanActivity extends ActionLogActivity implements TaskStatusListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    public void onCreate(Bundle b) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(b);
        setContentView(R.layout.activity_scanqr);

        initToolbar();
        initNavigationBar();

        barcodeScannerView = findViewById(R.id.qrcode_view);

        TextView tooltip = findViewById(R.id.tooltip);
        tooltip.setText("Platziere den QR-Code innerhalb des Rechtecks");
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tooltip.getLayoutParams();
        params.setMargins(0, 0, 0, GraphicUtils.getDisplayHeight()/4);
        tooltip.setLayoutParams(params);

        capture = new EssensbonCaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), b);
        capture.decode();
    }

    private void initNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(getString(R.string.qr_scan_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected String getActivityTag() {
        return "scan-activity";
    }

    public void notifyResult(String result) {
        new QRReadTask().addListener(this).execute(result);
    }

    @Override
    public void taskFinished(Object... params) {
        boolean result = (boolean) params[0];

        long[] interval = result ? new long[]{0, 200, 100, 200} : new long[]{0, 1000, 500, 1000};

        final AlertDialog dialog = new FeedbackDialog(Utils.getController().getEssensbonActivity(), result, (Integer) params[1]);

        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(interval, -1);

        if (EssensbonUtils.isAutoFadeEnabled()) {

            int duration = EssensbonUtils.getFadeTime();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, duration * 1000);

        }

        dialog.show();
    }
}
