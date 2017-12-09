package de.slg.startseite.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import de.slg.essensbons.activity.EssensQRActivity;
import de.slg.klausurplan.activity.KlausurplanActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.activity.IntroActivity;
import de.slg.leoapp.activity.PreferenceActivity;
import de.slg.leoapp.dialog.ChangelogDialog;
import de.slg.leoapp.dialog.EditTextDialog;
import de.slg.leoapp.dialog.InformationDialog;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.task.MailSendTask;
import de.slg.leoapp.utility.User;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.messenger.activity.MessengerActivity;
import de.slg.schwarzes_brett.activity.SchwarzesBrettActivity;
import de.slg.startseite.CardAdapter;
import de.slg.startseite.CardType;
import de.slg.startseite.dialog.CardAddDialog;
import de.slg.startseite.dialog.VerificationDialog;
import de.slg.stimmungsbarometer.dialog.AbstimmDialog;
import de.slg.umfragen.activity.SurveyActivity;

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
public class MainActivity extends LeoAppFeatureActivity {
    public static boolean        editing;
    public        AbstimmDialog  abstimmDialog;
    private       CardAdapter    mAdapter;
    private       EditTextDialog featureRequestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        processIntent();
        super.onCreate(savedInstanceState);

        Utils.getController().registerMainActivity(this);
        Utils.getController().setContext(getApplicationContext());

        initFeatureCards();
        initIntroduction();
        initOptionalDialog();

        if (!EssensQRActivity.mensaModeRunning && Utils.getController().getPreferences().getBoolean("pref_key_mensa_mode", false)) {
            startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
        } else {
            EssensQRActivity.mensaModeRunning = false;
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_startseite;
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
        return R.string.title_home;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.startseite;
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
                    getDrawerLayout().openDrawer(GravityCompat.START);
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
                featureRequestDialog = new EditTextDialog(this,
                            getString(R.string.feature_request_title),
                            getString(R.string.feature_request),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String emailText = featureRequestDialog.getTextInput();
                                    new MailSendTask().execute(emailText);
                                    featureRequestDialog.dismiss();
                                    Toast.makeText(Utils.getContext(), Utils.getString(R.string.thank_you_feature), Toast.LENGTH_SHORT).show();
                                }
                            });

                featureRequestDialog.show();
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
        getNavigationView().getMenu().findItem(R.id.startseite).setChecked(true);

        if (abstimmDialog != null) {
            abstimmDialog.show();
        }

        TextView username = (TextView) getNavigationView().getHeaderView(0).findViewById(R.id.username);
        username.setText(Utils.getUserName());

        TextView grade = (TextView) getNavigationView().getHeaderView(0).findViewById(R.id.grade);
        if (Utils.getUserPermission() == User.PERMISSION_LEHRER)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) getNavigationView().getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.utility.Utils.getCurrentMoodRessource());

        Utils.getNotificationManager().cancel(NotificationHandler.ID_BAROMETER);
        Utils.getNotificationManager().cancel(NotificationHandler.ID_STUNDENPLAN);
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

    private void initIntroduction() {
        String prevVersion = Utils.getController().getPreferences().getString("previousVersion", "");
        if (prevVersion.equals("")) {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
        } else if (!prevVersion.equals(Utils.getAppVersionName())) {
            new ChangelogDialog(this).show();
        }
    }

    private void initOptionalDialog() {
        new InformationDialog(this).setText(R.string.dialog_betatest).show();
    }

    private void processIntent() {
        int notificationTarget = getIntent().getIntExtra("start_intent", -1);
        if (notificationTarget != -1) {
            Utils.getController().closeActivities();

            switch (notificationTarget) {
                case NotificationHandler.ID_ESSENSQR:
                    startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
                    break;

                case NotificationHandler.ID_KLAUSURPLAN:
                    startActivity(new Intent(getApplicationContext(), KlausurplanActivity.class));
                    break;

                case NotificationHandler.ID_MESSENGER:
                    startActivity(new Intent(getApplicationContext(), MessengerActivity.class));
                    break;

                case NotificationHandler.ID_NEWS:
                    startActivity(new Intent(getApplicationContext(), SchwarzesBrettActivity.class));
                    break;

                case NotificationHandler.ID_SURVEY:
                    startActivity(new Intent(getApplicationContext(), SurveyActivity.class));
                    break;
            }
        }
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

    public void showVerificationDialog() {
        VerificationDialog dialog = new VerificationDialog(this);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setCancelable(false);
    }

    public void addCard(CardType t) {
        mAdapter.addToList(t);
        mAdapter.notifyDataSetChanged();
    }

    public void notifyVote() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                abstimmDialog = new AbstimmDialog(MainActivity.this);
                abstimmDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ImageView mood = (ImageView) getNavigationView().getHeaderView(0).findViewById(R.id.profile_image);
                        mood.setImageResource(de.slg.stimmungsbarometer.utility.Utils.getCurrentMoodRessource());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                abstimmDialog = null;
                            }
                        }, 100);
                    }
                });
                abstimmDialog.show();
            }
        });
    }
}