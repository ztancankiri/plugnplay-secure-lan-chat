package com.ztan.chatapp.discovery;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RequestListener extends Thread {

    private String subnetPrefix;
    private AtomicBoolean isActive;
    private List<InetAddress> broadcastAddresses;
    private int serverPort;

    public RequestListener(String subnetPrefix, int serverPort) {
        this.subnetPrefix = subnetPrefix;
        this.isActive = new AtomicBoolean(false);
        this.broadcastAddresses = findBroadcasAddresses();
        this.serverPort = serverPort;
    }

    @Override
    public synchronized void start() {
        isActive.set(true);
        super.start();
    }

    public synchronized void close() {
        isActive.set(false);
        interrupt();
    }

    @Override
    public void run() {
        while (isActive.get()) {
            try {
                if (receiveRequestPacket(DiscoveryConfig.DISCOVERY_REQUEST_PORT)) {
                    sendResponsePacket(DiscoveryConfig.DISCOVERY_RESPONSE_PORT);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String findLocalIP() throws SocketException {
        List<String> localIPs = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                continue;
            }

            networkInterface.getInterfaceAddresses()
                    .stream()
                    .map(InterfaceAddress::getAddress)
                    .filter(Objects::nonNull)
                    .map(InetAddress::getHostAddress)
                    .forEach(localIPs::add);
        }

        for (String ip : localIPs) {
            if (ip.contains(subnetPrefix)) {
                return ip;
            }
        }

        return null;
    }

    private void sendResponsePacket(int port) throws IOException {
        String localIP = findLocalIP();

        if (localIP == null) {
            return;
        }

        DiscoveryPacket response = new DiscoveryPacket(findLocalIP(), serverPort);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] responseBytes = byteArrayOutputStream.toByteArray();

        DatagramSocket responseSocket = new DatagramSocket();
        for (InetAddress address : broadcastAddresses) {
            DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, address, port);
            responseSocket.send(responsePacket);
        }
        responseSocket.close();
    }

    private boolean receiveRequestPacket(int port) throws IOException, ClassNotFoundException {
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        DatagramSocket requestSocket = new DatagramSocket(port);
        requestSocket.receive(packet);
        requestSocket.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        DiscoveryPacket discoveryPacket = (DiscoveryPacket) objectInputStream.readObject();

        if (discoveryPacket.type == DiscoveryType.REQUEST) {
            return true;
        }

        return false;
    }

    private List<InetAddress> findBroadcasAddresses() {
        try {
            List<InetAddress> broadcastAddresses = new ArrayList<>();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                networkInterface.getInterfaceAddresses()
                        .stream()
                        .map(InterfaceAddress::getBroadcast)
                        .filter(Objects::nonNull)
                        .forEach(broadcastAddresses::add);
            }

            return broadcastAddresses;
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

}
