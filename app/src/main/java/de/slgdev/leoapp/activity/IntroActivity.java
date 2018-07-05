package de.slgdev.leoapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.paolorotolo.appintro.AppIntro2;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.activity.fragment.AbstractOrderedFragment;
import de.slgdev.leoapp.activity.fragment.DeviceFragment;
import de.slgdev.leoapp.activity.fragment.DisclaimerFragmentBuilder;
import de.slgdev.leoapp.activity.fragment.InfoFragmentBuilder;
import de.slgdev.leoapp.activity.fragment.VerificationFragment;
import de.slgdev.leoapp.task.RegistrationTask;
import de.slgdev.leoapp.task.SyncUserTask;
import de.slgdev.leoapp.task.VerificationTask;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.ResponseCode;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.utility.datastructure.List;

/**
 * IntroActivity.
 * <p>
 * Diese Activity verschafft dem User bei erstmaligem Starten einen kurzen Überblick über die App. Kann bei Bedarf erweitert werden.
 *
 * @author Gianni
 * @version 2017.2312
 * @since 0.5.7
 */
public class IntroActivity extends AppIntro2 {

    private static final int VERIFICATION_SLIDE = 6;
    private static boolean running;
    private static boolean ignoreSlideChange;
    private static boolean dismissable;
    private static List<Fragment> slides;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = false;
        dismissable = false;

        //Info-Slides
        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.app_name)
                        .setContent(R.string.intro1)
                        .setImage(R.mipmap.leo_app_icon)
                        .setColor(R.color.introSlide1)
                        .build()
        );

        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.intro2_title)
                        .setContent(R.string.intro2)
                        .setImage(R.drawable.intro_image2)
                        .setColor(R.color.introSlide2)
                        .build()
        );

        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.intro3_title)
                        .setContent(R.string.intro3)
                        .setImage(R.drawable.intro_message3)
                        .setColor(R.color.introSlide3)
                        .build()
        );

        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.intro4_title)
                        .setContent(R.string.intro4)
                        .setImage(R.drawable.intro_message4)
                        .setColor(R.color.introSlide5)
                        .build()
        );

        addSlide(
                new InfoFragmentBuilder()
                        .setTitle(R.string.intro5_title)
                        .setContent(R.string.intro5)
                        .setImage(R.drawable.intro_ic5)
                        .setColor(R.color.introSlide1)
                        .build()
        );

        addSlide(
                new DisclaimerFragmentBuilder()
                        .setTitle(R.string.please_read)
                        .setContent(R.string.intro_info)
                        .setColor(R.color.introSlide3)
                        .build()
        );

        if (getIntent().getExtras() == null) {

            //Verifiation-Slide
            addSlide(
                    VerificationFragment.newInstance(R.string.verification_title,
                            R.string.verification_desc,
                            R.color.introSlide1,
                            VERIFICATION_SLIDE)
            );

            addSlide(
                    DeviceFragment.newInstance(R.string.device_slide_title,
                            R.string.device_slide_content,
                            R.color.introSlide3,
                            VERIFICATION_SLIDE + 1)
            );

            //Success-Slide
            addSlide(
                    new InfoFragmentBuilder()
                            .setTitle(R.string.intro_finished_title)
                            .setContent(R.string.intro_finished_desc)
                            .setImage(R.drawable.intro_finished)
                            .setPosition(VERIFICATION_SLIDE + 2)
                            .setColor(R.color.colorSatisfied)
                            .build()
            );
        }

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
        Utils.getController().getMainActivity().initFeatureCards();
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (dismissable)
            super.onBackPressed();
    }

    @Override
    public boolean onCanRequestNextPage() {
        return !running;
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        onSlideChanged((AbstractOrderedFragment) oldFragment, (AbstractOrderedFragment) newFragment);
    }

    @Override
    public void addSlide(@NonNull Fragment fragment) {
        if (slides == null)
            slides = new List<>();

        slides.append(fragment);

        super.addSlide(fragment);
    }

    public void onSlideChanged(@Nullable AbstractOrderedFragment oldFragment, @Nullable final AbstractOrderedFragment newFragment) {

        if (oldFragment == null || newFragment == null || ignoreSlideChange) {
            ignoreSlideChange = false;
            return;
        }

        if (oldFragment.getPosition() < newFragment.getPosition() && oldFragment.getPosition() == VERIFICATION_SLIDE) {
            cancel(oldFragment);
        } else if (oldFragment.getPosition() > newFragment.getPosition() && newFragment.getPosition() == VERIFICATION_SLIDE) {
            cancel(oldFragment);
        } else if (newFragment.getPosition() == VERIFICATION_SLIDE) {
            ImageButton nextButton = findViewById(R.id.next);
            nextButton.setOnClickListener(v -> verifyLoginData());
        } else {
            ImageButton nextButton = findViewById(R.id.next);
            nextButton.setOnClickListener(v -> getPager().setCurrentItem(getPager().getCurrentItem() + 1));
            dismissable = newFragment.getPosition() != 0;
        }
    }

    private void onSynchronisationProcessed(ResponseCode response, View v) {
        switch (response) {
            case NO_CONNECTION:
            case AUTH_FAILED:
            case SERVER_FAILED:
                GraphicUtils.sendToast(getString(R.string.error_later));
                running = false;
                break;
            case SUCCESS:
                ignoreSlideChange = true;
                getPager().setCurrentItem(VERIFICATION_SLIDE + 2);
                v.findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                running = false;
                break;
        }
    }

    private void onVerificationProcessed(ResponseCode response) {
        running = false;
        slides.getObjectAt(VERIFICATION_SLIDE).getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
        switch (response) {
            case NO_CONNECTION:
                GraphicUtils.sendToast(R.string.snackbar_no_connection_info);
                break;
            case AUTH_FAILED:
                GraphicUtils.sendToast(getString(R.string.data_differs));
                break;
            case SERVER_FAILED:
                GraphicUtils.sendToast(getString(R.string.error_later));
                break;
            case SUCCESS:
                if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
                    Utils.getController().getPreferences()
                            .edit()
                            .putBoolean("pref_key_notification_test", false)
                            .putBoolean("pref_key_notification_essensqr", false)
                            .putBoolean("pref_key_notification_news", false)
                            .putBoolean("pref_key_notification_schedule", false)
                            .apply();
                }
                Utils.logError("Checksum: "+Utils.getDeviceChecksum());
                ignoreSlideChange = true;
                getPager().setCurrentItem(VERIFICATION_SLIDE + 1);
                ImageButton nextButton = findViewById(R.id.next);
                nextButton.setOnClickListener(v -> addLoginToDatabase());
                slides.getObjectAt(VERIFICATION_SLIDE).getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
        }
    }

    private void cancel(final AbstractOrderedFragment oldFragment) {
        new Handler().postDelayed(() -> {
            ignoreSlideChange = true;
            getPager().setCurrentItem(oldFragment.getPosition());
        }, 1);
    }

    private void addLoginToDatabase() {
        if (running)
            return;

        running = true;

        View v = slides.getObjectAt(VERIFICATION_SLIDE + 1).getView();
        EditText deviceName = v.findViewById(R.id.editText1);
        String enteredIdentifier = deviceName.getText().toString();

        if (enteredIdentifier.length() <= 1) {
            GraphicUtils.sendToast(R.string.toast_devicename_invalid);
            running = false;
            return;
        }

        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_cur_device", enteredIdentifier)
                .apply();

        RegistrationTask task = new RegistrationTask();
        task.addListener(params -> {

            ResponseCode response = (ResponseCode) params[0];

            switch (response) {
                case NO_CONNECTION:
                    running = false;
                    v.findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                    GraphicUtils.sendToast(getString(R.string.snackbar_no_connection_info));
                    break;
                case AUTH_FAILED:
                case NOT_SENT:
                case SERVER_FAILED:
                    running = false;
                    v.findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                    GraphicUtils.sendToast(getString(R.string.error_later));
                    break;
                case SUCCESS:
                    new SyncUserTask().addListener(param -> onSynchronisationProcessed((ResponseCode) param[0], v));
            }

        });
        task.execute();
    }

    private void verifyLoginData() {
        if (running)
            return;

        running = true;

        View v = slides.getObjectAt(VERIFICATION_SLIDE).getView();

        EditText name = v.findViewById(R.id.editText1);
        EditText password = v.findViewById(R.id.editText2);

        String userName = name.getText().toString();
        String userPassword = password.getText().toString();

        if (userName.length() == 0 || userPassword.length() == 0) {
            GraphicUtils.sendToast(R.string.data_differs);
            running = false;
            return;
        }

        v.findViewById(R.id.progressBarVerification).setVisibility(View.VISIBLE);

        Utils.setUserDefaultName(userName);
        Utils.setUserPassword(userPassword);

        VerificationTask task = new VerificationTask();
        task.addListener(params -> onVerificationProcessed((ResponseCode) params[0]));
        task.execute();
    }
}
