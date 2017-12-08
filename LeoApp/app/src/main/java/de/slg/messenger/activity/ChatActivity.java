package de.slg.messenger.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.slg.leoapp.R;
import de.slg.leoapp.utility.Utils;
import de.slg.leoapp.view.ActionLogActivity;
import de.slg.messenger.MessageAdapter;
import de.slg.messenger.utility.Chat;
import de.slg.messenger.utility.Message;

public class ChatActivity extends ActionLogActivity {
    private int           cid;
    private String        cname;
    private Chat.ChatType ctype;
    private Message[]     messagesArray;
    private boolean[]     selected;
    private boolean       hasSelected;

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
        de.slg.messenger.utility.Utils.setCurrentlyDisplayedChat(cid);
    }

    @Override
    protected void onPause() {
        super.onPause();
        de.slg.messenger.utility.Utils.setCurrentlyDisplayedChat(-1);
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

        longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
            }
        };

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = rvMessages.getChildLayoutPosition(v);
                if (hasSelected && messagesArray[index].mdate.getTime() > 0) {
                    v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentTransparent));
                    selected[index] = true;
                    v.setOnClickListener(disableListener);
                }
            }
        };

        disableListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
                selected[rvMessages.getChildLayoutPosition(v)] = false;
                v.setOnLongClickListener(longClickListener);
                v.setOnClickListener(clickListener);
                setHasSelected();
            }
        };

        rvMessages = (RecyclerView) findViewById(R.id.recyclerViewMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMessages.setLayoutManager(layoutManager);
        refreshUI(true, true);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.actionBarChat);
        toolbar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        toolbar.setTitle(cname);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (ctype != Chat.ChatType.PRIVATE && Utils.getController().getMessengerDatabase().userInChat(Utils.getUserID(), cid)) {
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getApplicationContext(), ChatEditActivity.class)
                            .putExtra("cid", cid)
                            .putExtra("cname", cname), 1);
                }
            });
        }
    }

    private void initSendMessage() {
        etMessage = (EditText) findViewById(R.id.inputMessage);

        ImageButton sendButton = (ImageButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        if (ctype == Chat.ChatType.GROUP && !Utils.getController().getMessengerDatabase().userInChat(Utils.getUserID(), cid)) {
            etMessage.setEnabled(false);
            etMessage.setHint("Du bist nicht in diesem Chat!");
            sendButton.setEnabled(false);
        }
    }

    private String getMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                message = etMessage.getText().toString();
            }
        });

        while (message.length() > 0 && message.charAt(0) == ' ')
            message = message.substring(1);

        while (message.length() > 0 && message.charAt(message.length() - 1) == ' ')
            message = message.substring(0, message.length() - 1);

        return message;
    }

    private void sendMessage() {
        String message = getMessage();
        if (message.length() > 0) {
            new SendMessage().execute(message);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.swapAdapter(new MessageAdapter(getApplicationContext(), messagesArray, clickListener, longClickListener, selected, ctype), false);
                if (scroll)
                    rvMessages.scrollToPosition(messagesArray.length - 1);
            }
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

    private class SendMessage extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            etMessage.setText("");
        }

        @Override
        protected Void doInBackground(String... params) {
            if (cid == -1) {
                int oUid = getIntent().getIntExtra("uid", -1);
                if (oUid == -1)
                    return null;
                if (Utils.checkNetwork()) {
                    try {
                        URLConnection connection = new URL(Utils.BASE_URL_PHP + "messenger/addChat.php?key=5453&chatname=" + Utils.getUserID() + "+-+" + oUid + "&chattype=" + Chat.ChatType.PRIVATE.toString().toLowerCase())
                                .openConnection();

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                connection.getInputStream(), "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        String        l;
                        while ((l = reader.readLine()) != null)
                            builder.append(l);
                        reader.close();
                        cid = Integer.parseInt(builder.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "You need an active Internet-Connection to perform this Action", Toast.LENGTH_LONG).show();
                }
            }

            if (cid != -1) {
                if (!Utils.checkNetwork()) {
                    Utils.getController().getMessengerDatabase().enqueueMessage(params[0], cid);
                    refreshUI(true, true);
                } else {
                    Message[] mOld = messagesArray;
                    messagesArray = new Message[mOld.length + 1];
                    System.arraycopy(mOld, 0, messagesArray, 0, mOld.length);
                    messagesArray[mOld.length] = new Message(params[0]);
                    refreshUI(false, true);

                    try {
                        URLConnection connection = new URL(generateURL(params[0]))
                                .openConnection();

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                connection.getInputStream(), "UTF-8"));
                        String line;
                        while ((line = reader.readLine()) != null)
                            Log.e("TAG", line);
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private String generateURL(String message) throws UnsupportedEncodingException {
            message = URLEncoder.encode(message, "UTF-8");
            String key      = de.slg.messenger.utility.Utils.Verschluesseln.createKey(message);
            String vMessage = de.slg.messenger.utility.Utils.Verschluesseln.encrypt(message, key);
            String vKey     = de.slg.messenger.utility.Utils.Verschluesseln.encryptKey(key);
            return Utils.BASE_URL_PHP + "messenger/addMessageEncrypted.php?&uid=" + Utils.getUserID() + "&message=" + vMessage + "&cid=" + cid + "&vKey=" + vKey;
        }
    }
}