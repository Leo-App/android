package de.slg.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import static de.slg.messenger.DBConnection.DBHelper.USER_DEFAULTNAME;
import static de.slg.messenger.DBConnection.DBHelper.USER_NAME;
import static de.slg.messenger.DBConnection.DBHelper.USER_STUFE;

public class OverviewWrapper extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ChatsFragment cFragment;
    private UserFragment uFragment;
    private SearchFragment sFragment;
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

    private void initNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        navigationView.getMenu().findItem(R.id.messenger).setChecked(true);

//        navigationView.getMenu().findItem(R.id.nachhilfe).setEnabled(Utils.isVerified());
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
//                    case R.id.nachhilfe:
//                        i = new Intent(getApplicationContext(), NachhilfeboerseActivity.class);
//                        break;
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
//                    case R.id.vertretung:
//                        i = new Intent(getApplicationContext(), WrapperSubstitutionActivity.class);
//                        break;
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
        if (Utils.getUserPermission() == 2)
            grade.setText(Utils.getLehrerKuerzel());
        else
            grade.setText(Utils.getUserStufe());

        ImageView mood = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mood.setImageResource(Utils.getCurrentMoodRessource());
    }

    private void initArrays() {
        userArray = Utils.getMDB().getUsers();
        chatArray = Utils.getMDB().getChats(false);
        Utils.receiveMessenger();
    }

    public void notifyUpdate() {
        chatArray = Utils.getMDB().getChats(false);
        userArray = Utils.getMDB().getUsers();
        uFragment.refreshUI();
        cFragment.refreshUI();
        sFragment.refreshUI();
        ChatActivity chatActivity = Utils.getChatActivity();
        if (chatActivity != null)
            chatActivity.refreshUI(true, false);
    }

    public static class UserFragment extends Fragment {
        public View view;
        public RecyclerView rvUsers;
        View.OnClickListener userClickListener;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

                initRecyclerView();
            }

            return view;
        }

        private void initRecyclerView() {
            rvUsers = (RecyclerView) view.findViewById(R.id.recyclerView);
            userClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = rvUsers.getChildAdapterPosition(view);
                    User clickedUser = Utils.getOverviewWrapper().userArray[position];
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra("uid", clickedUser.uid)
                            .putExtra("cid", Utils.getMDB().getChatWith(clickedUser.uid))
                            .putExtra("cname", clickedUser.uname)
                            .putExtra("ctype", Chat.Chattype.PRIVATE.toString()));
                }
            };

            rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
            rvUsers.setAdapter(new UserAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().userArray, userClickListener));
        }

        public void refreshUI() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rvUsers != null)
                        rvUsers.swapAdapter(new UserAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().userArray, userClickListener), false);
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
                final TextView username = (TextView) v.findViewById(R.id.username);
                final TextView userdefault = (TextView) v.findViewById(R.id.userdefault);
                username.setText(array[position].uname);
                userdefault.setText(array[position].udefaultname + ", " + array[position].ustufe);
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
    }

    public static class ChatsFragment extends Fragment {
        public RecyclerView rvChats;
        private View view;
        private View.OnClickListener chatClickListener;
        private View.OnLongClickListener chatLongClickListener;
        private int selected;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_recyclerview, container, false);

                initRecyclerView();
            }

            return view;
        }

        private void initRecyclerView() {
            selected = -1;
            rvChats = (RecyclerView) view.findViewById(R.id.recyclerView);
            chatClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = rvChats.getChildAdapterPosition(view);
                    Chat clickedChat = Utils.getOverviewWrapper().chatArray[position];
                    startActivity(new Intent(getContext(), ChatActivity.class)
                            .putExtra("cid", clickedChat.cid)
                            .putExtra("cname", clickedChat.cname)
                            .putExtra("ctype", clickedChat.ctype.toString()));
                    view.findViewById(R.id.notify).setVisibility(View.GONE);
                    view.findViewById(R.id.imageButtonDelete).setVisibility(View.GONE);
                    view.findViewById(R.id.imageButtonMute).setVisibility(View.GONE);
                }
            };
            chatLongClickListener = new View.OnLongClickListener() {
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
            rvChats.setAdapter(new ChatAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().chatArray, chatClickListener, chatLongClickListener));
        }

        public void refreshUI() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (rvChats != null)
                        rvChats.swapAdapter(new ChatAdapter(getActivity().getLayoutInflater(), Utils.getOverviewWrapper().chatArray, chatClickListener, chatLongClickListener), false);
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

    public static class SearchFragment extends Fragment {
        public View view;
        public RecyclerView rvSearch;
        boolean initialized = false;
        private Object[] data;
        private View.OnClickListener clickListener;

        private boolean expanded;

        private String suchbegriff = "";
        private boolean chatsFirst = false;
        private String name = USER_DEFAULTNAME;
        private boolean nameDesc = false, groupGrade = true;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_search, container, false);

                data = Utils.getMDB().getSuchergebnisse(suchbegriff, chatsFirst, USER_STUFE + ", " + name);

                initRecyclerView();
                initSearch();
                initSort();
            }

            initialized = true;
            return view;
        }

        private void initRecyclerView() {
            rvSearch = (RecyclerView) view.findViewById(R.id.recyclerViewSearch);

            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = rvSearch.getChildAdapterPosition(v);
                    if (data[position] instanceof User) {
                        User clickedUser = (User) data[position];
                        startActivity(new Intent(getContext(), ChatActivity.class)
                                .putExtra("uid", clickedUser.uid)
                                .putExtra("cid", Utils.getMDB().getChatWith(clickedUser.uid))
                                .putExtra("cname", clickedUser.uname)
                                .putExtra("ctype", Chat.Chattype.PRIVATE.toString()));
                    } else {
                        Chat clickedChat = (Chat) data[position];
                        startActivity(new Intent(getContext(), ChatActivity.class)
                                .putExtra("cid", clickedChat.cid)
                                .putExtra("cname", clickedChat.cname)
                                .putExtra("ctype", Chat.Chattype.GROUP.toString()));
                    }
                }
            };

            rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
            rvSearch.setAdapter(new HybridAdapter(getActivity().getLayoutInflater()));
        }

        private void initSearch() {
            TextView input = (TextView) view.findViewById(R.id.editText);
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    suchbegriff = s.toString();
                    refreshUI();
                }
            });
        }

        private void initSort() {
            expanded = false;
            view.findViewById(R.id.sortCard).setVisibility(View.GONE);

            final FloatingActionButton expand = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
            expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expanded = !expanded;
                    if (expanded) {
                        expand.setImageResource(R.drawable.ic_expand_less_white_24dp);
                        SearchFragment.this.view.findViewById(R.id.sortCard).setVisibility(View.VISIBLE);
                    } else {
                        expand.setImageResource(R.drawable.ic_expand_more_white_24dp);
                        SearchFragment.this.view.findViewById(R.id.sortCard).setVisibility(View.GONE);
                    }
                }
            });

            final Button first = (Button) view.findViewById(R.id.buttonFirst);
            first.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chatsFirst) {
                        first.setText("Benutzer");
                        chatsFirst = false;
                    } else {
                        first.setText("Chats");
                        chatsFirst = true;
                    }
                    refreshUI();
                }
            });
            if (!chatsFirst) {
                first.setText("Benutzer");
            } else {
                first.setText("Chats");
            }

            final Button sortName = (Button) view.findViewById(R.id.buttonName);
            sortName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (name.equals(USER_DEFAULTNAME)) {
                        name = USER_NAME;
                        sortName.setText("Nickname");
                    } else {
                        name = USER_DEFAULTNAME;
                        sortName.setText("Standardname");
                    }
                    refreshUI();
                }
            });
            if (!name.equals(USER_DEFAULTNAME)) {
                sortName.setText("Nickname");
            } else {
                sortName.setText("Standardname");
            }

            final ImageButton nameUpDown = (ImageButton) view.findViewById(R.id.buttonNameUpDown);
            nameUpDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nameDesc) {
                        nameDesc = false;
                        nameUpDown.setImageResource(R.drawable.ic_expand_less_white_24dp);
                    } else {
                        nameDesc = true;
                        nameUpDown.setImageResource(R.drawable.ic_expand_more_white_24dp);
                    }
                    refreshUI();
                }
            });
            if (!nameDesc) {
                nameUpDown.setImageResource(R.drawable.ic_expand_less_white_24dp);
            } else {
                nameUpDown.setImageResource(R.drawable.ic_expand_more_white_24dp);
            }


            final Button grade = (Button) view.findViewById(R.id.buttonGrade);
            grade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    grade.setActivated(groupGrade);
                    if (groupGrade)
                        grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorInactive));
                    else
                        grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    groupGrade = !groupGrade;
                    refreshUI();
                }
            });
            grade.setActivated(!groupGrade);
            if (!groupGrade)
                grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorInactive));
            else
                grade.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

        }

        public void refreshUI() {
            if (initialized) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rvSearch != null) {
                            String orderUser = "";
                            if (groupGrade)
                                orderUser = USER_STUFE + ", ";
                            orderUser += name;
                            if (nameDesc)
                                orderUser += " DESC";
                            data = Utils.getMDB().getSuchergebnisse(suchbegriff, chatsFirst, orderUser);
                            rvSearch.swapAdapter(new HybridAdapter(getActivity().getLayoutInflater()), false);
                        }
                    }
                });
            }
        }

        private class HybridAdapter extends RecyclerView.Adapter {
            private final LayoutInflater inflater;

            HybridAdapter(LayoutInflater inflater) {
                this.inflater = inflater;
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ViewHolder(inflater.inflate(R.layout.list_item_user, null));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                View v = holder.itemView;
                Object current = data[position];

                final TextView username = (TextView) v.findViewById(R.id.username);
                final TextView userdefault = (TextView) v.findViewById(R.id.userdefault);
                final ImageView icon = (ImageView) v.findViewById(R.id.iconUser);

                if (current instanceof User) {
                    User u = (User) current;
                    username.setText(u.uname);
                    userdefault.setText(u.udefaultname + ", " + u.ustufe);
                    icon.setImageResource(R.drawable.ic_account_circle_black_24dp);
                } else {
                    Chat c = (Chat) current;
                    username.setText(c.cname);
                    userdefault.setText("");
                    icon.setImageResource(R.drawable.ic_question_answer_white_24dp);
                }
            }

            @Override
            public int getItemCount() {
                return data.length;
            }

            private class ViewHolder extends RecyclerView.ViewHolder {
                ViewHolder(View itemView) {
                    super(itemView);
                    itemView.setOnClickListener(clickListener);
                    itemView.findViewById(R.id.checkBox).setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}