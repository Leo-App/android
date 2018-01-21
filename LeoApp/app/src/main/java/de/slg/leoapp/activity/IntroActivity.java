package de.slg.leoapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.github.paolorotolo.appintro.AppIntro2;

import de.slg.leoapp.R;
import de.slg.leoapp.Start;
import de.slg.leoapp.activity.fragment.AbstractOrderedFragment;
import de.slg.leoapp.activity.fragment.InfoFragmentBuilder;
import de.slg.leoapp.activity.fragment.VerificationFragment;
import de.slg.leoapp.task.RegistrationTask;
import de.slg.leoapp.task.SyncFilesTask;
import de.slg.leoapp.task.SyncGradeTask;
import de.slg.leoapp.task.SyncQuestionTask;
import de.slg.leoapp.task.SyncUserTask;
import de.slg.leoapp.task.SyncVoteTask;
import de.slg.leoapp.utility.GraphicUtils;
import de.slg.leoapp.utility.ResponseCode;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.utility.VerificationListener;

/**
 * IntroActivity.
 * <p>
 * Diese Activity verschafft dem User bei erstmaligem Starten einen kurzen Überblick über die App. Kann bei Bedarf erweitert werden.
 *
 * @author Gianni
 * @version 2017.2312
 * @since 0.5.7
 */

public class IntroActivity extends AppIntro2 implements VerificationListener {

    private static final int VERIFICATION_SLIDE = 5;
    private static boolean running;
    private static boolean ignoreSlideChange;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = false;

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

        if (getIntent().getExtras() == null) {

            //Verifiation-Slide
            addSlide(
                    VerificationFragment.newInstance(R.string.verification_title,
                            R.string.verification_desc,
                            R.color.introSlide1,
                            VERIFICATION_SLIDE)
            );

            //Success-Slide
            addSlide(
                    new InfoFragmentBuilder()
                            .setTitle(R.string.intro_finished_title)
                            .setContent(R.string.intro_finished_desc)
                            .setImage(R.drawable.intro_finished)
                            .setPosition(VERIFICATION_SLIDE + 1)
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

        if (oldFragment.getPosition() < newFragment.getPosition() && oldFragment.getPosition() == VERIFICATION_SLIDE) {
            cancel(oldFragment);
        } else if (oldFragment.getPosition() > newFragment.getPosition() && newFragment.getPosition() == VERIFICATION_SLIDE) {
            cancel(oldFragment);
        } else if (newFragment.getPosition() == VERIFICATION_SLIDE) {
            ImageButton nextButton = findViewById(R.id.next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVerification(newFragment);
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
    public void onSynchronisationProcessed(ResponseCode response, Fragment fragment) {
        switch (response) {
            case NO_CONNECTION:
            case AUTH_FAILED:
            case SERVER_FAILED:
                GraphicUtils.sendToast(getString(R.string.error_later));
                running = false;
                break;
            case SUCCESS:
                ignoreSlideChange = true;
                getPager().setCurrentItem(VERIFICATION_SLIDE + 1);
                fragment.getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                running = false;
                break;
        }
    }

    @Override
    public void onVerificationProcessed(ResponseCode response, Fragment fragment) {

        switch (response) {
            case NO_CONNECTION:
                GraphicUtils.sendToast(R.string.snackbar_no_connection_info);
                running = false;
                fragment.getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                break;
            case AUTH_FAILED:
                GraphicUtils.sendToast(getString(R.string.data_differs));
                fragment.getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                running = false;
                break;
            case SERVER_FAILED:
                GraphicUtils.sendToast(getString(R.string.error_later));
                fragment.getView().findViewById(R.id.progressBarVerification).setVisibility(View.INVISIBLE);
                running = false;

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
                } else {
                    new SyncGradeTask().execute();
                }
                new SyncUserTask(fragment).registerListener(this).execute();
                new SyncVoteTask().execute();
                new SyncFilesTask().execute();
                new SyncQuestionTask().execute();
                Start.startReceiveService();

                break;
        }
    }

    private void cancel(final AbstractOrderedFragment oldFragment) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ignoreSlideChange = true;
                getPager().setCurrentItem(oldFragment.getPosition());
            }
        }, 1);
    }

    private void startVerification(AbstractOrderedFragment oldFragment) {

        if (running)
            return;

        running = true;

        View v = oldFragment.getView();

        EditText name     = v.findViewById(R.id.editText1);
        EditText password = v.findViewById(R.id.editText2);

        String userName     = name.getText().toString();
        String userPassword = password.getText().toString();

        if (userName.length() == 0 || userPassword.length() == 0) {
            GraphicUtils.sendToast(R.string.data_differs);
            running = false;
            return;
        }

        v.findViewById(R.id.progressBarVerification).setVisibility(View.VISIBLE);

        Utils.setUserDefaultName(userName);
        Utils.setUserPassword(userPassword);

        RegistrationTask task = new RegistrationTask();
        task.registerListener(this);
        task.execute(oldFragment);
    }
}
