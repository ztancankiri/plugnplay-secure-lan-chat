package com.ztan.chatapp.security;

import java.io.Serializable;

public class EncryptedMessage implements Serializable {

    public int extraIteration;
    public byte[] salt;
    public byte[] iv;
    public byte[] message;

    public EncryptedMessage(int extraIteration, byte[] salt, byte[] iv, byte[] message) {
        this.extraIteration = extraIteration;
        this.salt = salt;
        this.iv = iv;
        this.message = message;
    }
}
