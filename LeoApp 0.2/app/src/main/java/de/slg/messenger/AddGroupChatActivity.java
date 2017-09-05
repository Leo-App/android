package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private final User[] users = Utils.getMDB().getUsers();
    private EditText etChatname;
    private MenuItem confirm;
    private boolean[] selection;
    private int       selected;

    private boolean chatnameSet, usersSelected;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_chat);
        Utils.registerAddGroupChatActivity(this);

        initToolbar();
        initContainer();
        initEditText();
        initSearchButton();

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
            new CreateChat().execute();
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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(R.string.title_new_groupchat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initEditText() {
        etChatname = (EditText) findViewById(R.id.editTextChatName);
        etChatname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                chatnameSet = s.length() > 0;
                confirm.setVisible(chatnameSet && usersSelected);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initContainer() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutUsers);
        linearLayout.removeAllViews();

        for (int i = 0; i < users.length; i++) {
            User u = users[i];
            View v = getLayoutInflater().inflate(R.layout.list_item_user, null);

            final TextView username    = (TextView) v.findViewById(R.id.username);
            final TextView userdefault = (TextView) v.findViewById(R.id.userdefault);

            username.setText(u.uname);
            userdefault.setText(u.udefaultname + ", " + u.ustufe);

            v.findViewById(R.id.checkBox).setVisibility(View.GONE);

            final int finalI = i;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selection[finalI] = !selection[finalI];
                    v.setSelected(selection[finalI]);

                    if (selection[finalI])
                        selected++;
                    else
                        selected--;

                    usersSelected = (selected > 0);
                }
            });

            linearLayout.addView(v);
        }

        selected = 0;
        selection = new boolean[users.length];
    }

    private void initSearchButton() {
        View v = findViewById(R.id.floatingActionButton);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                findViewById(R.id.editTextSearch).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.text_view_slide_in));
            }
        });
    }

    private class CreateChat extends AsyncTask<Void, Void, Void> {
        private int    cid;
        private String cname;

        @Override
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            cid = -1;
            cname = etChatname.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            sendChat();

            if (cid != -1) {
                sendAssoziation(new Assoziation(cid, Utils.getUserID()));
                for (int i = 0; i < users.length; i++) {
                    if (selection[i])
                        sendAssoziation(new Assoziation(cid, users[i].uid));
                }
            }

            return null;
        }

        private void sendChat() {
            try {
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        new URL(generateURL(cname))
                                                .openConnection()
                                                .getInputStream(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                String        l;
                while ((l = reader.readLine()) != null)
                    builder.append(l);
                reader.close();

                cid = Integer.parseInt(builder.toString());

                Utils.getMDB().insertChat(new Chat(cid, cname, Chat.ChatType.GROUP));
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
                    while (reader.readLine() != null)
                        ;
                    reader.close();
                    Utils.getMDB().insertAssoziation(assoziation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        private String generateURL(String cname) throws UnsupportedEncodingException {
            return Utils.BASE_URL + "messenger/addChat.php?key=5453&chatname=" + URLEncoder.encode(cname, "UTF-8") + "&chattype=" + Chat.ChatType.GROUP.toString().toLowerCase();
        }

        private String generateURL(Assoziation assoziation) {
            return Utils.BASE_URL + "messenger/addAssoziation.php?key=5453&userid=" + assoziation.uid + "&chatid=" + assoziation.cid;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);

            Utils.receiveMessenger();

            startActivity(new Intent(getApplicationContext(), ChatActivity.class)
                    .putExtra("cid", cid)
                    .putExtra("cname", cname)
                    .putExtra("ctype", Chat.ChatType.GROUP.toString()));
            finish();
        }
    }
}