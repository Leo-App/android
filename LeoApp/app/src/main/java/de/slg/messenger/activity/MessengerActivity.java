package de.slg.messenger.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import de.slg.leoapp.R;
import de.slg.leoapp.dialog.InformationDialog;
import de.slg.leoapp.notification.NotificationHandler;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.LeoAppFeatureActivity;
import de.slg.messenger.activity.fragment.ChatsFragment;
import de.slg.messenger.activity.fragment.SearchFragment;
import de.slg.messenger.activity.fragment.UserFragment;

public class MessengerActivity extends LeoAppFeatureActivity {
    private ChatsFragment  cFragment;
    private UserFragment   uFragment;
    private SearchFragment sFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getController().registerMessengerActivity(this);

        Utils.getController().getMessengerDatabase();

        initTabs();

        Dialog dialog = new InformationDialog(this, "Dieser Teil der Anwendung ist noch in Arbeit!");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_wrapper_messenger;
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
        return R.id.actionBarOverview;
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
            if (Utils.checkNetwork()) {
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
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_question_answer_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_search_white_24dp);
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