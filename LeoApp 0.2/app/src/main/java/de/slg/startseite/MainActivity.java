package de.slg.startseite;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.NotificationService;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.stimmungsbarometer.AbstimmDialog;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;

public class MainActivity extends AppCompatActivity {
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
        initButtons();

        if (!EssensQRActivity.mensaModeRunning && Utils.getController().getPreferences().getBoolean("pref_key_mensa_mode", false)) {
            startActivity(new Intent(getApplicationContext(), EssensQRActivity.class));
        } else {
            EssensQRActivity.mensaModeRunning = false;
        }
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (editing)
                onBackPressed();
            else
                drawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.action_appedit) {
            editing = true;

            initFeatureCards();

            findViewById(R.id.card_viewMain).setVisibility(View.GONE);
            findViewById(R.id.card_view0).setVisibility(View.GONE);

            getSupportActionBar().setTitle(getString(R.string.cards_customize));
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.action_appedit_done) {
            writeCardsToPreferences();

            onBackPressed();
        } else if (item.getItemId() == R.id.action_appinfo_quick) {
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
        } else if (item.getItemId() == R.id.action_appedit_add) {
            new CardAddDialog(this).show();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (editing) {
            editing = false;

            initFeatureCards();

            findViewById(R.id.card_viewMain).setVisibility(View.VISIBLE);
            if (!Utils.getController().getPreferences().getBoolean("pref_key_dont_remind_me", false))
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
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());

        mAdapter.updateCustomCards();

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

    void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.getMenu().findItem(R.id.newsboard).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.messenger).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.klausurplan).setEnabled(Utils.isVerified());
        navigationView.getMenu().findItem(R.id.stundenplan).setEnabled(Utils.isVerified());

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
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
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
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(de.slg.stimmungsbarometer.Utils.getCurrentMoodRessource());
    }

    void initFeatureCards() {
        if (Utils.isVerified()) {
            findViewById(R.id.card_view0).setVisibility(View.GONE);
        }

        findViewById(R.id.buttonCardView0).setOnClickListener(new View.OnClickListener() {
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

    private void initButtons() {
        final ImageButton help = (ImageButton) findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri    webpage = Uri.parse("http://www.leoapp-slg.de");
                Intent intent  = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        final ImageButton feature = (ImageButton) findViewById(R.id.feature_request);
        feature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeatureDialog dialog = new FeatureDialog(MainActivity.this);
                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });
    }

    private void processIntent() {
        int notificationTarget = getIntent().getIntExtra("start_intent", -1);
        if (notificationTarget != -1) {
            Utils.getController().closeAll();

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

    public void showVerificationDialog() {
        VerificationDialog dialog = new VerificationDialog(this);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void addCard(CardType t) {

        mAdapter.addToList(t);
        mAdapter.notifyDataSetChanged();

        //TODO: Scroll to new Position

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