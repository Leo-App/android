package de.slg.startseite;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.slg.essensqr.EssensQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.NotificationService;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;
import de.slg.messenger.MessengerActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.schwarzes_brett.UpdateViewTrackerTask;
import de.slg.stimmungsbarometer.AbstimmDialog;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.StundenplanActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ZXingScannerView.ResultHandler {
    public static View        cooredinatorLayout;
    public static ProgressBar progressBar;
    public static TextView    title, info;
    public static Button verify, dismiss;
    public static boolean editing;
    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0;
    private ZXingScannerView scV;
    private boolean          runningScan;

    private NavigationView navigationView;
    private DrawerLayout   drawerLayout;
    private CardAdapter    mAdapter;

    private AbstimmDialog abstimmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);
        Utils.registerMainActivity(this);

        Utils.context = getApplicationContext();

        int notificationTarget = getIntent().getIntExtra("start_intent", -1);
        if (notificationTarget != -1) {
            Utils.closeAll();

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
                    mood.setImageResource(Utils.getCurrentMoodRessource());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            abstimmDialog = null;
                        }
                    }, 100);
                }
            });
        }

        if (getIntent().getIntExtra("days", 15) <= 14) {
            showVerificationDialog();
            //TODO neu verifizieren dialog!!!
        }

        //Schwarzes Brett: ViewTracker-Synchronization
        ArrayList<Integer> cachedViews = Utils.getCachedIDs();
        new UpdateViewTrackerTask().execute(cachedViews.toArray(new Integer[cachedViews.size()]));

        title = (TextView) findViewById(R.id.info_title0);
        info = (TextView) findViewById(R.id.info_text0);
        verify = (Button) findViewById(R.id.buttonCardView0);
        dismiss = (Button) findViewById(R.id.buttonDismissCardView0);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        cooredinatorLayout = findViewById(R.id.coordinator);

        runningScan = false;

        initToolbar();
        initFeatureCards();
        initNavigationView();
        initButtons();

        new SyncTaskName().execute();
        new SyncTaskGrade().execute();

        if (!Utils.getPreferences().getString("pref_key_request_cached", "-").equals("-")) {
            new MailSendTask().execute(Utils.getPreferences().getString("pref_key_request_cached", ""));
        }

        if (Utils.getPreferences().getBoolean("pref_key_level_has_to_be_synchronized", false)) {
            new UpdateTaskGrade(getApplicationContext()).execute();
        }

        if (Utils.isVerified()) {
            MainActivity.dismiss.setVisibility(View.GONE);
            title.setTextColor(Color.GREEN);
            title.setText(getString(R.string.title_info_auth));
            info.setText(getString(R.string.summary_info_auth_success));
            verify.setText(getString(R.string.button_info_noreminder));
        }

        if (Utils.getPreferences().getBoolean("pref_key_dont_remind_me", false)) {
            findViewById(R.id.card_view0).setVisibility(View.GONE);
        }

        if (!EssensQRActivity.mensaModeRunning && Utils.getPreferences().getBoolean("pref_key_mensa_mode", false)) {
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
            if (Utils.getPreferences().getBoolean("pref_key_card_config_quick", false))
                menu.findItem(R.id.action_appinfo_quick).setIcon(R.drawable.ic_format_list_bulleted_white_24dp);
            else
                menu.findItem(R.id.action_appinfo_quick).setIcon(R.drawable.ic_widgets_white_24dp);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.action_appedit) {
            editing = true;
            initFeatureCards();
            //           findViewById(R.id.card_viewMain).setVisibility(View.GONE);
            findViewById(R.id.card_view0).setVisibility(View.GONE);
            final Handler handler = new Handler(); //Short delay for aesthetics
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    invalidateOptionsMenu();
                }
            }, 100);
            getSupportActionBar().setTitle(getString(R.string.cards_customize));
        } else if (item.getItemId() == R.id.action_appedit_done) {
            editing = false;
            writeCardsToPreferences();
            initFeatureCards();
            //            findViewById(R.id.card_viewMain).setVisibility(View.VISIBLE);
            if (!Utils.getPreferences().getBoolean("pref_key_dont_remind_me", false))
                findViewById(R.id.card_view0).setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getString(R.string.title_home));
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.action_appinfo_quick) {
            writeCardsToPreferences();
            boolean b = Utils.getPreferences().getBoolean("pref_key_card_config_quick", false);
            Utils.getPreferences().edit()
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
            //           findViewById(R.id.card_viewMain).setVisibility(View.VISIBLE);
            if (!Utils.getPreferences().getBoolean("pref_key_dont_remind_me", false))
                findViewById(R.id.card_view0).setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(getString(R.string.title_home));
            invalidateOptionsMenu();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (runningScan) {
                runningScan = false;
                scV.stopCamera();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return false;
            }
            return super.onKeyDown(keyCode, event);
        } else
            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.startseite).setChecked(true);

        if (abstimmDialog != null)
            abstimmDialog.show();

        if (!runningScan) {

            TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
            username.setText(Utils.getUserName());

            TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
            if (Utils.getUserPermission() == 2)
                grade.setText(Utils.getLehrerKuerzel());
            else
                grade.setText(Utils.getUserStufe());

            ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
            mood.setImageResource(Utils.getCurrentMoodRessource());

            //            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            //           scrollView.smoothScrollTo(0, 0);

            mAdapter.updateCustomCards();
        }

        Utils.getNotificationManager().cancel(NotificationService.ID_BAROMETER);
        Utils.getNotificationManager().cancel(NotificationService.ID_STUNDENPLAN);
    }

    @Override
    protected void onPause() {
        if (scV != null && scV.isActivated()) {
            scV.stopCamera();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else if (abstimmDialog != null) {
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonCardView0) {
            if (!Utils.isVerified())
                showVerificationDialog();
            else {
                Utils.getPreferences().edit()
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
        } else {
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

    @Override
    public void handleResult(Result result) {
        runningScan = false;
        scV.stopCamera();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        String results = result.getText();
        Log.d("LeoApp", results);
        Log.d("LeoApp", "checkCode");
        if (isValid(results)) {
            final String[] data = results.split("-");
            Log.d("LeoApp", "validCode");
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    verify.setVisibility(View.GONE);
                    dismiss.setVisibility(View.GONE);

                    RegistrationTask t = new RegistrationTask(MainActivity.this);
                    t.execute(data[0], String.valueOf(data[1]));
                }
            };
            handler.postDelayed(r, 100);
        } else {
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                @Override
                public void run() {
                    MainActivity.info.setText(getString(R.string.summary_info_auth_failed));
                    MainActivity.title.setText(getString(R.string.error));
                }
            };
            handler.postDelayed(r, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_USE_CAMERA &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
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
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    void initFeatureCards() {

        findViewById(R.id.buttonCardView0).setOnClickListener(this);
        findViewById(R.id.buttonDismissCardView0).setOnClickListener(this);

        //        TextView version = (TextView) findViewById(R.id.versioncode_maincard);
        //       version.setText(Utils.getAppVersionName());

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewCards);
        mAdapter = new CardAdapter();

        final boolean quickLayout = Utils.getPreferences().getBoolean("pref_key_card_config_quick", false);

        RecyclerView.LayoutManager mLayoutManager = quickLayout

                ?

                new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false) {
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                }

                :

                new LinearLayoutManager(getApplicationContext()) {
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                };

        ItemTouchHelper.Callback simpleItemTouchCallback = new ItemTouchHelper.Callback() {

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!editing)
                    return 0;

                int dragFlags  = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setLayoutManager(editing ? mLayoutManager : mLayoutManager);
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
 /*       final ImageButton help = (ImageButton) findViewById(R.id.help);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri    webpage = Uri.parse("http://www.leoapp-slg.de");
                Intent intent  = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

                // startActivity(new Intent(getApplicationContext(), TutorialActivity.class));
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
        }); */
    }

    public void showVerificationDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();

        View v = getLayoutInflater().inflate(R.layout.dialog_verification, null);
        v.findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        v.findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startCamera();
            }
        });
        dialog.setView(v);

        dialog.show();
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

        Utils.getPreferences().edit()
                .putString("pref_key_card_config", b.toString())
                .apply();
    }

    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LeoApp", "No permission. Checking");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            scV = new ZXingScannerView(getApplicationContext());
            setContentView(scV);
            scV.setResultHandler(this);
            scV.startCamera(0);

            runningScan = true;
        }
    }

    private boolean isValid(String s) {
        String[] parts = s.split("-");

        if (parts.length != 3)
            return false;
        Log.d("LeoApp", "passedLengthTest");

        int priority;
        int birthyear;
        if (parts[0].length() < 6)
            return false;
        Log.d("LeoApp", "passedUsernameLengthTest");

        try {
            priority = Integer.parseInt(parts[1]);
            Log.d("LeoApp", "passedPriorityNumberTest");

            if (priority < 1 || priority > 2)
                return false;
            Log.d("LeoApp", "passedPriorityNumberSizeTest");

            if (priority == 2)
                birthyear = 0x58;
            else if (parts[0].length() != 12)
                return false;
            else
                birthyear = Integer.parseInt(parts[0].substring(10));
            Log.d("LeoApp", "passedBirthyearTest");
        } catch (NumberFormatException e) {
            return false;
        }

        return birthyear >= 0 && getChecksum(parts[0], priority, birthyear).equals(parts[2]);
    }

    private int toInt(String s) {
        int    result = 0, i, count = 1;
        String regex  = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (char c : s.toCharArray()) {
            for (i = 0; i < regex.length(); i++) {
                if (c == regex.charAt(i))
                    break;
            }
            result += i * count;
            count *= 64;
        }
        return result;
    }

    private String getChecksum(String username, int priority, int birthyear) {
        Calendar c = new GregorianCalendar();

        int year  = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day   = c.get(Calendar.DAY_OF_MONTH);

        int numericName     = toInt(username.substring(0, 3));
        int numericLastName = toInt(username.substring(3, 6));

        long checksum = (long) (Long.valueOf((int) (Math.pow(year, 2)) + "" + (int) (Math.pow(day, 2)) + "" + (int) (Math.pow(month, 2))) * username.length() * Math.cos(birthyear) + priority * (numericName - numericLastName));

        return Long.toHexString(checksum);
    }
}