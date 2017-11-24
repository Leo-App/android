package de.slg.leoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.slg.leoapp.dialog.EditTextDialog;
import de.slg.leoapp.sqlite.SQLiteConnectorNews;
import de.slg.leoapp.task.UpdateTaskName;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.umfragen.SurveyActivity;

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
public class ProfileActivity extends LeoAppFeatureActivity {
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
        return R.id.drawer;
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
        TextView nameProfil        = (TextView) findViewById(R.id.nameProfil);
        TextView defaultNameProfil = (TextView) findViewById(R.id.defaultName);
        TextView stufeProfil       = (TextView) findViewById(R.id.stufeProfil);
        TextView survey            = (TextView) findViewById(R.id.surveyActual);

        nameProfil.setText(Utils.getUserName());
        defaultNameProfil.setText(Utils.getUserDefaultName());
        stufeProfil.setText(Utils.getUserStufe());
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER) {
            stufeProfil.setText("-");
            findViewById(R.id.card_viewTEA).setVisibility(View.VISIBLE);
            findViewById(R.id.editTEA).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog =
                            new EditTextDialog(ProfileActivity.this,
                                    getString(R.string.dialog_change_abbr),
                                    getString(R.string.settings_title_kuerzel),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Utils.getController().getPreferences().edit()
                                                    .putString("pref_key_kuerzel_general", dialog.getTextInput())
                                                    .apply();
                                            initProfil();
                                            initNavigationDrawer();
                                            dialog.dismiss();
                                        }
                                    });

                    dialog.show();

                    dialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                    dialog.setTextInput(Utils.getLehrerKuerzel());
                }
            });
        }

        setProfilePicture();

        findViewById(R.id.editProfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog =
                        new EditTextDialog(ProfileActivity.this,
                                getString(R.string.title_name_change),
                                getString(R.string.settings_title_nickname),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        UpdateTaskName task = new UpdateTaskName(Utils.getUserName());
                                        Utils.getController().getPreferences().edit()
                                                .putString("pref_key_general_name", dialog.getTextInput())
                                                .apply();
                                        task.execute();
                                        dialog.dismiss();
                                    }
                                });

                dialog.show();

                dialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                dialog.setTextInput(Utils.getUserName());
            }
        });

        String surveyTitle = getCurrentSurvey();
        if(surveyTitle == null) {
            findViewById(R.id.toSurvey).setVisibility(View.GONE);
            surveyTitle = "-";
        }

        survey.setText(surveyTitle);
        findViewById(R.id.toSurvey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SurveyActivity.class);
                intent.putExtra("redirect", true);
                startActivity(intent);
            }
        });

    }

    private void setProfilePicture() {
        final ImageView profilePic     = (ImageView) findViewById(R.id.profilePic);
        final int       res            = de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource();

        profilePic.setImageResource(res);
        switch (res) {
            case R.drawable.ic_sentiment_very_satisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorVerySatisfied), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_sentiment_satisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorSatisfied), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_sentiment_neutral_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorNeutral), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_sentiment_dissatisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorDissatisfied), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_sentiment_very_dissatisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorBadMood), PorterDuff.Mode.MULTIPLY);
                break;
            case R.drawable.ic_account_circle_black_24dp:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                break;
        }
    }

    private String getCurrentSurvey() {
        SQLiteConnectorNews dbh = new SQLiteConnectorNews(getApplicationContext());
        SQLiteDatabase      db  = dbh.getReadableDatabase();

        Cursor c = db.query(SQLiteConnectorNews.TABLE_SURVEYS, new String[]{SQLiteConnectorNews.SURVEYS_TITEL}, SQLiteConnectorNews.SURVEYS_REMOTE_ID + " = " + Utils.getUserID(), null, null, null, null);

        c.moveToFirst();
        String returnS = c.getCount() == 0 ? null : c.getString(0);


        c.close();
        db.close();
        dbh.close();

        return returnS;
    }

    public View getCoordinatorLayout() {
        return findViewById(R.id.coordinator);
    }

}