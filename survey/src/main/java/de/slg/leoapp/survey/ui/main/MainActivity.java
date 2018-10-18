package de.slg.leoapp.survey.ui.main;

import android.os.Bundle;
import android.view.View;

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
        return R.layout.survey_activity_main;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.string.survey_feature_title;
    }

    @NotNull
    @Override
    protected String getActivityTag() {
        return "survey_feature_main";
    }

    @Override
    protected int getActionIcon() {
        return R.drawable.ic_arrow_left;
    }

    @Nullable
    @Override
    protected View.OnClickListener getActionListener() {
        return view -> {
            //todo
        };
    }
}
