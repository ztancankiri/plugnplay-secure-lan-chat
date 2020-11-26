package com.ztan.chatapp.security;

import com.ztan.chatapp.Message;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AES {

    private static final String CIPHER_INSTANCE_NAME = "AES/CBC/PKCS5PADDING";
    private static final String ALGORITHM = "AES";

    public static EncryptedMessage encrypt(String passphrase, Message message) {
        while (passphrase.length() < 8) {
            passphrase += passphrase;
        }

        try {
            KeyGenerator keyGenerator = new KeyGenerator(passphrase);
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyGenerator.getKey(), ALGORITHM), new IvParameterSpec(keyGenerator.getIV()));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
            byte[] cipheredMessage = cipher.doFinal(byteArrayOutputStream.toByteArray());

            return new EncryptedMessage(keyGenerator.getExtraIteration(), keyGenerator.getSalt(), keyGenerator.getIV(), cipheredMessage);

        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | IOException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Error while encrypting!");
        }

        return null;
    }

    public static Message decrypt(String passphrase, EncryptedMessage encryptedMessage) {
        while (passphrase.length() < 8) {
            passphrase += passphrase;
        }

        int extraIteration = encryptedMessage.extraIteration;
        byte[] salt = encryptedMessage.salt;
        byte[] iv = encryptedMessage.iv;
        byte[] message = encryptedMessage.message;

        try {
            KeyGenerator keyGenerator = new KeyGenerator(passphrase, salt, iv, extraIteration);
            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE_NAME);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyGenerator.getKey(), ALGORITHM), new IvParameterSpec(iv));
            byte[] plainMessage = cipher.doFinal(message);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(plainMessage);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (Message) objectInputStream.readObject();

        } catch (NoSuchPaddingException | ClassNotFoundException | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | IOException | IllegalBlockSizeException | BadPaddingException e) {
            System.err.println("Error while encrypting!");
        }

        return null;
    }
}
