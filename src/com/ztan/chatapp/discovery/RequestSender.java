package com.ztan.chatapp.discovery;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class RequestSender {

    public static String findServer(int timeout) throws IOException, ClassNotFoundException {
        sendRequestPacket(DiscoveryConfig.DISCOVERY_REQUEST_PORT);
        return receiveResponsePacket(DiscoveryConfig.DISCOVERY_RESPONSE_PORT, timeout);
    }

    private static List<InetAddress> findBroadcasAddresses() {
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

    private static void sendRequestPacket(int port) throws IOException {
        DiscoveryPacket request = new DiscoveryPacket();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        byte[] requestBytes = byteArrayOutputStream.toByteArray();

        DatagramSocket requestSocket = new DatagramSocket();
        for (InetAddress address : Objects.requireNonNull(findBroadcasAddresses())) {
            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, address, port);
            requestSocket.send(requestPacket);
        }
        requestSocket.close();
    }

    private static String receiveResponsePacket(int port, int timeout) throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket(port);
        socket.setSoTimeout(timeout);
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            socket.close();
            return null;
        }

        socket.close();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        DiscoveryPacket discoveryPacket = (DiscoveryPacket) objectInputStream.readObject();

        if (discoveryPacket != null && discoveryPacket.type == DiscoveryType.RESPONSE) {
            return discoveryPacket.IP + ":" + discoveryPacket.port;
        }

        return null;
    }

}
