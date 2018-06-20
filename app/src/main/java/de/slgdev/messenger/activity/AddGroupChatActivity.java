package de.slgdev.messenger.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.utility.User;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.messenger.task.CreateGroupChat;

public class AddGroupChatActivity extends ActionLogActivity {
    private final User[] users = Utils.getController().getMessengerDatabase().getUsers();
    private LinearLayout container;
    private EditText     etChatname;
    private MenuItem     confirm;
    private boolean[]    selection;
    private int          selected;
    private int          cid;

    private boolean chatnameSet, usersSelected;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_add_chat);
        Utils.getController().registerAddGroupChatActivity(this);

        initToolbar();
        initContainer();
        initEditText();
        initSearch();

        chatnameSet = false;
        usersSelected = false;
    }

    @Override
    protected String getActivityTag() {
        return "AddGroupChatActivity";
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
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            new CreateGroupChat(this, etChatname.getText().toString()).execute(getSelected());
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerAddGroupChatActivity(null);
    }

    private Integer[] getSelected() {
        int n = 0;
        for (boolean b : selection) {
            if (b) {
                n++;
            }
        }
        Integer[] selected = new Integer[n];
        n = 0;
        for (int i = 0; i < users.length; i++) {
            if (selection[i]) {
                selected[n] = users[i].uid;
                n++;
            }
        }
        return selected;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(R.string.title_new_groupchat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initEditText() {
        etChatname = findViewById(R.id.editTextChatName);
        etChatname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                chatnameSet = s.toString().length() > 0;
                confirm.setVisible(chatnameSet && usersSelected);
            }
        });
    }

    private void initSearch() {
        EditText search = findViewById(R.id.editTextSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                fitContainer(s.toString());
            }
        });
    }

    private void initContainer() {
        container = findViewById(R.id.linearLayoutUsers);
        container.removeAllViews();

        for (int i = 0; i < users.length; i++) {
            User u = users[i];
            View v = getLayoutInflater().inflate(R.layout.list_item_user, null);

            final TextView username    = v.findViewById(R.id.username);
            final TextView userdefault = v.findViewById(R.id.userdefault);

            username.setText(u.uname);
            userdefault.setText(u.udefaultname + ", " + u.ustufe);

            v.findViewById(R.id.checkBox).setVisibility(View.GONE);

            final int finalI = i;
            v.setOnClickListener(v1 -> {
                selection[finalI] = !selection[finalI];
                v1.setSelected(selection[finalI]);

                if (selection[finalI])
                    selected++;
                else
                    selected--;

                usersSelected = (selected > 0);
                confirm.setVisible(chatnameSet && usersSelected);
            });

            container.addView(v);
        }

        selected = 0;
        selection = new boolean[users.length];
    }

    private void fitContainer(String search) {
        search = search.toLowerCase();
        for (int i = 0; i < container.getChildCount(); i++) {
            User u = users[i];
            if (u.udefaultname.toLowerCase().contains(search) || u.uname.toLowerCase().contains(search)) {
                container.getChildAt(i).setVisibility(View.VISIBLE);
            } else {
                container.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}