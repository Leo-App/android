package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slg.leoapp.R;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

public class AddGroupChatActivity extends AppCompatActivity {

    public static OverviewWrapper wrapper;
    private User[] allUsers;
    private ListView lvAllUsers;
    private EditText etChatname;
    private UserAdapter userAdapter;
    private boolean bChatname, bUsersSelected;
    private Menu menu;
    private Chat newChat;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_chat);

        initToolbar();
        initListView();
        initEditText();

        bChatname = false;
        bUsersSelected = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == R.id.action_saveChat) {
            createNewChat();
        } else if (mi.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void createNewChat() {
        newChat = new Chat(-1, etChatname.getText().toString(), Chat.Chattype.GROUP);
        new CreateChat().execute();
    }

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarAddChat);
        actionBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        actionBar.setTitle(getString(R.string.title_new_groupchat));
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        lvAllUsers = (ListView) findViewById(R.id.listViewAllUsers);
        allUsers = wrapper.userArray;
        userAdapter = new UserAdapter(getApplicationContext(), allUsers, true);
        lvAllUsers.setAdapter(userAdapter);
        lvAllUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                TextView username = (TextView) view.findViewById(R.id.username);
                int color = getResources().getColor(R.color.colorAccent);
                if (!checkBox.isChecked())
                    color = getResources().getColor(R.color.colorText);
                username.setTextColor(color);
                bUsersSelected = userAdapter.selectCount() > 0;
                menu.clear();
                if (bChatname && bUsersSelected)
                    getMenuInflater().inflate(R.menu.messenger_add_chat, menu);
            }
        });
    }

    private void initEditText() {
        etChatname = (EditText) findViewById(R.id.editTextChatName);
        etChatname.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bChatname = s.length() > 0;
                menu.clear();
                if (bChatname && bUsersSelected)
                    getMenuInflater().inflate(R.menu.messenger_add_chat, menu);
            }
        });
        etChatname.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etChatname.getWindowToken(), 0);
                }
                return true;
            }
        });
    }

    class CreateChat extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            sendChat(newChat);
            User[] members = userAdapter.getSelected();
            sendAssoziation(new Assoziation(newChat.chatId, Utils.getUserID(), false));
            ChatActivity.chat = newChat;
            ChatActivity.chatname = newChat.chatName;
            startActivity(new Intent(getApplicationContext(), ChatActivity.class));
            finish();
            for (int i = 0; i < members.length; i++) {
                sendAssoziation(new Assoziation(newChat.chatId, members[i].userId, false));
            }
            return null;
        }

        private void sendChat(Chat chat) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(generateURL(chat)).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String erg = "";
                String l;
                while ((l = reader.readLine()) != null)
                    erg += l;
                Log.i("SendTask", "result of send Chat: " + erg);
                chat.chatId = Integer.parseInt(erg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean sendAssoziation(Assoziation assoziation) {
            if (assoziation != null)
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(generateURL(assoziation)).openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    String erg = "";
                    String l;
                    while ((l = reader.readLine()) != null)
                        erg += l;
                    Log.d("SendTask", "result of send Assoziation: " + erg);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return false;
        }

        private String generateURL(Chat chat) {
            String chatname = chat.chatName.replace(' ', '+');
            return "http://moritz.liegmanns.de/messenger/addChat.php?key=5453&chatname=" + chatname + "&chattype=" + Chat.Chattype.GROUP.toString().toLowerCase();
        }

        private String generateURL(Assoziation assoziation) {
            return "http://moritz.liegmanns.de/messenger/addUserToChat.php?key=5453&userid=" + assoziation.userID + "&chatid=" + assoziation.chatID;
        }
    }
}