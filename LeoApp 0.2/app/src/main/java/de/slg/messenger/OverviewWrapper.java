package de.slg.messenger;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.ReceiveService;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class OverviewWrapper extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ChatsFragment cFragment;
    public UserFragment uFragment;
    public ChatActivity chatActivity;
    public Chat[] chatArray = null;
    public User[] userArray = null;
    public ListView lvUsers, lvChats;
    public boolean userDone = false, chatsDone = false;

    public static final User currentUser = Utils.getCurrentUser();

    public DBConnection dbConnection;

    private Intent serviceIntent;
    public NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_messenger);

        serviceIntent = new Intent(getApplicationContext(), ReceiveService.class);

        initToolbar();
        initDatabase();
        initNavigationView();
        initTabs();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        ChatActivity.wrapper = this;
        AddGroupChatActivity.wrapper = this;
        ReceiveService.wrapper = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (mi.getItemId() == R.id.action_add) {
            if (Utils.checkNetwork()) {
                startActivity(new Intent(getApplicationContext(), AddGroupChatActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Verbinde dich mit dem Internet um neue Gruppen zu erstellen.", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarOverview);
        actionBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        actionBar.setTitle("Messenger");
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initTabs() {
        ChatsFragment.wrapper = this;
        cFragment = new ChatsFragment();

        UserFragment.wrapper = this;
        uFragment = new UserFragment();

        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 1)
                    return uFragment;
                return cFragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_question_answer_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.messenger).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.foodmarks:
                        i = new Intent(getApplicationContext(), WrapperQRActivity.class);
                        break;
                    case R.id.messenger:
                        i = null;
                        break;
                    case R.id.newsboard:
                        i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                        break;
                    case R.id.nachhilfe:
                        i = new Intent(getApplicationContext(), MainActivity.class);
                        break;
                    case R.id.stundenplan:
                        i = new Intent(getApplicationContext(), WrapperStundenplanActivity.class);
                        break;
                    case R.id.barometer:
                        i = new Intent(getApplicationContext(), StimmungsbarometerActivity.class);
                        break;
                    case R.id.klausurplan:
                        i = new Intent(getApplicationContext(), KlausurplanActivity.class);
                        break;
                    case R.id.startseite:
                        i = null;
                        break;
                    case R.id.settings:
                        i = new Intent(getApplicationContext(), PreferenceActivity.class);
                        break;
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
        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initDatabase() {
        dbConnection = new DBConnection(getApplicationContext(), currentUser, this);
        userArray = dbConnection.getUsers();
        chatArray = dbConnection.getChats();
        receive();
    }

    public int indexOf(Chat c) {
        if (c != null && chatArray != null)
            for (int i = 0; i < chatArray.length; i++)
                if (c.equals(chatArray[i]))
                    return i;
        return -1;
    }

    public User findUser(int id) {
        for (User u : userArray)
            if (u.userId == id)
                return u;
        return null;
    }

    public void notifyUpdate() {
        uFragment.refreshUI();
        cFragment.refreshUI();
        if (chatActivity != null)
            chatActivity.refreshUI();
    }

    public void receive() {
        stopService(serviceIntent);
        new ReceiveTask(this).execute();
        startService(serviceIntent);
    }
}