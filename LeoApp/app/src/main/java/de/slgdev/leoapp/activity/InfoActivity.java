package de.slgdev.leoapp.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;

public class InfoActivity extends ActionLogActivity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_info);

        initToolbar();
        initVersionCode();
    }

    private void initVersionCode() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Utils.logError(e);
        }
        String version = pInfo.versionName;
        int    verCode = pInfo.versionCode;

        ((TextView) findViewById(R.id.textView6Info)).setText(getString(R.string.version_code, version, verCode));

    }

    private void initToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.toolbar_name_info));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected String getActivityTag() {
        return "InfoActivity";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
