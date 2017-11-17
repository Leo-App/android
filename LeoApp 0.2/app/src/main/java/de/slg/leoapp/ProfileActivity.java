package de.slg.leoapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.slg.essensqr.EssensQRActivity;
import de.slg.leoapp.dialog.EditTextDialog;
import de.slg.leoapp.sqlite.SQLiteConnectorNews;
import de.slg.leoapp.task.UpdateTaskName;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
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
public class ProfileActivity extends ActionLogActivity {
    private DrawerLayout   drawerLayout;
    private EditTextDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Utils.getController().registerProfileActivity(this);

        initToolbar();
        initNavigationView();
        initProfil();
    }

    @Override
    protected String getActivityTag() {
        return "ProfileActivity";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerProfileActivity(null);
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
                                    getString(R.string.settings_title_kuerzel),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Utils.getController().getPreferences().edit()
                                                    .putString("pref_key_kuerzel_general", dialog.getTextInput())
                                                    .apply();
                                            initProfil();
                                            initNavigationView();
                                            dialog.dismiss();
                                        }
                                    });

                    dialog.show();
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    dialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                }
            });
        }

        setProfilePicture();

        findViewById(R.id.editProfil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog =
                        new EditTextDialog(ProfileActivity.this,
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
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                dialog.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }
        });

        survey.setText(getCurrentSurvey());
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(R.string.profil);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private String getCurrentSurvey() {
        SQLiteConnectorNews dbh = new SQLiteConnectorNews(getApplicationContext());
        SQLiteDatabase      db  = dbh.getReadableDatabase();

        Cursor c = db.query(SQLiteConnectorNews.TABLE_SURVEYS, new String[]{SQLiteConnectorNews.SURVEYS_TITEL}, SQLiteConnectorNews.SURVEYS_REMOTE_ID + " = " + Utils.getUserID(), null, null, null, null);

        c.moveToFirst();
        String returnS = c.getCount() == 0 ? "-" : c.getString(0);


        c.close();
        db.close();
        dbh.close();

        return returnS;
    }

    public void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.getMenu().findItem(R.id.newsboard).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.messenger).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.klausurplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.stundenplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.foodmarks).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.barometer).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.umfragen).setEnabled(Utils.isVerified());

        navigationView.getMenu().findItem(R.id.profile).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.foodmarks:
                        i = new Intent(getApplicationContext(), EssensQRActivity.class);
                        break;
                    case R.id.messenger:
                        i = new Intent(getApplicationContext(), MessengerActivity.class);
                        break;
                    case R.id.newsboard:
                        i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                        break;
                    case R.id.stundenplan:
                        i = new Intent(getApplicationContext(), StundenplanActivity.class);
                        break;
                    case R.id.barometer:
                        i = new Intent(getApplicationContext(), StimmungsbarometerActivity.class);
                        break;
                    case R.id.klausurplan:
                        return true;
                    case R.id.startseite:
                        i = null;
                        break;
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
                        break;
                    case R.id.profile:
                        return true;
                    default:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                if (i != null)
                    startActivity(i);
                finish();
                return true;
            }
        });
        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());
        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());
        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());
    }

    public View getCoordinatorLayout() {
        return findViewById(R.id.coordinator);
    }

}