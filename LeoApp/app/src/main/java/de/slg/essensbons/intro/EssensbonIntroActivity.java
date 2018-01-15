package de.slg.essensbons.intro;

import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;

import de.slg.leoapp.R;
import de.slg.leoapp.activity.fragment.InfoFragmentBuilder;
import de.slg.leoapp.activity.fragment.VerificationFragment;

public class EssensbonIntroActivity extends AppIntro2 {

    private static boolean ignoreSlideChange;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        //InfoFragmentBuilder aus de.slg.leoapp.activity.fragment
        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.qr_intro_title1)
                        .setContent(R.string.qr_intro_title1) //TODO
                        .setImage(R.drawable.ic_qrcode_scan)
                        .setColor(R.color.introSlide1)
                        .build()
        );

        addSlide(VerificationFragment.newInstance(
                R.string.qr_intro_titleVerification,
                R.string.qr_intro_descVerification,
                R.color.introSlide2,
                1
        ));

        showStatusBar(false);
        showSkipButton(false);
        setProgressButtonEnabled(true);
        setDepthAnimation();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
