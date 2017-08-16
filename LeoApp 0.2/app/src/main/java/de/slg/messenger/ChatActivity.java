package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.GraphicUtils;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class ChatActivity extends AppCompatActivity {
    private int cid;
    private String cname;
    private Chat.Chattype ctype;
    private Message[] messagesArray;
    private boolean[] selected;
    private boolean hasSelected;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private String message;

    private View.OnLongClickListener longClickListener;
    private View.OnClickListener clickListener;
    private View.OnClickListener disableListener;
    private MenuItem delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.registerChatActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        cid = getIntent().getIntExtra("cid", -1);
        cname = getIntent().getStringExtra("cname");
        ctype = Chat.Chattype.valueOf(getIntent().getStringExtra("ctype"));

        messagesArray = new Message[0];
        if (cid != -1)
            Utils.receiveMessenger();

        initToolbar();
        initSendMessage();
        initRecyclerView();

        if (cid != -1)
            Utils.getMDB().setMessagesRead(cid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_chat, menu);
        menu.findItem(R.id.action_chat_info).setVisible(ctype != Chat.Chattype.PRIVATE && Utils.getMDB().userInChat(Utils.getUserID(), cid));
        delete = menu.findItem(R.id.action_delete);
        delete.setVisible(hasSelected);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_chat_info) {
            startActivityForResult(new Intent(getApplicationContext(), ChatEditActivity.class)
                    .putExtra("cid", cid)
                    .putExtra("cname", cname), 1);
        } else if (item.getItemId() == R.id.action_delete) {
            deleteSelectedMessages();
            delete.setVisible(false);
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        Utils.registerChatActivity(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI(false, true);
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
        rvMessages.setVisibility(View.INVISIBLE);
        rvMessages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        refreshUI(true, true);
        rvMessages.setVisibility(View.VISIBLE);
    }

    private void initToolbar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarChat);
        actionBar.setTitle(cname);
        actionBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

        if (ctype == Chat.Chattype.GROUP && !Utils.getMDB().userInChat(Utils.getUserID(), cid)) {
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
            messagesArray = Utils.getMDB().getMessagesFromChat(cid);
        }
        if (messagesArray.length != selected.length) {
            boolean[] sOld = selected;
            selected = new boolean[messagesArray.length];
            System.arraycopy(sOld, 0, selected, 0, sOld.length > selected.length ? selected.length : sOld.length);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.swapAdapter(new MessageAdapter(), false);
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
                Utils.getMDB().deleteMessage(messagesArray[i].mid);
                selected[i] = false;
            }
        }
        refreshUI(true, true);
    }

    private class MessageAdapter extends RecyclerView.Adapter {
        MessageAdapter() {
            super();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Message current = messagesArray[position];

            final View v = holder.itemView;
            final TextView datum = (TextView) v.findViewById(R.id.textViewDate);
            final TextView nachricht = (TextView) v.findViewById(R.id.nachricht);
            final TextView absender = (TextView) v.findViewById(R.id.absender);
            final TextView uhrzeit = (TextView) v.findViewById(R.id.datum);
            final LinearLayout layout = (LinearLayout) v.findViewById(R.id.chatbubblewrapper);
            final View chatbubble = v.findViewById(R.id.chatbubble);
            final View space = v.findViewById(R.id.space);
            final View progressbar = v.findViewById(R.id.progressBar);

            nachricht.setText(current.mtext);
            absender.setText(current.uname);
            uhrzeit.setText(current.getTime());
            datum.setText(current.getDate());

            final boolean mine = current.uid == Utils.getUserID();
            if (mine) {
                layout.setGravity(Gravity.RIGHT);
                absender.setVisibility(View.GONE);
                nachricht.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.background_light));
                uhrzeit.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.background_light));
            } else {
                layout.setGravity(Gravity.LEFT);
                nachricht.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.background_dark));
                uhrzeit.setTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.background_dark));
                if (ctype == Chat.Chattype.PRIVATE) {
                    absender.setVisibility(View.GONE);
                } else {
                    absender.setVisibility(View.VISIBLE);
                }
            }
            chatbubble.setEnabled(mine);

            final boolean send = uhrzeit.getText().toString().equals("");
            if (send) {
                uhrzeit.setVisibility(View.GONE);
                progressbar.setVisibility(View.VISIBLE);
            } else {
                uhrzeit.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
            }

            final boolean first = position == 0 || !gleicherTag(current.mdate, messagesArray[position - 1].mdate);
            if (first) {
                datum.setVisibility(View.VISIBLE);
            } else {
                datum.setVisibility(View.GONE);
                if (current.uid == messagesArray[position - 1].uid) {
                    absender.setVisibility(View.GONE);
                    space.setVisibility(View.GONE);
                }
            }

            if (selected[position]) {
                v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccentTransparent));
            } else {
                v.findViewById(R.id.chatbubblewrapper).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
            }
        }

        @Override
        public int getItemCount() {
            return messagesArray.length;
        }

        private boolean gleicherTag(Date pDate1, Date pDate2) {
            Calendar c1 = new GregorianCalendar(), c2 = new GregorianCalendar();
            c1.setTime(pDate1);
            c2.setTime(pDate2);
            return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder() {
                super(getLayoutInflater().inflate(R.layout.list_item_message, null));
                TextView nachricht = (TextView) itemView.findViewById(R.id.nachricht);
                nachricht.setMaxWidth(GraphicUtils.getDisplayWidth() * 2 / 3);
                itemView.setOnLongClickListener(longClickListener);
                itemView.setOnClickListener(clickListener);
            }
        }
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
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new URL("http://moritz.liegmanns.de/messenger/addChat.php?key=5453&chatname=" + Utils.getUserID() + "+-+" + oUid + "&ctype=" + Chat.Chattype.PRIVATE.toString().toLowerCase())
                                                        .openConnection()
                                                        .getInputStream(), "UTF-8"));
                        String erg = "";
                        String l;
                        while ((l = reader.readLine()) != null)
                            erg += l;
                        reader.close();
                        cid = Integer.parseInt(erg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Message[] mOld = messagesArray;
            messagesArray = new Message[mOld.length + 1];
            System.arraycopy(mOld, 0, messagesArray, 0, mOld.length);
            messagesArray[mOld.length] =
                    new Message(0,
                            params[0],
                            0,
                            cid,
                            Utils.getUserID(),
                            true);
            if (!Utils.checkNetwork()) {
                Utils.getMDB().insertUnsendMessage(params[0], cid);
                refreshUI(true, true);
            } else {
                messagesArray[messagesArray.length - 1].mdate = new Date();
                messagesArray[messagesArray.length - 1].sending = true;
                refreshUI(false, true);
                try {
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            new URL(generateURL(params[0]))
                                                    .openConnection()
                                                    .getInputStream(), "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) Log.e("TAG", line);
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Utils.receiveMessenger();
        }

        private String generateURL(String message) throws UnsupportedEncodingException {
            message = URLEncoder.encode(message, "UTF-8");

            String key = Verschluesseln.createKey(message);
            String vMessage = Verschluesseln.encrypt(message, key);
            String vKey = Verschluesseln.encryptKey(key);
            return "http://moritz.liegmanns.de/messenger/addMessageEncrypted.php?key=5453&userid=" + Utils.getUserID() + "&message=" + vMessage + "&chatid=" + cid + "&vKey=" + vKey;
        }
    }
}