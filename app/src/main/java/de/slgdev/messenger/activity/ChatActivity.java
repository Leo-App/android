package de.slgdev.messenger.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import de.slgdev.leoapp.R;
import de.slgdev.leoapp.Start;
import de.slgdev.leoapp.service.SocketService;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.leoapp.view.ActionLogActivity;
import de.slgdev.messenger.utility.Chat;
import de.slgdev.messenger.utility.Message;
import de.slgdev.messenger.utility.MessengerUtils;
import de.slgdev.messenger.view.MessageAdapter;

public class ChatActivity extends ActionLogActivity {
    private int           cid;
    private String        cname;
    private Chat.ChatType ctype;

    private Message[] messagesArray;
    private boolean[] selected;
    private boolean   hasSelected;

    private RecyclerView rvMessages;
    private EditText     etMessage;
    private String       message;

    private View.OnLongClickListener longClickListener;
    private View.OnClickListener     clickListener;
    private View.OnClickListener     disableListener;
    private MenuItem                 delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.getController().registerChatActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        cid = getIntent().getIntExtra("cid", -1);
        cname = getIntent().getStringExtra("cname");
        ctype = Chat.ChatType.valueOf(getIntent().getStringExtra("ctype"));

        messagesArray = new Message[0];

        initToolbar();
        initSendMessage();
        initRecyclerView();

        if (cid != -1) {
            Utils.getController().getMessengerDatabase().setMessagesRead(cid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_chat, menu);
        delete = menu.findItem(R.id.action_delete);
        delete.setVisible(hasSelected);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_delete) {
            deleteSelectedMessages();
            delete.setVisible(false);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.getController().registerChatActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI(false, true);
        MessengerUtils.setCurrentlyDisplayedChat(cid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MessengerUtils.setCurrentlyDisplayedChat(-1);
        if (cid != -1) {
            Utils.getController().getMessengerDatabase().setMessagesRead(cid);
        }
    }

    @Override
    protected String getActivityTag() {
        return "ChatActivity";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            getSupportActionBar().setTitle(data.getStringExtra("cname"));
        }
    }

    private void initRecyclerView() {
        selected = new boolean[messagesArray.length];
        hasSelected = false;

        longClickListener = v -> {
            int index = rvMessages.getChildLayoutPosition(v);
            if (messagesArray[index].mdate.getTime() > 0) {
                hasSelected = true;
                delete.setVisible(true);
                v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentTransparent));
                selected[index] = true;
                v.setOnClickListener(disableListener);
                return true;
            }
            return false;
        };

        clickListener = v -> {
            int index = rvMessages.getChildLayoutPosition(v);
            if (hasSelected && messagesArray[index].mdate.getTime() > 0) {
                v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentTransparent));
                selected[index] = true;
                v.setOnClickListener(disableListener);
            }
        };

        disableListener = v -> {
            v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
            selected[rvMessages.getChildLayoutPosition(v)] = false;
            v.setOnLongClickListener(longClickListener);
            v.setOnClickListener(clickListener);
            setHasSelected();
        };

        rvMessages = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMessages.setLayoutManager(layoutManager);
        refreshUI(true, true);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(cname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (ctype != Chat.ChatType.PRIVATE && Utils.getController().getMessengerDatabase().userInChat(Utils.getUserID(), cid)) {
            toolbar.setOnClickListener(v -> startActivityForResult(new Intent(getApplicationContext(), ChatEditActivity.class)
                    .putExtra("cid", cid)
                    .putExtra("cname", cname), 1));
        }
    }

    private void initSendMessage() {
        etMessage = findViewById(R.id.inputMessage);

        ImageButton sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(view -> sendMessage());

        if (ctype == Chat.ChatType.GROUP && !Utils.getController().getMessengerDatabase().userInChat(Utils.getUserID(), cid)) {
            etMessage.setEnabled(false);
            etMessage.setHint(R.string.not_in_chat);
            sendButton.setEnabled(false);
        }
    }

    private String getMessage() {
        runOnUiThread(() -> message = etMessage.getText().toString());

        while (message.length() > 0 && message.charAt(0) == ' ')
            message = message.substring(1);

        while (message.length() > 0 && message.charAt(message.length() - 1) == ' ')
            message = message.substring(0, message.length() - 1);

        return message;
    }

    private void sendMessage() {
        String message = getMessage();
        if (message.length() > 0) {
            new SendMessage(this).execute(message);
        }
    }

    public void refreshUI(boolean refreshArray, final boolean scroll) {
        if (refreshArray) {
            messagesArray = Utils.getController().getMessengerDatabase().getMessagesFromChat(cid);
        }

        if (messagesArray.length != selected.length) {
            boolean[] sOld = selected;
            selected = new boolean[messagesArray.length];
            System.arraycopy(sOld, 0, selected, 0, sOld.length > selected.length ? selected.length : sOld.length);
        }

        runOnUiThread(() -> {
            rvMessages.swapAdapter(new MessageAdapter(getApplicationContext(), messagesArray, clickListener, longClickListener, selected, ctype), false);
            if (scroll)
                rvMessages.scrollToPosition(messagesArray.length - 1);
        });
    }

    private void setHasSelected() {
        hasSelected = false;
        for (boolean b : selected) {
            if (b) {
                hasSelected = true;
                break;
            }
        }
        delete.setVisible(hasSelected);
    }

    private void deleteSelectedMessages() {
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                if (messagesArray[i].mdate.getTime() > 0) {
                    Utils.getController().getMessengerDatabase().deleteMessage(messagesArray[i].mid);
                } else {
                    Utils.getController().getMessengerDatabase().deleteQueuedMessage(messagesArray[i].mid);
                }
                selected[i] = false;
            }
        }
        refreshUI(true, true);
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    private static class SendMessage extends AsyncTask<String, Void, Void> {
        private ChatActivity  activity;
        private SocketService service;

        private SendMessage(ChatActivity activity) {
            this.activity = activity;
            this.service = Utils.getController().getSocketService();

            if (service == null) {
                Start.startReceiveService();
                this.service = Utils.getController().getSocketService();
            }
        }

        @Override
        protected void onPreExecute() {
            activity.etMessage.setText("");
        }

        @Override
        protected Void doInBackground(String... params) {
            int oUid = activity.getIntent().getIntExtra("uid", -1);

            if (activity.cid == -1) {
                assert oUid != -1;

                if (Utils.isNetworkAvailable()) {

                    service.send(new Chat(0, oUid + " - " + Utils.getUserID(), Chat.ChatType.PRIVATE));

                    while (activity.cid == -1)
                        ;
                } else {
                    Toast.makeText(activity, R.string.need_internet, Toast.LENGTH_LONG).show();
                    return null;
                }
            }

            if (Utils.isNetworkAvailable()) {
                Message[] mOld = activity.messagesArray;
                activity.messagesArray = new Message[mOld.length + 1];
                System.arraycopy(mOld, 0, activity.messagesArray, 0, mOld.length);
                activity.messagesArray[mOld.length] = new Message(params[0]);
                activity.refreshUI(false, true);

                service.send(new Message(0, params[0], activity.cid));
            } else {
                Utils.getController().getMessengerDatabase().enqueueMessage(params[0], activity.cid);
                activity.refreshUI(true, true);
            }

            return null;
        }
    }
}