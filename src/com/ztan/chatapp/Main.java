package com.ztan.chatapp;

import com.ztan.chatapp.client.Application;
import com.ztan.chatapp.discovery.DiscoveryConfig;
import com.ztan.chatapp.discovery.RequestListener;
import com.ztan.chatapp.discovery.RequestSender;
import com.ztan.chatapp.server.SocketServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int discoveryTimeout = DiscoveryConfig.DISCOVERY_TIMEOUT;
        String subnetPrefix = DiscoveryConfig.DISCOVERY_SUBNET_PREFIX;

        if (args.length > 0) {
            try {
                discoveryTimeout = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Timeout should be an integer in milliseconds!");
                System.exit(1);
            }

            if (args.length > 1) {
                subnetPrefix = args[1];
                // TODO: Check if it is a valid IPv4 prefix.
            }
        }

        String server = RequestSender.findServer(discoveryTimeout);
        if (server == null) {
            startServer(subnetPrefix);

            while (server == null) {
                server = RequestSender.findServer(discoveryTimeout);
            }
        }

        String[] config = server.split(":");
        new Application("ChatApp", config[0], Integer.parseInt(config[1]));
    }

    public static void startServer(String subnetPrefix) {
        int port = generateRandomPort();
        SocketServer socketServer = new SocketServer(port);
        socketServer.start();

        RequestListener requestListener = new RequestListener(subnetPrefix, port);
        requestListener.start();
    }

    public static int generateRandomPort() {
        return 5000 + (int) (1000 * Math.random());
    }
}
