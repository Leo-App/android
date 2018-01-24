package de.slgdev.leoapp.service;

import de.slgdev.leoapp.utility.Utils;
import de.slgdev.messenger.activity.AddGroupChatActivity;
import de.slgdev.messenger.activity.ChatActivity;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

class SocketListener extends WebSocketListener {
    private ReceiveService receiveService;
    private MessageHandler messageHandler;

    SocketListener(ReceiveService receiveService, MessageHandler messageHandler) {
        this.receiveService = receiveService;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Utils.logDebug("Socket opened!");
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
        Utils.logError("Socket Error");
        Utils.logError(t);
        receiveService.setSocketRunning(false);
    }
}