package de.slgdev.messenger.network;

import de.slgdev.leoapp.service.ReceiveService;
import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.activity.AddGroupChatActivity;
import de.slgdev.messenger.activity.ChatActivity;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketListener extends WebSocketListener {
    private ReceiveService receiveService;
    private MessageHandler messageHandler;

    public SocketListener(ReceiveService receiveService, MessageHandler messageHandler) {
        this.receiveService = receiveService;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Utils.logDebug(response.headers().toString());
        Utils.logDebug(response.message());
        receiveService.setSocketRunning(true);
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        if (message.startsWith("+OK id")) {
            int cid = Integer.parseInt(
                    message.substring(6)
            );
            ChatActivity chatActivity = Utils.getController().getChatActivity();
            if (chatActivity != null) {
                chatActivity.setCid(cid);
            }
            AddGroupChatActivity addGroupChatActivity = Utils.getController().getAddGroupChatActivity();
            if (addGroupChatActivity != null) {
                addGroupChatActivity.setCid(cid);
            }
        } else {
            messageHandler.append(message);
        }
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Utils.logDebug("Socket closed!");
        receiveService.setSocketRunning(false);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if (response != null) {
            Utils.logDebug(response.headers().toString());
            Utils.logDebug(response.message());
        }
        Utils.logError(t);
        receiveService.setSocketRunning(false);
    }
}