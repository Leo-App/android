package de.slg.leoapp.survey.ui.main;

import org.jetbrains.annotations.NotNull;

import de.slg.leoapp.core.ui.LeoAppFeatureActivity;
import de.slg.leoapp.survey.R;

public class MainActivity extends LeoAppFeatureActivity {
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
}
