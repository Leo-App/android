package de.slgdev.svBriefkasten.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;

public class BriefkastenActivity extends LeoAppNavigationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefkasten);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_briefkasten;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawerLayout;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.title_activity_briefkasten;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.letterbox;
    }

    @Override
    protected String getActivityTag() {
        return "SV-Activity";
    }
}
