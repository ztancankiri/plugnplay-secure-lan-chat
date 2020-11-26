package com.ztan.chatapp.server;

import com.ztan.chatapp.SocketClient;
import com.ztan.chatapp.SocketDataListener;
import com.ztan.chatapp.security.EncryptedMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketServer extends Thread implements SocketDataListener {

    private AtomicBoolean isActive;
    private List<SocketClient> clients;
    private ServerSocket serverSocket;
    private int port;

    public SocketServer(int port) {
        isActive = new AtomicBoolean(false);
        this.port = port;
        clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (SocketException e) {
            System.err.println("Error on opening ServerSocket!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        isActive.set(true);
        super.start();
        System.out.println("Chat Server started running on port " + port + "...");
    }

    public synchronized void close() {
        isActive.set(false);

        for (SocketClient client : clients) {
            client.close();
        }

        try {
            serverSocket.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isActive.get()) {
            try {
                Socket newConnection = serverSocket.accept();
                SocketClient newClient = new SocketClient(newConnection, this);
                clients.add(newClient);
                newClient.start();
            }
            catch (SocketException e) {
                System.err.println("Error while accepting new client!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionOpened(SocketClient client) {
        System.out.printf("Client from %s connected!\n", client.getIP());
    }

    @Override
    public void onDataReceived(SocketClient client, EncryptedMessage message) {
        for (SocketClient socketClient : clients) {
            socketClient.send(message);
        }
    }

    @Override
    public void onConnectionClosed(SocketClient client) {
        clients.remove(client);
        client.close();
        System.out.printf("Client from %s disconnected!\n", client.getIP());
    }
}
