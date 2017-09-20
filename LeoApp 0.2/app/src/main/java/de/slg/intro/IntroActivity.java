package de.slg.intro;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import de.slg.leoapp.R;

/**
 * Created by Mirko on 26.08.2017.
 */

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Drawable d = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_chat_bubble_white_24dp);
        d.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        addSlide(AppIntroFragment.newInstance("LeoApp", "Finde hier all die Dinge, die deinen Schulalltag leichter machen werden.", R.mipmap.leo_app_icon , ContextCompat.getColor(this, R.color.introSlide1)));
        addSlide(AppIntroFragment.newInstance("Abläufe vereinfachen", "Zeige Deinen digitalen Essensbon einfach dem Mensapersonal und Du bekommst dein Essen!", R.drawable.ic_intro_image2 , ContextCompat.getColor(this, R.color.introSlide2))); // oder nutze den Klausurplan, um dich über anstehende Arbeiten zu informieren
        addSlide(AppIntroFragment.newInstance("Kommunikation verbessern", "Mit dem Messenger einfach deine Lehrer und Freunde kontaktieren, ohne deine Nummer preiszugeben", R.drawable.ic_intro3 , ContextCompat.getColor(this, R.color.introSlide3))); /// und mit dem digitalen Schwarzen Brett immer auf dem neusten Stand bleiben.
        addSlide(AppIntroFragment.newInstance("Alles in einer App", "Dein Stundenplan und deine nächsten Klassenarbeiten sind nur noch zwei Klicks entfernt!", R.mipmap.leo_app_icon , ContextCompat.getColor(this, R.color.introSlide1)));


        setBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        //setSeparatorColor(ContextCompat.getColor(this, R.color.colorAccent));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
