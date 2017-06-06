package de.slg.messenger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.slg.leoapp.R;
import de.slg.leoapp.User;
import de.slg.leoapp.Utils;

public class UserFragment extends Fragment {

    public View rootView;
    public ListView lvUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_overview, container, false);

        initListView();

        return rootView;
    }

    private void initListView() {
        lvUsers = (ListView) rootView.findViewById(R.id.listViewUser);
        lvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < Utils.getOverviewWrapper().userArray.length) {
                    User clickedUser = Utils.getOverviewWrapper().userArray[position];
                    ChatActivity.chatname = clickedUser.userName;
                    Chat newChat = new Chat(-1, "" + clickedUser.userId + " - " + Utils.getCurrentUser().userId, Chat.Chattype.PRIVATE);
                    int index = Utils.getOverviewWrapper().indexOf(newChat);
                    if (index == -1) {
                        new CreateChat(clickedUser).execute(newChat);
                        ChatActivity.chat = newChat;
                    } else {
                        ChatActivity.chat = Utils.getOverviewWrapper().chatArray[index];
                    }
                    ChatActivity.chatname = clickedUser.userName;
                    startActivity(new Intent(getContext(), ChatActivity.class));
                }
            }
        });
        lvUsers.setAdapter(new UserAdapter(getContext(), Utils.getOverviewWrapper().userArray, false));
    }

    public void refreshUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lvUsers.setAdapter(new UserAdapter(getContext(), Utils.getOverviewWrapper().userArray, false));
            }
        });
    }

    private class CreateChat extends AsyncTask<Chat, Void, Void> {

        private User other;

        CreateChat(User other) {
            this.other = other;
        }

        @Override
        protected Void doInBackground(Chat... params) {
            if (Utils.checkNetwork()) {
                sendChat(params[0]);
                sendAssoziation(new Assoziation(params[0].chatId, Utils.getUserID(), false));
                sendAssoziation(new Assoziation(params[0].chatId, other.userId, false));
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
                if (!erg.equals("error in chat"))
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
                    while (reader.readLine() != null){

                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return false;
        }

        private String generateURL(Chat chat) {
            String chatname = chat.chatName.replace(' ', '+');
            return "http://moritz.liegmanns.de/messenger/addChat.php?key=5453&chatname=" + chatname + "&chattype=" + Chat.Chattype.PRIVATE.toString().toLowerCase();
        }

        private String generateURL(Assoziation assoziation) {
            return "http://moritz.liegmanns.de/messenger/addUserToChat.php?key=5453&userid=" + assoziation.userID + "&chatid=" + assoziation.chatID;
        }
    }
}