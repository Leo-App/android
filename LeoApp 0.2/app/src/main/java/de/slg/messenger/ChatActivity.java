package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.slg.leoapp.List;
import de.slg.leoapp.R;
import de.slg.leoapp.Utils;

public class ChatActivity extends AppCompatActivity {
    public static Chat currentChat;
    private Message[] messagesArray;

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Snackbar snackbar;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.registerChatActivity(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messagesArray = new Message[0];
        Utils.receive();

        initToolbar();
        initSendMessage();
        initRecyclerView();
        initSnackbar();

        Utils.getMessengerDBConnection().setMessagesRead(currentChat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messenger_chat, menu);
        if (currentChat.ctype == Chat.Chattype.PRIVATE || !Utils.getMessengerDBConnection().isUserInChat(Utils.getCurrentUser(), currentChat))
            menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_edtiParticipants) {
            startEditChat();
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
        getSupportActionBar().setTitle(currentChat.cname);
    }

    private void initRecyclerView() {
        rvMessages = (RecyclerView) findViewById(R.id.recyclerViewMessages);
        rvMessages.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        refreshUI(true, true);
    }

    private void initToolbar() {
        String chatname = currentChat.cname;
        if (currentChat.ctype == Chat.Chattype.PRIVATE) {
            String[] split = currentChat.cname.split(" - ");
            if (split[0].equals("" + Utils.getUserID()))
                chatname = Utils.getMessengerDBConnection().getUname(Integer.parseInt(split[1]));
            else
                chatname = Utils.getMessengerDBConnection().getUname(Integer.parseInt(split[0]));
        }
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBarChat);
        actionBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        actionBar.setTitle(chatname);
        setSupportActionBar(actionBar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initSendMessage() {
        etMessage = (EditText) findViewById(R.id.inputMessage);

        FloatingActionButton sendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        if (!Utils.getMessengerDBConnection().isUserInChat(Utils.getCurrentUser(), currentChat)) {
            etMessage.setEnabled(false);
            etMessage.setHint("Du bist nicht mehr in diesem Chat!");
            sendButton.setEnabled(false);
        }
    }

    private void initSnackbar() {
        snackbar = Snackbar
                .make(findViewById(R.id.coordinatorLayout),
                        "Something went wrong! Please restart the app",
                        Snackbar.LENGTH_LONG)
                .setActionTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
                .setAction(getString(R.string.snackbar_undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
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
        if (message.length() > 0 && currentChat != null) {
            new SendMessage().execute(message);
            etMessage.setText("");
            Utils.receive();
        }
    }

    private void startEditChat() {
        ChatEditActivity.currentChat = currentChat;
        startActivity(new Intent(getApplicationContext(), ChatEditActivity.class));
    }

    public void refreshUI(boolean refreshMessages, final boolean scroll) {
        if (refreshMessages)
            messagesArray = Utils.getMessengerDBConnection().getMessagesFromChat(currentChat, true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rvMessages.swapAdapter(new MessageAdapter(), false);
                if (scroll)
                    rvMessages.scrollToPosition(messagesArray.length - 1);
            }
        });
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class MessageAdapter extends RecyclerView.Adapter<ViewHolder> {
        private Chat.Chattype chattype;
        private LayoutInflater inflater;
        private TextView nachricht, absender, uhrzeit, datum;

        MessageAdapter() {
            super();
            this.inflater = getLayoutInflater();
            this.chattype = currentChat.ctype;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.list_item_message, null));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message current = messagesArray[position];
            View v = holder.itemView;
            boolean first = position == 0 || !gleicherTag(current.mdate, messagesArray[position - 1].mdate);
            boolean mine = current.uid == Utils.getUserID();
            if (mine) {
                LinearLayout l1 = (LinearLayout) v.findViewById(R.id.wrapperlayout1);
                LinearLayout l2 = (LinearLayout) v.findViewById(R.id.wrapperlayout2);
                l1.setGravity(Gravity.END);
                l2.setGravity(Gravity.END);
                v.findViewById(R.id.wrapperlayout3).setEnabled(true);
                v.findViewById(R.id.absender).setVisibility(View.GONE);
            } else {
                LinearLayout l1 = (LinearLayout) v.findViewById(R.id.wrapperlayout1);
                LinearLayout l2 = (LinearLayout) v.findViewById(R.id.wrapperlayout2);
                l1.setGravity(Gravity.START);
                l2.setGravity(Gravity.START);
                v.findViewById(R.id.wrapperlayout3).setEnabled(false);
                absender = (TextView) v.findViewById(R.id.absender);
                absender.setText(current.uname);
                if (chattype == Chat.Chattype.PRIVATE) {
                    v.findViewById(R.id.absender).setVisibility(View.GONE);
                } else {
                    v.findViewById(R.id.absender).setVisibility(View.VISIBLE);
                }
            }
            nachricht = (TextView) v.findViewById(R.id.nachricht);
            nachricht.setText(current.mtext);
            uhrzeit = (TextView) v.findViewById(R.id.datum);
            uhrzeit.setVisibility(View.VISIBLE);
            String time = current.getTime();
            if (time.equals("")) {
                uhrzeit.setVisibility(View.GONE);
                v.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            } else {
                uhrzeit.setVisibility(View.VISIBLE);
                v.findViewById(R.id.progressBar).setVisibility(View.GONE);
                uhrzeit.setText(current.getTime());
            }
            if (first) {
                datum = (TextView) v.findViewById(R.id.textViewDate);
                datum.setVisibility(View.VISIBLE);
                datum.setText(current.getDate());
            } else {
                v.findViewById(R.id.textViewDate).setVisibility(View.GONE);
                if (current.uid == messagesArray[position - 1].uid) {
                    v.findViewById(R.id.absender).setVisibility(View.GONE);
                    v.findViewById(R.id.space).setVisibility(View.GONE);
                }
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
    }

    private class SendMessage extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            if (currentChat.cid == -1) {
                snackbar.show();
            } else {
                List<Message> messageList = new List<>(messagesArray);
                messageList.append(
                        new Message(0,
                                params[0],
                                0,
                                currentChat.cid,
                                Utils.getUserID(),
                                true));
                messagesArray = messageList.fill(new Message[messageList.length()]);
                refreshUI(false, true);
                if (!Utils.checkNetwork()) {
                    Utils.getMessengerDBConnection().insertUnsendMessage(params[0], currentChat.cid);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Nachricht wird versendet, sobald du dich mit dem Internet verbindest",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    try {
                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                new URL(generateURL(params[0]))
                                                        .openConnection()
                                                        .getInputStream(), "UTF-8"));
                        while (reader.readLine() != null) ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        private String generateURL(String message) {
            return "http://moritz.liegmanns.de/messenger/addMessage.php?key=5453&userid=" + Utils.getUserID() + "&message=" + message.replace(" ", "%20").replace(System.getProperty("line.separator"), "%0A") + "&chatid=" + currentChat.cid;
        }
    }
}