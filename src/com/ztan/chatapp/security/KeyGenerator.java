package com.ztan.chatapp.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class KeyGenerator {

    private static final String HASH_FUNCTION = "PBKDF2WithHmacSHA256";
    private static final String ALGORITHM = "AES";
    private static final int ITERATION = 65536;
    private static final int LENGTH = 16;

    private int extraIteration;
    private byte[] salt;
    private byte[] iv;
    private byte[] key;

    public KeyGenerator(String passphrase) {
        this.salt = generateRandomString(LENGTH).getBytes();
        this.iv = generateRandomIV(LENGTH);
        this.extraIteration = generateRandomNumber();

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_FUNCTION);

            KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATION + this.extraIteration, LENGTH * LENGTH);
            SecretKey tmp = factory.generateSecret(keySpec);
            this.key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public KeyGenerator(String passphrase, byte[] salt, byte[] iv, int extraIteration) {
        this.salt = salt;
        this.iv = iv;
        this.extraIteration = extraIteration;

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(HASH_FUNCTION);

            KeySpec keySpec = new PBEKeySpec(passphrase.toCharArray(), salt, ITERATION + this.extraIteration, LENGTH * LENGTH);
            SecretKey tmp = factory.generateSecret(keySpec);
            this.key = new SecretKeySpec(tmp.getEncoded(), ALGORITHM).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private int generateRandomNumber() {
        return (int) (1000 * Math.random());
    }

    private byte[] generateRandomIV(int length) {
        byte[] ivArr = new byte[length];

        for (int i = 0; i < ivArr.length; i++) {
            ivArr[i] = (byte) (1000 * Math.random());
        }

        return new IvParameterSpec(ivArr).getIV();
    }

    private String generateRandomString(int length) {
        String alphabet = "ABCDEF0123456789abcdef";
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int idx = (int) (alphabet.length() * Math.random());
            result.append(alphabet.charAt(idx));
        }

        return result.toString();
    }

    public int getExtraIteration() {
        return extraIteration;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIV() {
        return iv;
    }

    public byte[] getKey() {
        return key;
    }
}
