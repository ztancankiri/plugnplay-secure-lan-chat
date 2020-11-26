package com.ztan.chatapp.client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame {
    private JPanel panel;
    private JTextField txt_message;
    private JButton btn_send;
    private JTextArea txt_messages;

    private IGUIInteractor interactor;
    private String nickname;
    private String passphrase;

    public GUI(String applicationName, IGUIInteractor interactor) {
        super(applicationName);

        this.interactor = interactor;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setResizable(false);
        setContentPane(panel);
        pack();

        setLocationRelativeTo(null);
        setVisible(true);

        passphrase = askPassphrase();
        nickname = askNickname();

        btn_send.addActionListener(actionEvent -> {
            send();
        });

        txt_message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    send();
                }
            }
        });

        txt_message.grabFocus();

        DefaultCaret caret = (DefaultCaret) txt_messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    private String askPassphrase() {
        String answerPassphrase = JOptionPane.showInputDialog(getContentPane(), "Enter the passphrase:", "Passphrase?", JOptionPane.QUESTION_MESSAGE);

        if (answerPassphrase == null) {
            System.exit(1);
        }
        else if (answerPassphrase.equals("")) {
            return askPassphrase();
        }

        return answerPassphrase;
    }

    private String askNickname() {
        String answerNickname = JOptionPane.showInputDialog(getContentPane(), "Enter your nickname:", "Nickname?", JOptionPane.QUESTION_MESSAGE);

        if (answerNickname == null) {
            int answerYesNo = JOptionPane.showConfirmDialog(getContentPane(), "Do you want to use a random nickname?", "Random Nickname?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (answerYesNo == 0) {
                return generateRandomString(5);
            }
            else if (answerYesNo == 1) {
                return askNickname();
            }
        }
        else if (answerNickname.equals("")) {
            return askNickname();
        }

        return answerNickname;
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

    private void send() {
        interactor.sendMessage(passphrase, nickname, txt_message.getText());
        txt_message.setText("");
    }

    public void showMessage(String nickname, String message) {
        txt_messages.append(String.format("[%s]: %s\n", nickname, message));
    }

    public void showPopup(String title, String message) {
        JOptionPane.showMessageDialog(getContentPane(), message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public String getPassphrase() {
        return passphrase;
    }
}
