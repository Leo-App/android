package de.slg.startseite;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.IntroActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.ProfileActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.dialog.InformationDialog;
import de.slg.leoapp.service.NotificationService;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.stimmungsbarometer.AbstimmDialog;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
import de.slg.umfragen.SurveyActivity;

/**
 * MainActivity.
 * <p>
 * "Startseite" der LeoApp, hier wird das Hauptmenü angezeigt. Zur Verfügung stehen zwei Layouts: Listen- und Schnellansicht (Für Programmlogik siehe {@link CardAdapter}).
 * Zusätzlich wird Hintergrundlogik, wie das Weiterleiten von Notifications, das Anzeigen von Dialogen oder der Mensamode verwaltet.
 *
 * @author Gianni, Moritz
 * @version 2017.1111
 * @since 0.0.1
 */
public class MainActivity extends ActionLogActivity {
    public static boolean        editing;
    public        AbstimmDialog  abstimmDialog;
    private       NavigationView navigationView;
    private       DrawerLayout   drawerLayout;
    private       CardAdapter    mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        processIntent();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);
        Utils.getController().registerMainActivity(this);

        Utils.getController().setContext(getApplicationContext());

        initToolbar();
        initFeatureCards();
        initNavigationView();
        initAppIntro();

        initOptionalDialog();

        if (!EssensQRActivity.mensaModeRunning && Utils.getController().getPreferences().getBoolean("pref_key_mensa_mode", false)) {
            startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
        } else {
            EssensQRActivity.mensaModeRunning = false;
        }
    }

    @Override
    public String getActivityTag() {
        return "MainActivity";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (editing) {
            getMenuInflater().inflate(R.menu.startseite_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.startseite, menu);
            if (Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false))
                menu.findItem(R.id.action_appinfo_quick).setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
            else
                menu.findItem(R.id.action_appinfo_quick).setIcon(R.drawable.ic_widgets_white_24dp);
        }

        if (Utils.isVerified()) {
            menu.removeItem(R.id.action_verify);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (editing)
                    onBackPressed();
                else
                    drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.action_appedit:
                editing = true;

                initFeatureCards();

                findViewById(R.id.card_viewMain).setVisibility(View.GONE);
                findViewById(R.id.card_view0).setVisibility(View.GONE);

                getSupportActionBar().setTitle(getString(R.string.cards_customize));
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

                invalidateOptionsMenu();
                break;

            case R.id.action_appedit_done:
                writeCardsToPreferences();
                onBackPressed();
                break;

            case R.id.action_appinfo_quick:
                writeCardsToPreferences();

                boolean b = Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false);

                Utils.getController().getPreferences().edit()
                        .putBoolean("pref_key_card_config_quick", !b)
                        .apply();

                initFeatureCards();

                if (!b)
                    item.setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
                else
                    item.setIcon(R.drawable.ic_widgets_white_24dp);
                break;

            case R.id.action_appedit_add:
                new CardAddDialog(this).show();
                break;

            case R.id.action_verify:
                showVerificationDialog();
                break;

            case R.id.action_request:
                FeatureDialog dialog = new FeatureDialog(MainActivity.this);
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                break;

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), PreferenceActivity.class));
                break;

            case R.id.action_help:
                Intent myIntent = new Intent(MainActivity.this, IntroActivity.class);
                MainActivity.this.startActivity(myIntent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (editing) {
            editing = false;

            initFeatureCards();

            findViewById(R.id.card_viewMain).setVisibility(View.VISIBLE);
            if (!Utils.getController().getPreferences().getBoolean("pref_key_dont_remind_me", false) && !Utils.isVerified())
                findViewById(R.id.card_view0).setVisibility(View.VISIBLE);

            getSupportActionBar().setTitle(getString(R.string.title_home));
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

            invalidateOptionsMenu();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.startseite).setChecked(true);

        if (abstimmDialog != null) {
            abstimmDialog.show();
        }

        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());

        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());

        //    mAdapter.updateCards();

        Utils.getNotificationManager().cancel(NotificationService.ID_BAROMETER);
        Utils.getNotificationManager().cancel(NotificationService.ID_STUNDENPLAN);
    }

    @Override
    protected void onPause() {
        if (abstimmDialog != null) {
            abstimmDialog.hide();
            super.onPause();
        } else {
            super.onPause();
        }
    }

    @Override
    public void finish() {
        if (abstimmDialog != null) {
            abstimmDialog.dismiss();
        }
        super.finish();
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.getMenu().findItem(R.id.newsboard).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.messenger).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.klausurplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.stundenplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.foodmarks).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.barometer).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.umfragen).setEnabled(Utils.isVerified());

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
                        i = new Intent(getApplicationContext(), KlausurplanActivity.class);
                        break;
                    case R.id.startseite:
                        return true;
                    case R.id.umfragen:
                        i = new Intent(getApplicationContext(), SurveyActivity.class);
                        break;
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
                        break;
                    case R.id.profile:
                        i = new Intent(getApplicationContext(), ProfileActivity.class);
                        break;
                    default:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                if (i != null)
                    startActivity(i);
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

    private void initFeatureCards() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.scrollView).scrollTo(0, 0);
            }
        }, 60);

        if (Utils.isVerified() || Utils.getController().getPreferences().getBoolean("pref_key_dont_remind_me", false)) {
            findViewById(R.id.card_view0).setVisibility(View.GONE);
        }

        findViewById(R.id.buttonCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isVerified())
                    showVerificationDialog();
                else {
                    Utils.getController().getPreferences()
                            .edit()
                            .putBoolean("pref_key_dont_remind_me", true)
                            .apply();
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_fade_out);
                    findViewById(R.id.card_view0).startAnimation(anim);
                    final Handler handler = new Handler(); //Remove card after animation
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.card_view0).setVisibility(View.GONE);
                        }
                    }, 310);
                }
            }
        });
        findViewById(R.id.buttonDismissCardView0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.getController().getPreferences()
                        .edit()
                        .putBoolean("pref_key_dont_remind_me", true)
                        .apply();
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_fade_out);
                findViewById(R.id.card_view0).startAnimation(anim);
                final Handler handler = new Handler(); //Remove card after animation
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.card_view0).setVisibility(View.GONE);
                    }
                }, 310);
            }
        });

        TextView version = (TextView) findViewById(R.id.versioncode_maincard);
        version.setText(Utils.getAppVersionName());

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCards);
        mAdapter = new CardAdapter();

        final boolean quickLayout = Utils.getController().getPreferences().getBoolean("pref_key_card_config_quick", false);

        ItemTouchHelper.Callback simpleItemTouchCallback = new ItemTouchHelper.Callback() {

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!editing)
                    return 0;

                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        if (quickLayout) {
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            mRecyclerView.setLayoutManager(layoutManager);
        }

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(getString(R.string.title_home));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initAppIntro() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean           isFirst  = getPrefs.getBoolean("first", true);

                if (isFirst) {
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                        }
                    });
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("first", false);
                    e.apply();
                }
            }
        });
        t.start();
    }

    private void initOptionalDialog() {
        new InformationDialog(this).setText(R.string.dialog_betatest).show();
    }

    private void processIntent() {
        int notificationTarget = getIntent().getIntExtra("start_intent", -1);
        if (notificationTarget != -1) {
            Utils.getController().closeActivities();

            switch (notificationTarget) {
                case NotificationService.ID_ESSENSQR:
                    startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
                    break;

                case NotificationService.ID_KLAUSURPLAN:
                    startActivity(new Intent(getApplicationContext(), KlausurplanActivity.class));
                    break;

                case NotificationService.ID_MESSENGER:
                    startActivity(new Intent(getApplicationContext(), MessengerActivity.class));
                    break;

                case NotificationService.ID_NEWS:
                    startActivity(new Intent(getApplicationContext(), SchwarzesBrettActivity.class));
                    break;
            }
        }

        if (getIntent().getBooleanExtra("show_dialog", false)) {
            abstimmDialog = new AbstimmDialog(this);
            abstimmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
                    mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            abstimmDialog = null;
                        }
                    }, 100);
                }
            });
        }
    }

    void showVerificationDialog() {
        VerificationDialog dialog = new VerificationDialog(this);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
    }

    void addCard(CardType t) {
        mAdapter.addToList(t);
        mAdapter.notifyDataSetChanged();
    }

    private void writeCardsToPreferences() {
        StringBuilder b = new StringBuilder("");
        if (mAdapter.cards.size() > 0) {
            for (mAdapter.cards.toFirst(); mAdapter.cards.hasAccess(); mAdapter.cards.next()) {
                if (b.length() > 0)
                    b.append(";");
                b.append(mAdapter.cards.getContent().type.toString());
            }
        } else {
            b.append("");
        }

        Utils.getController().getPreferences()
                .edit()
                .putString("pref_key_card_config", b.toString())
                .apply();
    }
}