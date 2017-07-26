package de.slg.stimmungsbarometer;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.slg.leoapp.R;

public class AbstimmActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AbstimmDialog dialog = new AbstimmDialog(this);
        dialog.userid = getIntent().getIntExtra("userid", 0);
        dialog.findViewById(R.id.buttonDialog1).setVisibility(View.GONE);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }
}