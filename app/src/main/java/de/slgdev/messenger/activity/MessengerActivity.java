package de.slgdev.messenger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.notification.NotificationHandler;
import de.slgdev.leoapp.utility.NetworkUtils;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.LeoAppNavigationActivity;
import de.slgdev.messenger.activity.fragment.ChatsFragment;
import de.slgdev.messenger.activity.fragment.SearchFragment;
import de.slgdev.messenger.activity.fragment.UserFragment;

public class MessengerActivity extends LeoAppNavigationActivity {
    private ChatsFragment  cFragment;
    private UserFragment   uFragment;
    private SearchFragment sFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerMessengerActivity(this);

        Utils.getController().getMessengerDatabase();

        initTabs();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_wrapper_messenger;
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
        return R.string.title_messenger;
    }

    @Override
    protected int getNavigationHighlightId() {
        return R.id.messenger;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_add) {
            if (NetworkUtils.isNetworkAvailable()) {
                startActivity(new Intent(getApplicationContext(), AddGroupChatActivity.class));
            } else {
                //TODO vielleicht offline verfügbar
                Toast.makeText(getApplicationContext(), R.string.need_internet, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void finish() {
        Utils.getController().registerMessengerActivity(null);
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.getNotificationManager().cancel(NotificationHandler.ID_MESSENGER);
        uFragment.refreshUI();
        cFragment.refreshUI();
        sFragment.refreshUI();
    }

    @Override
    protected String getActivityTag() {
        return "MessengerActivity";
    }

    private void initTabs() {
        cFragment = new ChatsFragment();
        uFragment = new UserFragment();
        sFragment = new SearchFragment();
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 1)
                    return uFragment;
                if (position == 2)
                    return sFragment;
                return cFragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.icon_messenger);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_search);
    }

    /**
     * Falls initialisiert, aktualisiert die Tabs und die {@link ChatActivity}
     */
    public void notifyUpdate() {
        if (uFragment != null)
            uFragment.refreshUI();
        if (cFragment != null)
            cFragment.refreshUI();
        if (sFragment != null)
            sFragment.refreshUI();
        ChatActivity chatActivity = Utils.getController().getChatActivity();
        if (chatActivity != null)
            chatActivity.refreshUI(true, false);
    }
}