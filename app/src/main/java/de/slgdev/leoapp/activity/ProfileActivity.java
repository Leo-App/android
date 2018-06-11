package de.slgdev.leoapp.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.dialog.EditTextDialog;
import de.slgdev.leoapp.sqlite.SQLiteConnectorUmfragen;
import de.slgdev.leoapp.task.UpdateNameTask;
import de.slgdev.leoapp.utility.GraphicUtils;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.stimmungsbarometer.utility.StimmungsbarometerUtils;
import de.slgdev.umfragen.activity.SurveyActivity;

/**
 * ProfileActivity.
 * <p>
 * Diese Activity soll dem User einen Gesamtüberblick über sein Profil verschaffen. Angezeigt werden der Nickname (änderbar), optional ein Lehrerkürzel (änderbar), seine
 * Stufe, ggf. die aktuelle Stufe, seine gewählte Stimmung (beziehungsweise in Zukunft Meinung zu laufender Langzeitumfrage), seine LKs und schließlich ggf. seine
 * offene Umfrage. In Zukunft lässt sich hier auch die Funktionalität eines änderbaren Profilbilds implementieren.
 *
 * @author Luzia, Moritz
 * @version 2017.1311
 * @since 0.5.8
 */

public class ProfileActivity extends LeoAppNavigationActivity {
    private EditTextDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerProfileActivity(this);
        initProfil();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_profile;
    }

    @Override
    protected int getDrawerLayoutId() {
        return R.id.drawerLayout;
    }

    @Override
    protected int getNavigationId() {
        return R.id.navigationView;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int getToolbarTextId() {
        return R.string.profil;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.profile;
    }

    @Override
    protected String getActivityTag() {
        return "ProfileActivity";
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerProfileActivity(null);
    }

    @Override
    public void initNavigationDrawer() {
        super.initNavigationDrawer();
    }

    public void initProfil() {
        TextView       nameProfil        = findViewById(R.id.nameProfil);
        TextView       defaultNameProfil = findViewById(R.id.defaultName);
        TextView       stufeProfil       = findViewById(R.id.stufeProfil);
        TextView       survey            = findViewById(R.id.surveyActual);
        final TextView kuerzel           = findViewById(R.id.teaProfil);

        nameProfil.setText(Utils.getUserName());
        defaultNameProfil.setText(Utils.getUserDefaultName());
        stufeProfil.setText(Utils.getUserStufe());
        kuerzel.setText(Utils.getLehrerKuerzel());
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
            stufeProfil.setText(R.string.profile_level_teacher);
            findViewById(R.id.cardViewLehrer).setVisibility(View.VISIBLE);
            findViewById(R.id.editTEA).setOnClickListener(v -> {
                dialog =
                        new EditTextDialog(ProfileActivity.this,
                                getString(R.string.dialog_change_abbr),
                                getString(R.string.settings_title_kuerzel),
                                v1 -> {
                                    Utils.getController().getPreferences().edit()
                                            .putString("pref_key_kuerzel_general", dialog.getTextInput())
                                            .apply();
                                    initProfil();
                                    initNavigationDrawer();
                                    kuerzel.setText(Utils.getLehrerKuerzel());
                                    dialog.dismiss();
                                });

                dialog.show();

                dialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                dialog.setTextInput(Utils.getLehrerKuerzel());
            });
        }

        setProfilePicture();

        findViewById(R.id.editProfil).setOnClickListener(v -> {
            dialog =
                    new EditTextDialog(ProfileActivity.this,
                            getString(R.string.title_name_change),
                            getString(R.string.settings_title_nickname),
                            v12 -> {
                                String name = dialog.getTextInput();
                                UpdateNameTask task = new UpdateNameTask(Utils.getUserName());

                                if (name.length() > 20) {
                                    GraphicUtils.sendToast(R.string.name_too_long);
                                } else {
                                    Utils.getController().getPreferences().edit()
                                            .putString("pref_key_general_name", name)
                                            .apply();

                                    task.execute();
                                }

                                dialog.dismiss();
                            });

            dialog.show();

            dialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            dialog.setTextInput(Utils.getUserName());
        });

        String surveyTitle = getCurrentSurvey();
        if (surveyTitle == null) {
            findViewById(R.id.toSurvey).setVisibility(View.GONE);
            surveyTitle = "-";
        }

        survey.setText(surveyTitle);
        findViewById(R.id.toSurvey).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SurveyActivity.class);
            intent.putExtra("redirect", true);
            startActivity(intent);
            finish();
        });
    }

    private void setProfilePicture() {
        final ImageView profilePic = findViewById(R.id.profilePic);
        final int       res        = StimmungsbarometerUtils.getCurrentMoodRessource();

        profilePic.setImageResource(res);
        switch (res) {
            case R.drawable.ic_smiley_1:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorVerySatisfied), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_smiley_2:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorSatisfied), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_smiley_3:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorNeutral), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_smiley_4:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorDissatisfied), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_smiley_5:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorBadMood), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_profile:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

    private String getCurrentSurvey() {
        SQLiteConnectorUmfragen dbh    = new SQLiteConnectorUmfragen(getApplicationContext());
        String                  survey = dbh.getSurveyWithId(Utils.getUserID());
        dbh.close();
        return survey;
    }
}