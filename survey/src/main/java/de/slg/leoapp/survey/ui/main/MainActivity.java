package de.slg.leoapp.survey.ui.main;

import android.os.Bundle;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.slg.leoapp.core.ui.LeoAppFeatureActivity;
import de.slg.leoapp.core.utility.ViewBinder;
import de.slg.leoapp.survey.R;

public class MainActivity extends LeoAppFeatureActivity {

    @Override
    protected void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        ViewBinder.bind(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected int getNavigationHighlightId() {
        return 0x123e8ab;
    }

    @NotNull
    @Override
    protected String getActivityTag() {
        return "survey_feature_main";
    }

    @Override
    protected boolean usesActionButton() {
        return true;
    }

    @Override
    protected int getActionIcon() {
        return R.drawable.ic_arrow_left;
    }
}
