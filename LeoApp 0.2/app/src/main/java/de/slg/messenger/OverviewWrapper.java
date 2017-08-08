package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import de.slg.nachhilfe.NachhilfeboerseActivity;
import de.slg.schwarzes_brett.SchwarzesBrettActivity;
import de.slg.startseite.MainActivity;
import de.slg.stimmungsbarometer.StimmungsbarometerActivity;
import de.slg.stundenplan.WrapperStundenplanActivity;
import de.slg.vertretung.WrapperSubstitutionActivity;

public class OverviewWrapper extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ChatsFragment cFragment;
    private UserFragment uFragment;
    private Chat[] chatArray = null;
    private User[] userArray = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.registerOverviewWrapper(this);
        Utils.context = getApplicationContext();
        Utils.getMDB();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrapper_messenger);

        initToolbar();
        initArrays();
        initNavigationView();
        initTabs();

        Utils.getNotificationManager().cancel(5453);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
        } else if (item.getItemId() == R.id.action_add) {
            if (Utils.checkNetwork()) {
                startActivity(new Intent(getApplicationContext(), AddGroupChatActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.need_internet, Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public void finish() {
        Utils.registerOverviewWrapper(null);
        super.finish();
    }

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarOverview);
        actionBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
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
                        return true;
                    case R.id.newsboard:
                        i = new Intent(getApplicationContext(), SchwarzesBrettActivity.class);
                        break;
                    case R.id.nachhilfe:
                        i = new Intent(getApplicationContext(), NachhilfeboerseActivity.class);
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
                    case R.id.vertretung:
                        i = new Intent(getApplicationContext(), WrapperSubstitutionActivity.class);
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

        TextView grade = (TextView) navigationView.getHeaderView(0).findViewById(R.id.grade);
        grade.setText(Utils.getUserStufe());
        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initArrays() {
        userArray = Utils.getMDB().getUsers();
        chatArray = Utils.getMDB().getChats(false);
        Utils.receive();
    }

    public void notifyUpdate() {
        chatArray = Utils.getMDB().getChats(false);
        userArray = Utils.getMDB().getUsers();
        uFragment.refreshUI();
        cFragment.refreshUI();
        ChatActivity chatActivity = Utils.getChatActivity();
        if (chatActivity != null)
            chatActivity.refreshUI(true, false);
    }

    public static class UserFragment extends Fragment {
        public View view;
        public RecyclerView rvUsers;
        View.OnClickListener clickListener;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_user_overview, container, false);

            initRecyclerView();

            return view;
        }

        private void initRecyclerView() {
            rvUsers = (RecyclerView) view.findViewById(R.id.recyclerViewUser);
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = rvUsers.getChildAdapterPosition(view);
                    if (position < Utils.getOverviewWrapper().userArray.length) {
                        User clickedUser = Utils.getOverviewWrapper().userArray[position];
                        Chat c = Utils.getMDB().getChatWith(clickedUser.uid);
                        Intent i = new Intent(getContext(), ChatActivity.class)
                                .putExtra("loading", c == null);
                        if (c == null) {
                            if (Utils.checkNetwork()) {
                                c = new Chat(-1, "" + clickedUser.uid + " - " + Utils.getCurrentUser().uid, false, Chat.Chattype.PRIVATE);
                                new CreateChat(c).execute();
                            } else {
                                Toast.makeText(getContext(), R.string.need_internet, Toast.LENGTH_SHORT).show();
                            }
                        }
                        ChatActivity.currentChat = c;
                        startActivity(i);
                    }
                }
            };

            rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
            rvUsers.setAdapter(new UserAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().userArray, clickListener));
        }

        public void refreshUI() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvUsers.swapAdapter(new UserAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().userArray, clickListener), false);
                }
            });
        }

        private class UserAdapter extends RecyclerView.Adapter {
            private final LayoutInflater inflater;
            private final User[] array;
            private final View.OnClickListener listener;

            UserAdapter(LayoutInflater inflater, User[] array, View.OnClickListener listener) {
                this.inflater = inflater;
                this.array = array;
                this.listener = listener;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.list_item_user, null));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                View v = holder.itemView;
                TextView username = (TextView) v.findViewById(R.id.username);
                username.setText(array[position].uname);
                v.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
            }

            @Override
            public int getItemCount() {
                return array.length;
            }

            private class ViewHolder extends RecyclerView.ViewHolder {
                ViewHolder(View itemView) {
                    super(itemView);
                    itemView.setOnClickListener(listener);
                }
            }
        }

        private class CreateChat extends AsyncTask<Void, Void, Void> {
            private final Chat c;
            private final String url;

            CreateChat(Chat c) {
                this.c = c;
                url = generateURL(c);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (Utils.checkNetwork()) {
                    try {
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new URL(url)
                                                        .openConnection()
                                                        .getInputStream(), "UTF-8"));
                        String erg = "";
                        String l;
                        while ((l = reader.readLine()) != null)
                            erg += l;
                        reader.close();
                        if (!erg.startsWith("error"))
                            c.cid = Integer.parseInt(erg);
                        else
                            Log.e("Error", erg);
                        Utils.getMDB().insertAssoziation(new Assoziation(c.cid, Utils.getUserID()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            private String generateURL(Chat chat) {
                String chatname = chat.cname.replace(' ', '+');
                Utils.getMDB().setChatname(chat);
                return "http://moritz.liegmanns.de/messenger/addChat.php?key=5453&chatname=" + chatname + "&chattype=" + Chat.Chattype.PRIVATE.toString().toLowerCase();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Utils.receive();
            }
        }
    }

    public static class ChatsFragment extends Fragment {
        public RecyclerView rvChats;
        private View view;
        private View.OnClickListener clickListener;
        private View.OnLongClickListener longClickListener;
        private int selected;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_chat_overview, container, false);

            initRecyclerView();

            return view;
        }

        private void initRecyclerView() {
            selected = -1;
            rvChats = (RecyclerView) view.findViewById(R.id.recyclerViewChats);
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = rvChats.getChildAdapterPosition(view);
                    ChatActivity.currentChat = Utils.getOverviewWrapper().chatArray[position];
                    startActivity(new Intent(getContext(), ChatActivity.class).putExtra("loading", false));
                    view.findViewById(R.id.notify).setVisibility(View.GONE);
                    view.findViewById(R.id.imageButtonDelete).setVisibility(View.GONE);
                    view.findViewById(R.id.imageButtonMute).setVisibility(View.GONE);
                }
            };
            longClickListener = new View.OnLongClickListener() {
                private int previousPosition = -1;
                private int visibility;

                @Override
                public boolean onLongClick(final View view) {
                    if (previousPosition != -1) {
                        rvChats.getChildAt(previousPosition).findViewById(R.id.imageButtonDelete).setVisibility(View.GONE);
                        rvChats.getChildAt(previousPosition).findViewById(R.id.imageButtonMute).setVisibility(View.GONE);
                        rvChats.getChildAt(previousPosition).findViewById(R.id.notify).setVisibility(visibility);
                    }
                    previousPosition = rvChats.getChildAdapterPosition(view);
                    final View delete = view.findViewById(R.id.imageButtonDelete);
                    final View mute = view.findViewById(R.id.imageButtonMute);
                    final View notify = view.findViewById(R.id.notify);
                    visibility = notify.getVisibility();
                    notify.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                    mute.setVisibility(View.VISIBLE);
                    return true;
                }
            };

            rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
            rvChats.setAdapter(new ChatAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().chatArray, clickListener, longClickListener));
        }

        public void refreshUI() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rvChats.swapAdapter(new ChatAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().chatArray, clickListener, longClickListener), false);
                }
            });
        }

        private class ChatAdapter extends RecyclerView.Adapter {
            private final LayoutInflater inflater;
            private final Chat[] chats;
            private final View.OnClickListener clickListener;
            private final View.OnLongClickListener longClickListener;

            ChatAdapter(LayoutInflater inflater, Chat[] chats, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
                this.inflater = inflater;
                this.chats = chats;
                this.clickListener = clickListener;
                this.longClickListener = longClickListener;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.list_item_chat, null));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                final View v = holder.itemView;
                final TextView chatname = (TextView) v.findViewById(R.id.chatname);
                final TextView lastSender = (TextView) v.findViewById(R.id.letzteNachrichtAbsender);
                final TextView lastMessage = (TextView) v.findViewById(R.id.letzteNachrichtText);
                final ImageView icon = (ImageView) v.findViewById(R.id.iconChat);
                final View iconMute = v.findViewById(R.id.iconMute);
                final View buttonDelete = v.findViewById(R.id.imageButtonDelete);
                final View buttonMute = v.findViewById(R.id.imageButtonMute);
                final View notify = v.findViewById(R.id.notify);

                final Chat c = chats[position];

                if (c != null) {
                    chatname.setText(c.cname);

                    if (c.m != null) {
                        lastMessage.setVisibility(View.VISIBLE);
                        lastSender.setVisibility(View.VISIBLE);
                        v.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
                        lastMessage.setText(c.m.mtext);
                        lastSender.setText(c.m.uname);
                        if (!c.m.mread)
                            notify.setVisibility(View.VISIBLE);
                        else
                            notify.setVisibility(View.GONE);
                    } else {
                        lastMessage.setVisibility(View.GONE);
                        lastSender.setVisibility(View.GONE);
                        v.findViewById(R.id.textView3).setVisibility(View.GONE);
                        notify.setVisibility(View.GONE);
                    }

                    if (c.ctype == Chat.Chattype.PRIVATE) {
                        icon.setImageResource(R.drawable.ic_chat_bubble_white_24dp);
                    } else {
                        icon.setImageResource(R.drawable.ic_question_answer_white_24dp);
                    }

                    if (c.mute) {
                        iconMute.setVisibility(View.VISIBLE);
                    } else {
                        iconMute.setVisibility(View.GONE);
                    }
                    buttonMute.setActivated(c.mute);

                    if (position != selected) {
                        buttonDelete.setVisibility(View.GONE);
                        buttonMute.setVisibility(View.GONE);
                    } else {
                        buttonDelete.setVisibility(View.VISIBLE);
                        buttonMute.setVisibility(View.VISIBLE);
                    }

                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.getMDB().deleteChat(c.cid);
                            selected = -1;
                            Utils.getOverviewWrapper().notifyUpdate();
                        }
                    });
                    buttonMute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.getMDB().muteChat(c.cid, !c.mute);
                            selected = -1;
                            Utils.getOverviewWrapper().notifyUpdate();
                        }
                    });
                }
            }

            @Override
            public int getItemCount() {
                return chats.length;
            }

            private class ViewHolder extends RecyclerView.ViewHolder {
                ViewHolder(View itemView) {
                    super(itemView);
                    itemView.setOnClickListener(clickListener);
                    itemView.setOnLongClickListener(longClickListener);
                }
            }
        }
    }
}