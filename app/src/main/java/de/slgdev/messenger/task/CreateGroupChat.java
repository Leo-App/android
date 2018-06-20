package de.slgdev.messenger.task;

import android.content.Intent;
import android.os.AsyncTask;

import de.slgdev.leoapp.Start;
import de.slgdev.leoapp.service.SocketService;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.activity.AddGroupChatActivity;
import de.slgdev.messenger.activity.ChatActivity;
import de.slgdev.messenger.utility.Assoziation;
import de.slgdev.messenger.utility.Chat;

public class CreateGroupChat extends AsyncTask<Integer, Void, Intent> {
    private final String               cname;
    private final AddGroupChatActivity activity;
    private       SocketService        service;
    private       int                  cid;

    public CreateGroupChat(AddGroupChatActivity activity, String cname) {
        this.activity = activity;
        this.cname = cname;
        this.cid = -1;
        this.service = Utils.getController().getSocketService();

        if (service == null) {
            Start.startReceiveService();
            this.service = Utils.getController().getSocketService();
        }
    }

    @Override
    protected Intent doInBackground(Integer... params) {
        service.send(new Chat(cid, cname, Chat.ChatType.GROUP));

        while ((cid = activity.getCid()) == -1)
            ;

        service.send(new Assoziation(cid, Utils.getUserID()));
        for (Integer i : params) {
            service.send(new Assoziation(cid, i));
        }

        return new Intent(activity, ChatActivity.class)
                .putExtra("cid", cid)
                .putExtra("cname", cname)
                .putExtra("ctype", Chat.ChatType.GROUP.toString());
    }

    @Override
    protected void onPostExecute(Intent intent) {
        activity.startActivity(intent);
        activity.finish();
    }
}
