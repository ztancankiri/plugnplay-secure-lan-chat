package com.ztan.chatapp;

import com.ztan.chatapp.security.AES;
import com.ztan.chatapp.security.EncryptedMessage;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketClient extends Thread {

    private AtomicBoolean isActive;
    private SocketDataListener dataListener;

    private String IP;
    private Socket socket;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;

    public SocketClient(String IP, int port, SocketDataListener dataListener) {
        isActive = new AtomicBoolean(false);
        this.dataListener = dataListener;

        try {
            socket = new Socket(IP, port);

            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (SocketException e) {
            System.err.println("Connection Problem!");
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SocketClient(Socket socket, SocketDataListener dataListener) {
        isActive = new AtomicBoolean(false);
        this.dataListener = dataListener;
        this.socket = socket;

        try {
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (SocketException e) {
            System.err.println("Connection Problem!");
        } catch (UnknownHostException e) {
            System.err.println("Unknown Host!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        isActive.set(true);
        super.start();
    }

    public synchronized void close() {
        isActive.set(false);

        try {
            objectInputStream.close();
            inputStream.close();
            objectOutputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        interrupt();
    }

    @Override
    public void run() {
        while (isActive.get()) {
            try {
                EncryptedMessage message = (EncryptedMessage) objectInputStream.readObject();

                if (message != null) {
                    receiveData(message);
                }
                else {
                    System.out.println("Message is null!");
                }
            } catch (SocketException e) {
                closeConnection();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void receiveData(EncryptedMessage message) {
        dataListener.onDataReceived(this, message);
    }

    private void openConnection() {
        dataListener.onConnectionOpened(this);
    }

    private void closeConnection() {
        dataListener.onConnectionClosed(this);
    }

    public synchronized void send(EncryptedMessage message) {
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void send(String passphrase, String fromName, String text) {
        EncryptedMessage encryptedMessage = AES.encrypt(passphrase, new Message(socket.getInetAddress().getHostAddress(), fromName, text));
        send(encryptedMessage);
    }

    public synchronized void send(String passphrase, String fromName, byte[] data) {
        EncryptedMessage encryptedMessage = AES.encrypt(passphrase, new Message(socket.getInetAddress().getHostAddress(), fromName, data));
        send(encryptedMessage);
    }

    public String getIP() {
        return IP;
    }
}
