package com.ztan.chatapp.discovery;

import java.io.Serializable;

public class DiscoveryPacket implements Serializable {

    public DiscoveryType type;
    public String IP;
    public int port;

    public DiscoveryPacket() {
        this.type = DiscoveryType.REQUEST;
        this.IP = null;
        this.port = -1;
    }

    public DiscoveryPacket(String IP, int port) {
        this.type = DiscoveryType.RESPONSE;
        this.IP = IP;
        this.port = port;
    }

}
