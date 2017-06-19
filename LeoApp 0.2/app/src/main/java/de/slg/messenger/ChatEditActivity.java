package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.R;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

public class ChatEditActivity extends AppCompatActivity {
    static Chat currentChat;
    private Menu menu;
    private ListView lvUsers;
    private UserAdapter uOfChat1, uOfChat2, uRest;
    private String mode;
    private User[] usersOfChat1, usersOfChat2;
    private User[] usersNotInChat;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_chat_edit);

        initToolbar();
        initListView();
        initLeaveButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Utils.getMessengerDBConnection().isUserInChat(Utils.getCurrentUser(), currentChat))
            getMenuInflater().inflate(R.menu.messenger_chat_edit, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (mi.getItemId() == R.id.action_addUserToChat) {
            lvUsers.setAdapter(uRest);
            menu.clear();
            mode = "add";
            getMenuInflater().inflate(R.menu.messenger_confirm_action, menu);
        } else if (mi.getItemId() == R.id.action_removeUsersFromChat) {
            lvUsers.setAdapter(uOfChat1);
            menu.clear();
            mode = "remove";
            getMenuInflater().inflate(R.menu.messenger_confirm_action, menu);
        } else if (mi.getItemId() == R.id.action_cancel) {
            menu.clear();
            getMenuInflater().inflate(R.menu.messenger_chat_edit, menu);
            lvUsers.setAdapter(uOfChat2);
            mode = "";
        } else if (mi.getItemId() == R.id.action_confirm) {
            if (mode.equals("add"))
                addUsers(uRest.getSelected());
            if (mode.equals("remove"))
                removeUsers(uOfChat1.getSelected());
            mode = "";
            menu.clear();
            getMenuInflater().inflate(R.menu.messenger_chat_edit, menu);
            lvUsers.setAdapter(uOfChat2);
        }
        return true;
    }

    private void removeUsers(User... users) {
        new RemoveUser().execute(users);
        usersOfChat1 = Utils.getMessengerDBConnection().getUsersInChat(currentChat, false);
        usersOfChat2 = Utils.getMessengerDBConnection().getUsersInChat(currentChat, true);
        usersNotInChat = Utils.getMessengerDBConnection().getUsersNotInChat(currentChat);
    }

    private void addUsers(User... users) {
        new AddUser().execute(users);
        Utils.receive();
        usersOfChat1 = Utils.getMessengerDBConnection().getUsersInChat(currentChat, false);
        usersOfChat2 = Utils.getMessengerDBConnection().getUsersInChat(currentChat, true);
        usersNotInChat = Utils.getMessengerDBConnection().getUsersNotInChat(currentChat);
    }

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarEditChat);
        actionBar.setTitleTextColor(getResources().getColor(android.R.color.white));
        actionBar.setTitle(currentChat.cname);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initListView() {
        lvUsers = (ListView) findViewById(R.id.listViewUsersEdit);
        usersOfChat1 = Utils.getMessengerDBConnection().getUsersInChat(currentChat, false);
        usersOfChat2 = Utils.getMessengerDBConnection().getUsersInChat(currentChat, true);
        usersNotInChat = Utils.getMessengerDBConnection().getUsersNotInChat(currentChat);
        uOfChat1 = new UserAdapter(getApplicationContext(), usersOfChat1, true);
        uOfChat2 = new UserAdapter(getApplicationContext(), usersOfChat2, false);
        uRest = new UserAdapter(getApplicationContext(), usersNotInChat, true);
        lvUsers.setAdapter(uOfChat2);
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mode.equals("")) {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        });
    }

    private void initLeaveButton() {
        Button buttonLeave = (Button) findViewById(R.id.buttonLeaveChat);
        if (Utils.getMessengerDBConnection().isUserInChat(Utils.getCurrentUser(), currentChat)) {
            buttonLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeUsers(Utils.getCurrentUser());
                    finish();
                    startActivity(new Intent(getApplicationContext(), OverviewWrapper.class));
                }
            });
        } else {
            findViewById(R.id.layoutding2).setVisibility(View.GONE);
        }
    }

    private class AddUser extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... params) {
            for (User u : params) {
                sendAssoziation(new Assoziation(currentChat.cid, u.uid, false));
            }
            return null;
        }

        private void sendAssoziation(Assoziation assoziation) {
            if (assoziation != null) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(assoziation))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    while (reader.readLine() != null) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String generateURL(Assoziation assoziation) {
            return "http://moritz.liegmanns.de/messenger/addUserToChat.php?key=5453&userid=" + assoziation.uid + "&chatid=" + assoziation.cid;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            lvUsers.setAdapter(new UserAdapter(getApplicationContext(), Utils.getMessengerDBConnection().getUsersInChat(currentChat, true), false));
            super.onPostExecute(aVoid);
        }
    }

    private class RemoveUser extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... params) {
            for (User u : params) {
                removeAssoziation(new Assoziation(currentChat.cid, u.uid, false));
                Utils.getMessengerDBConnection().removeUserFromChat(u, currentChat);
            }
            return null;
        }

        private void removeAssoziation(Assoziation assoziation) {
            if (assoziation != null) {
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(assoziation))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    while (reader.readLine() != null) {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private String generateURL(Assoziation assoziation) {
            return "http://moritz.liegmanns.de/messenger/removeAssoziation.php?key=5453&chatid=" + assoziation.cid + "&userid=" + assoziation.uid;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            lvUsers.setAdapter(new UserAdapter(getApplicationContext(), Utils.getMessengerDBConnection().getUsersInChat(currentChat, true), false));
            super.onPostExecute(aVoid);
        }
    }
}