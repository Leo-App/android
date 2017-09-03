package de.slg.stimmungsbarometer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.slg.leoapp.NotificationService;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class AbstimmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.context = getApplicationContext();

        Utils.getNotificationManager().cancel(NotificationService.ID_BAROMETER);
        Utils.closeAll();

        final AbstimmDialog dialog = new AbstimmDialog(this);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.findViewById(R.id.buttonDialog1).setVisibility(View.GONE);
            }
        }, 100);
    }
}