package de.slg.leoapp;

import java.net.URI;

import javax.websocket.*;

/**
 * ChatServer Client
 *
 * @author Jiji_Sasidharan
 */
@ClientEndpoint
class WebSocketClient {

    private Session userSession = null;
    private MessageHandler messageHandler;

    WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(String message) {
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    interface MessageHandler {
        void handleMessage(String message);
    }
}