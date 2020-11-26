package com.ztan.chatapp;

import com.ztan.chatapp.security.EncryptedMessage;

public interface SocketDataListener {

    public void onConnectionOpened(SocketClient client);
    public void onDataReceived(SocketClient client, EncryptedMessage message);
    public void onConnectionClosed(SocketClient client);

}
