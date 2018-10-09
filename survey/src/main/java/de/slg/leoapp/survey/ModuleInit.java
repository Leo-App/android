package de.slg.leoapp.survey;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import de.slg.leoapp.annotation.Module;
import de.slg.leoapp.core.modules.Feature;
import de.slg.leoapp.core.modules.Notification;
import de.slg.leoapp.core.ui.LeoAppFeatureActivity;
import de.slg.leoapp.core.ui.intro.IntroFragment;
import de.slg.leoapp.core.utility.Permissions;
import de.slg.leoapp.survey.ui.main.MainActivity;

@Module(name = "survey")
public class ModuleInit implements Feature {
    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public int getFeatureId() {
        return 0x123e8ab;
    }

    @Override
    public int getNecessaryPermission() {
        return Permissions.PERMISSION_STUDENT;
    }

    @NotNull
    @Override
    public Class<? extends LeoAppFeatureActivity> getEntryActivity() {
        return MainActivity.class;
    }

    @Nullable
    @Override
    public Notification getNotification() {
        return null;
    }

    @NotNull
    @Override
    public Class<? extends IntroFragment>[] getIntroFragments() {
        return new Class[0];
    }
}