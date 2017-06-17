package de.slg.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.essensqr.WrapperQRActivity;
import de.slg.klausurplan.KlausurplanActivity;
import de.slg.leoapp.PreferenceActivity;
import de.slg.leoapp.R;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;

public class OverviewWrapper extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    public ChatsFragment cFragment;
    public UserFragment uFragment;
    public Chat[] chatArray = null;
    public User[] userArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.registerOverviewWrapper(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_messenger);

        initToolbar();
        initDatabase();
        initNavigationView();
        initTabs();

        Utils.getNotificationManager().cancelAll();
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

    @Override
    public void finish() {
        Utils.getMessengerDBConnection().setOverviewWrapper(null);
        super.finish();
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
        cFragment = new ChatsFragment();
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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_question_answer_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_person_white_24dp);
    }

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.messenger).setChecked(true);

        navigationView.getMenu().findItem(R.id.nachhilfe).setEnabled(Utils.isVerified());
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
        userArray = Utils.getMessengerDBConnection().getUsers();
        chatArray = Utils.getMessengerDBConnection().getChats();
        Utils.receive();
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
        chatArray = Utils.getMessengerDBConnection().getChats();
        userArray = Utils.getMessengerDBConnection().getUsers();
        uFragment.refreshUI();
        cFragment.refreshUI();
        ChatActivity chatActivity = Utils.getChatActivity();
        if (chatActivity != null)
            chatActivity.refreshUI();
    }

    public static class UserFragment extends Fragment {
        public View rootView;
        public ListView lvUsers;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_user_overview, container, false);

            initListView();

            return rootView;
        }

        private void initListView() {
            lvUsers = (ListView) rootView.findViewById(R.id.listViewUser);
            lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < Utils.getOverviewWrapper().userArray.length) {
                        User clickedUser = Utils.getOverviewWrapper().userArray[position];
                        ChatActivity.chatname = clickedUser.userName;
                        Chat newChat = new Chat(-1, "" + clickedUser.userId + " - " + Utils.getCurrentUser().userId, Chat.Chattype.PRIVATE);
                        int index = Utils.getOverviewWrapper().indexOf(newChat);
                        if (index == -1) {
                            new CreateChat(clickedUser).execute(newChat);
                            ChatActivity.chat = newChat;
                        } else {
                            ChatActivity.chat = Utils.getOverviewWrapper().chatArray[index];
                        }
                        ChatActivity.chatname = clickedUser.userName;
                        startActivity(new Intent(getContext(), ChatActivity.class));
                    }
                }
            });
            lvUsers.setAdapter(new UserAdapter(getContext(), Utils.getOverviewWrapper().userArray, false));
        }

        public void refreshUI() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lvUsers.setAdapter(new UserAdapter(getContext(), Utils.getOverviewWrapper().userArray, false));
                }
            });
        }

        private class CreateChat extends AsyncTask<Chat, Void, Void> {

            private User other;

            CreateChat(User other) {
                this.other = other;
            }

            @Override
            protected Void doInBackground(Chat... params) {
                if (Utils.checkNetwork()) {
                    sendChat(params[0]);
                    sendAssoziation(new Assoziation(params[0].chatId, Utils.getUserID(), false));
                    sendAssoziation(new Assoziation(params[0].chatId, other.userId, false));
                }
                return null;
            }

            private void sendChat(Chat chat) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(chat))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    String erg = "";
                    String l;
                    while ((l = reader.readLine()) != null)
                        erg += l;
                    if (!erg.equals("error in chat"))
                        chat.chatId = Integer.parseInt(erg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private boolean sendAssoziation(Assoziation assoziation) {
                if (assoziation != null)
                    try {
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new URL(generateURL(assoziation))
                                                        .openConnection()
                                                        .getInputStream(), "UTF-8"));
                        while (reader.readLine() != null) {

                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                return false;
            }

            private String generateURL(Chat chat) {
                String chatname = chat.chatName.replace(' ', '+');
                return "http://moritz.liegmanns.de/messenger/addChat.php?key=5453&chatname=" + chatname + "&chattype=" + Chat.Chattype.PRIVATE.toString().toLowerCase();
            }

            private String generateURL(Assoziation assoziation) {
                return "http://moritz.liegmanns.de/messenger/addUserToChat.php?key=5453&userid=" + assoziation.userID + "&chatid=" + assoziation.chatID;
            }
        }
    }

    public static class ChatsFragment extends Fragment {
        public View rootView;
        public ListView lvChats;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_chat_overview, container, false);

            initListView();

            return rootView;
        }

        private void initListView() {
            lvChats = (ListView) rootView.findViewById(R.id.listViewChats);
            lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < Utils.getOverviewWrapper().chatArray.length) {
                        ChatActivity.chatname = Utils.getOverviewWrapper().chatArray[position].chatTitle;
                        ChatActivity.chat = Utils.getOverviewWrapper().chatArray[position];
                        startActivity(new Intent(getContext(), ChatActivity.class));
                    }
                }
            });
            lvChats.setAdapter(new ChatAdapter(Utils.getOverviewWrapper().getApplicationContext(), Utils.getOverviewWrapper().chatArray));
        }

        public void refreshUI() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lvChats.setAdapter(new ChatAdapter(getContext(), Utils.getOverviewWrapper().chatArray));
                }
            });
        }

        private class ChatAdapter extends ArrayAdapter<Chat> {
            private Context context;
            private int resId;
            private Chat[] chats;
            private User currentUser;

            ChatAdapter(Context context, Chat[] chats) {
                super(context, R.layout.list_item_chat, chats);
                this.context = context;
                this.resId = R.layout.list_item_chat;
                this.chats = chats;
                this.currentUser = Utils.getCurrentUser();
            }

            @NonNull
            @Override
            public View getView(int position, View v, @NonNull ViewGroup parent) {
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(resId, null);
                }
                TextView chatname = (TextView) v.findViewById(R.id.chatname);
                TextView lastMessage = (TextView) v.findViewById(R.id.letzteNachricht);
                ImageView icon = (ImageView) v.findViewById(R.id.iconChat);
                ImageView notify = (ImageView) v.findViewById(R.id.notify);
                if (position < chats.length && chats[position] != null) {
                    if (chats[position].chatTyp == Chat.Chattype.GROUP) {
                        chatname.setText(chats[position].chatName);
                        chats[position].chatTitle = chats[position].chatName;
                    } else {
                        String[] s = chats[position].chatName.split(" - ");
                        int idO;
                        if (currentUser.userId == Integer.parseInt(s[0]))
                            idO = Integer.parseInt(s[1]);
                        else
                            idO = Integer.parseInt(s[0]);
                        User o = Utils.getOverviewWrapper().findUser(idO);
                        if (o != null) {
                            chatname.setText(o.userName);
                            chats[position].chatTitle = o.userName;
                        }
                    }
                    if (chats[position].letzeNachricht != null)
                        lastMessage.setText(chats[position].letzeNachricht.toString());
                    if (chats[position].chatTyp == Chat.Chattype.PRIVATE)
                        icon.setImageResource(R.drawable.ic_chat_bubble_white_24dp);
                    if (chats[position].chatTyp == Chat.Chattype.GROUP)
                        icon.setImageResource(R.drawable.ic_question_answer_white_24dp);
                    if (chats[position].letzeNachricht != null && chats[position].letzeNachricht.senderId != currentUser.userId && !chats[position].letzeNachricht.read)
                        notify.setVisibility(View.VISIBLE);
                }
                return v;
            }
        }
    }
}