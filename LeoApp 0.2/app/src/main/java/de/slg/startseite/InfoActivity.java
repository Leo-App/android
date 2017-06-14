package de.slg.startseite;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import de.slg.leoapp.R;

public class InfoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle b) {

        super.onCreate(b);
        setContentView(R.layout.activity_info);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbarInfo);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle(getString(R.string.toolbar_title_screen));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        int verCode = pInfo.versionCode;

        ((TextView) findViewById(R.id.textView6Info)).setText("Version " + version + " (" + verCode + ")");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}
