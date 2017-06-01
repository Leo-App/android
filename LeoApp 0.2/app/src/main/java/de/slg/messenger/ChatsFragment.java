package de.slg.messenger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.slg.leoapp.R;
import de.slg.leoapp.User;

public class ChatsFragment extends Fragment {

    public View rootView;
    public static OverviewWrapper wrapper;

    public ListView lvChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat_overview, container, false);

        initListView();

        wrapper.chatsDone = true;

        return rootView;
    }

    private void initListView() {
        wrapper.chatArray = wrapper.dbConnection.getChats();

        lvChats = (ListView) rootView.findViewById(R.id.listViewChats);
        lvChats.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < wrapper.chatArray.length) {
                    ChatActivity.chatname = wrapper.chatArray[position].chatVisibleName;
                    ChatActivity.chat = wrapper.chatArray[position];
                    ChatActivity.currentUser = wrapper.currentUser;
                    ChatActivity.wrapper = wrapper;
                    startActivity(new Intent(getContext(), ChatActivity.class));
                }
            }
        });
        lvChats.setAdapter(new ChatAdapter(wrapper.getApplicationContext(), wrapper.chatArray, wrapper));
        wrapper.lvChats = lvChats;
    }

    public void refreshUI() {
        wrapper.chatArray = wrapper.dbConnection.getChats();
        wrapper.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lvChats.setAdapter(new ChatAdapter(getContext(), wrapper.chatArray, wrapper));
            }
        });
    }

    class ChatAdapter extends ArrayAdapter<Chat> {

        private Context context;
        private int resId;
        private Chat[] chats;
        private User currentUser;
        private OverviewWrapper wrapper;

        public ChatAdapter(Context context, Chat[] chats, OverviewWrapper wrapper) {
            super(context, R.layout.list_item_chat, chats);
            this.context = context;
            this.resId = R.layout.list_item_chat;
            this.chats = chats;
            this.currentUser = wrapper.currentUser;
            this.wrapper = wrapper;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(resId, null);
            }
            TextView chatname = (TextView) v.findViewById(R.id.chatname);
            TextView lastMessage = (TextView) v.findViewById(R.id.letzteNachricht);
            ImageView icon = (ImageView) v.findViewById(R.id.icon);
            ImageView notify = (ImageView) v.findViewById(R.id.notify);
            if (position < chats.length && chats[position] != null) {
                if (chats[position].chatTyp == Chat.Chattype.GROUP) {
                    chatname.setText(chats[position].chatName);
                    chats[position].chatVisibleName = chats[position].chatName;
                } else {
                    String[] s = chats[position].chatName.split(" - ");
                    int idO;
                    if (currentUser.userId == Integer.parseInt(s[0]))
                        idO = Integer.parseInt(s[1]);
                    else
                        idO = Integer.parseInt(s[0]);
                    User o = wrapper.findUser(idO);
                    if (o != null) {
                        chatname.setText(o.userName);
                        chats[position].chatVisibleName = o.userName;
                    }
                }
                if (chats[position].letzeNachricht != null)
                    lastMessage.setText(chats[position].letzeNachricht.toString());
                if (chats[position].chatTyp == Chat.Chattype.PRIVATE)
                    icon.setImageResource(R.drawable.ic_chat_bubble_white_24dp);
                if (chats[position].chatTyp == Chat.Chattype.GROUP)
                    icon.setImageResource(R.drawable.ic_question_answer_white_24dp);
                if (chats[position].letzeNachricht != null && chats[position].letzeNachricht.senderId != currentUser.userId && !chats[position].letzeNachricht.read)
                    notify.setVisibility(View.VISIBLE);
            }
            return v;
        }
    }
}