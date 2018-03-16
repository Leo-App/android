package de.slgdev.leoapp.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppLayerActivity;

public class InfoActivity extends LeoAppLayerActivity {
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        initVersionCode();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_info;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.toolbar_name_info;
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

    @Override
    protected String getActivityTag() {
        return "InfoActivity";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        finish();
        return true;
    }
}
