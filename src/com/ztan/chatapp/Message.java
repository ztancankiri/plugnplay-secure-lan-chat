package com.ztan.chatapp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class Message implements Serializable {

    public String fromIP;
    public String fromName;
    public String text;
    public byte[] data;
    public MessageType type;

    public Message(String fromIP, String fromName, String text) {
        this.fromIP = fromIP;
        this.fromName = fromName;
        this.text = text;
        this.type = MessageType.TEXT;
    }

    public Message(String fromIP, String fromName, byte[] data) {
        this.fromIP = fromIP;
        this.fromName = fromName;
        this.data = data;
        this.type = MessageType.DATA;
    }

    @Override
    public String toString() {
        return "Message{" +
                "fromIP='" + fromIP + '\'' +
                ", fromName='" + fromName + '\'' +
                ", text='" + text + '\'' +
                ", data=" + Arrays.toString(data) +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(fromIP, message.fromIP) &&
                Objects.equals(fromName, message.fromName) &&
                Objects.equals(text, message.text) &&
                Arrays.equals(data, message.data) &&
                type == message.type;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fromIP, fromName, text, type);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
