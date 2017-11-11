package de.slg.leoapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import de.slg.essensqr.EssensQRActivity;
import de.slg.leoview.ActionLogActivity;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.Fach;
import de.slg.stundenplan.StundenplanActivity;

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

        nameProfil.setText(Utils.getUserName());
        defaultNameProfil.setText(Utils.getUserDefaultName());
        stufeProfil.setText(Utils.getUserStufe());
        if (Utils.getUserPermission() == 2) {
            TextView stufeTitel = (TextView) findViewById(R.id.textView12);
            stufeTitel.setText("Kürzel");
            stufeProfil.setText(Utils.getLehrerKuerzel());
            findViewById(R.id.editKuerzel).setVisibility(View.VISIBLE);
            findViewById(R.id.editKuerzel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog =
                            new EditTextDialog(ProfileActivity.this,
                                    getString(R.string.settings_title_kuerzel),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Utils.getController().getPreferences().edit()
                                                    .putString("pref_key_kuerzel_general", dialog.editText.getText().toString())
                                                    .apply();
                                            initProfil();
                                            initNavigationView();
                                            dialog.dismiss();
                                        }
                                    });

                    dialog.show();
                    dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                    dialog.editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                }
            });
        }

        if(Utils.getUserPermission()!=2 ) {
            if(Utils.getUserStufe().equals("Q1") || Utils.getUserStufe().equals("Q2")) {
                TextView lk1 = (TextView) findViewById(R.id.lk1);
                TextView lk2 = (TextView) findViewById(R.id.lk2);

                String[] lks = lkNamen();
                String l1 = lks[0];
                String l2 = "";
                for(int i = 1; i < lks.length; i++) {
                    if(!lks[i].equals(l1)) {
                        l2 = lks[i];
                        Log.e("LK2",lks[i]);
                        break;
                    }
                }
                lk1.setText(l1.split(" ")[0]);
                lk2.setText(l2.split(" ")[0]);
            } else {
                findViewById(R.id.cardViewLK).setVisibility(View.GONE);
            }

        }

        setzeProfilBild();

        findViewById(R.id.editName).setOnClickListener(new View.OnClickListener() {
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
                                                .putString("pref_key_general_name", dialog.editText.getText().toString())
                                                .apply();
                                        task.execute();
                                        dialog.dismiss();
                                    }
                                });

                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });
    }

    private String[] lkNamen() {
        String[] lks  = new String[10];
        int g = 0;

        Fach[][] gewaehlteFaecher = new Fach[5][];
        for (int i = 0; i < gewaehlteFaecher.length; i++) {
            gewaehlteFaecher[i] = de.slg.leoapp.Utils.getController().getStundenplanDatabase().gewaehlteFaecherAnTag(i + 1);
        }

        for (int i = 0; i< gewaehlteFaecher.length; i++) {
            Fach[] f = gewaehlteFaecher[i];
            for (int a = 0; a < f.length; a++) {
                if(f[a].getName().contains("LK")){
                    lks[g]=f[a].getName();
                    g++;
                }
            }
        }
        return lks;
    }

    private void setzeProfilBild() {
        final ImageView profilePic     = (ImageView) findViewById(R.id.profPic);
        final TextView  stimmungProfil = (TextView) findViewById(R.id.stimmungProfil);
        final int       res            = de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource();

        profilePic.setImageResource(res);
        switch (res) {
            case R.drawable.ic_sentiment_very_satisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorVerySatisfied), PorterDuff.Mode.MULTIPLY);
                stimmungProfil.setText(R.string.sg);
                break;
            case R.drawable.ic_sentiment_satisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorSatisfied), PorterDuff.Mode.MULTIPLY);
                stimmungProfil.setText(R.string.g);
                break;
            case R.drawable.ic_sentiment_neutral_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorNeutral), PorterDuff.Mode.MULTIPLY);
                stimmungProfil.setText(R.string.m);
                break;
            case R.drawable.ic_sentiment_dissatisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorDissatisfied), PorterDuff.Mode.MULTIPLY);
                stimmungProfil.setText(R.string.s);
                break;
            case R.drawable.ic_sentiment_very_dissatisfied_white_24px:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorBadMood), PorterDuff.Mode.MULTIPLY);
                stimmungProfil.setText(R.string.ss);
                break;
            case R.drawable.ic_account_circle_black_24dp:
                profilePic.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                stimmungProfil.setText("N/A");
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

    void initNavigationView() {
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
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());
        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());
    }

    View getCoordinatorLayout() {
        return findViewById(R.id.coordinator);
    }
}