package de.slg.essensbons.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.paolorotolo.appintro.AppIntro2;

import de.slg.essensbons.task.EssensbonLoginTask;
import de.slg.essensbons.utility.Authenticator;
import de.slg.essensbons.utility.EssensbonUtils;
import de.slg.leoapp.task.general.TaskStatusListener;
import de.slg.leoapp.R;
import de.slg.leoapp.activity.fragment.AbstractOrderedFragment;
import de.slg.leoapp.activity.fragment.InfoFragmentBuilder;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.Utils;
import de.slg.startseite.activity.MainActivity;

public class EssensbonIntroActivity extends AppIntro2 implements TaskStatusListener {

    private static boolean ignoreSlideChange;
    private static boolean running;

    private int      verificationSlide;
    private Fragment verificationFragment;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        Utils.getController().registerEssensbonIntroActity(this);

        running = false;

        initSlides();
        showStatusBar(false);
        showSkipButton(false);
        setProgressButtonEnabled(true);
        setDepthAnimation();
    }

    @Override
    public void finish() {
        super.finish();

        Utils.getController().getPreferences()
                .edit()
                .putBoolean("intro_shown_qr", true)
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
    public void onBackPressed() {
        Utils.getController().closeActivities();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public boolean onCanRequestNextPage() {
        return !running;
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        onSlideChanged((AbstractOrderedFragment) oldFragment, (AbstractOrderedFragment) newFragment);
    }

    public void onSlideChanged(@Nullable AbstractOrderedFragment oldFragment, @Nullable final AbstractOrderedFragment newFragment) {

        if (oldFragment == null || newFragment == null || ignoreSlideChange) {
            ignoreSlideChange = false;
            return;
        }

        if (oldFragment.getPosition() < newFragment.getPosition() && oldFragment.getPosition() == verificationSlide) {
            cancel(oldFragment);
        } else if (oldFragment.getPosition() > newFragment.getPosition() && newFragment.getPosition() == verificationSlide) {
            cancel(oldFragment);
        } else if (newFragment.getPosition() == verificationSlide) {
            ImageButton nextButton = findViewById(R.id.next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logIn();
                }
            });
        } else {
            ImageButton nextButton = findViewById(R.id.next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPager().setCurrentItem(getPager().getCurrentItem() + 1);
                }
            });
        }

    }

    @Override
    public void taskFinished(Object... result) {
        running = false;
        verificationFragment.getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);

        switch ((Authenticator) result[0]) {

            case NO_CONNECTION:
                GraphicUtils.sendToast(getString(R.string.need_internet));
                EssensbonUtils.setLoginStatus(false);
                break;
            case NOT_VALID:
                GraphicUtils.sendToast(getString(R.string.data_differs));
                EssensbonUtils.setLoginStatus(false);
                break;
            case VALID:
                ignoreSlideChange = true;
                getPager().setCurrentItem(verificationSlide + 1);
                EssensbonUtils.setLoginStatus(true);
                break;
        }
    }

    private void initSlides() {
        if (getIntent().getExtras() != null) {
            addSlide(
                    new InfoFragmentBuilder()
                            .setTitle(R.string.qr_intro_title1)
                            .setContent(R.string.qr_intro_desc1)
                            .setImage(R.drawable.ic_qrcode_scan)
                            .setColor(R.color.introSlide1)
                            .setPosition(0)
                            .build()
            );

            verificationSlide = 1;
            verificationFragment = LoginFragment.newInstance(
                    R.string.qr_intro_titleVerification,
                    R.string.qr_intro_descVerification,
                    R.color.introSlide2,
                    verificationSlide);

            addSlide(verificationFragment);
        } else {
            verificationSlide = 0;
            verificationFragment = LoginFragment.newInstance(
                    R.string.qr_intro_titleVerification,
                    R.string.qr_intro_descVerification,
                    R.color.introSlide2,
                    verificationSlide);

            addSlide(verificationFragment);

            findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logIn();
                }
            });
        }

        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.intro_finished_title)
                        .setContent(R.string.intro_finished_desc)
                        .setImage(R.drawable.intro_finished)
                        .setPosition(verificationSlide + 1)
                        .setColor(R.color.colorSatisfied)
                        .build()
        );
    }

    private void cancel(final AbstractOrderedFragment oldFragment) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ignoreSlideChange = true;
                getPager().setCurrentItem(oldFragment.getPosition());
            }
        }, 1);
    }

    private void logIn() {
        if (running)
            return;

        running = true;

        View v = verificationFragment.getView();

        EditText name = v.findViewById(R.id.editText1);
        EditText password = v.findViewById(R.id.editText2);

        String userName = name.getText().toString();
        String userPassword = password.getText().toString();

        if (userName.length() == 0 || userPassword.length() == 0) {
            GraphicUtils.sendToast(getString(R.string.data_differs));
            running = false;
            return;
        }

        if (!userName.matches("[0-9]{5}")) {
            GraphicUtils.sendToast(R.string.customer_number);
            running = false;
            return;
        }

        v.findViewById(R.id.progressBarVerification).setVisibility(View.VISIBLE);

        EssensbonUtils.setCustomerId(userName);
        EssensbonUtils.setPassword(userPassword);

        new EssensbonLoginTask().addListener(this).execute();
    }

}
