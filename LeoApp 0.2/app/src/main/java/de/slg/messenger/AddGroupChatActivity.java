package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import de.slg.leoapp.R;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

public class AddGroupChatActivity extends AppCompatActivity {
    private EditText etChatname;
    private UserAdapter userAdapter;
    private boolean chatnameSet, usersSelected;
    private MenuItem confirm;
    private Chat newChat;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_chat);
        Utils.registerAddGroupChatActivity(this);

        initToolbar();
        initListView();
        initEditText();

        chatnameSet = false;
        usersSelected = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_confirm_action, menu);
        confirm = menu.findItem(R.id.action_confirm);
        confirm.setVisible(chatnameSet && usersSelected);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_confirm) {
            createNewChat();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.registerAddGroupChatActivity(null);
    }

    private void createNewChat() {
        newChat = new Chat(-1, etChatname.getText().toString(), false, Chat.Chattype.GROUP);
        new CreateChat().execute();
    }

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarAddChat);
        actionBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        actionBar.setTitle(R.string.title_new_groupchat);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        ListView lvAllUsers = (ListView) findViewById(R.id.listViewAllUsers);
        User[] allUsers = Utils.getMDB().getUsers();
        userAdapter = new UserAdapter(getApplicationContext(), allUsers);
        lvAllUsers.setAdapter(userAdapter);
        lvAllUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                final TextView username = (TextView) view.findViewById(R.id.username);
                int color = ContextCompat.getColor(getApplicationContext(), R.color.colorAccent);
                if (!checkBox.isChecked())
                    color = ContextCompat.getColor(getApplicationContext(), R.color.colorText);
                username.setTextColor(color);
                usersSelected = userAdapter.selectCount() > 0;
                confirm.setVisible(chatnameSet && usersSelected);
            }
        });
    }

    private void initEditText() {
        etChatname = (EditText) findViewById(R.id.editTextChatName);
        etChatname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                chatnameSet = s.length() > 0;
                confirm.setVisible(chatnameSet && usersSelected);
            }
        });
    }

    private class CreateChat extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendChat(newChat);
            if (newChat.cid != -1) {
                User[] members = userAdapter.getSelected();
                sendAssoziation(new Assoziation(newChat.cid, Utils.getUserID()));
                Utils.getMDB().insertAssoziation(new Assoziation(newChat.cid, Utils.getUserID()));
                Utils.receiveMessenger();
                for (User member : members) {
                    sendAssoziation(new Assoziation(newChat.cid, member.uid));
                }
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
                reader.close();
                chat.cid = Integer.parseInt(erg);
                Utils.getMDB().insertAssoziation(new Assoziation(chat.cid, Utils.getUserID()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendAssoziation(Assoziation assoziation) {
            if (assoziation != null)
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(assoziation))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    String erg = "";
                    String l;
                    while ((l = reader.readLine()) != null)
                        erg += l;
                    Log.d("SendTask", "result of send Assoziation: " + erg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        private String generateURL(Chat chat) throws UnsupportedEncodingException {
            String chatname = URLEncoder.encode(chat.cname, "UTF-8");
            return "http://moritz.liegmanns.de/messenger/addChat.php?key=5453&chatname=" + chatname + "&chattype=" + Chat.Chattype.GROUP.toString().toLowerCase();
        }

        private String generateURL(Assoziation assoziation) {
            return "http://moritz.liegmanns.de/messenger/addAssoziation.php?key=5453&userid=" + assoziation.uid + "&chatid=" + assoziation.cid;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.receiveMessenger();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            startActivity(new Intent(getApplicationContext(), ChatActivity.class)
                    .putExtra("cid", newChat.cid)
                    .putExtra("cname", newChat.cname)
                    .putExtra("ctype", Chat.Chattype.GROUP.toString()));
            finish();
        }
    }
}