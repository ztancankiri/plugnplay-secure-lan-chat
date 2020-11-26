package com.ztan.chatapp.client;

import com.ztan.chatapp.Message;
import com.ztan.chatapp.MessageType;
import com.ztan.chatapp.SocketClient;
import com.ztan.chatapp.SocketDataListener;
import com.ztan.chatapp.security.AES;
import com.ztan.chatapp.security.EncryptedMessage;

public class Application implements SocketDataListener, IGUIInteractor {

    private GUI gui;
    private SocketClient socketClient;

    public Application(String applicationName, String IP, int port) {
        socketClient = new SocketClient(IP, port, this);
        socketClient.start();
        gui = new GUI(applicationName, this);
    }

    @Override
    public void onConnectionOpened(SocketClient client) {
        System.out.println("Connected to the char server...");
    }

    @Override
    public void onDataReceived(SocketClient client, EncryptedMessage message) {
        Message plainMessage = AES.decrypt(gui.getPassphrase(), message);

        if (plainMessage != null && plainMessage.type == MessageType.TEXT) {
            gui.showMessage(plainMessage.fromName, plainMessage.text);
        }
    }

    @Override
    public void onConnectionClosed(SocketClient client) {
        System.err.println("Socket connection is closed!");
        gui.showPopup("Connection!", "Connection to the server is lost!");
        System.exit(1);
    }

    @Override
    public void sendMessage(String passphrase, String nickname, String message) {
        socketClient.send(passphrase, nickname, message);
    }
}
