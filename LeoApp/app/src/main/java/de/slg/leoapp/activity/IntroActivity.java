package de.slg.leoapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;

/**
 * IntroActivity.
 * <p>
 * Diese Activity verschafft dem User bei erstmaligem Starten einen kurzen Überblick über die App. Kann bei Bedarf erweitert werden.
 *
 * @author Mirko
 * @version 2017.1111
 * @since 0.5.7
 */
@SuppressWarnings("deprecation")
public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), getString(R.string.intro1), R.mipmap.leo_app_icon, ContextCompat.getColor(this, R.color.introSlide1)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro2_title), getString(R.string.intro2), R.drawable.ic_intro_image2, ContextCompat.getColor(this, R.color.introSlide2))); // oder nutze den Klausurplan, um dich über anstehende Arbeiten zu informieren

        addSlide(AppIntroFragment.newInstance(getString(R.string.intro3_title), getString(R.string.intro3), R.drawable.ic_intro_message3, ContextCompat.getColor(this, R.color.introSlide3))); /// und mit dem digitalen Schwarzen Brett immer auf dem neusten Stand bleiben.
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro4_title), getString(R.string.intro4), R.drawable.ic_intro_message4, ContextCompat.getColor(this, R.color.introSlide4)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.intro5_title), getString(R.string.intro5), R.drawable.ic_intro5, ContextCompat.getColor(this, R.color.introSlide4)));

        showStatusBar(false);
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void finish() {
        super.finish();

        Utils.getController().getPreferences()
                .edit()
                .putString("previousVersion", Utils.getAppVersionName())
                .apply();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        this.finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
    }
}
