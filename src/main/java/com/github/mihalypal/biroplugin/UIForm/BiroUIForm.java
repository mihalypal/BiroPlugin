package com.github.mihalypal.biroplugin.UIForm;

import javax.swing.*;

public class BiroUIForm {

    private JPanel mainPanel;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton bejelentkezesButton;
    private JLabel szoveg;
    private JLabel szoveg1;
    private JLabel bejelentkezes_szoveg;

    public BiroUIForm() {
        bejelentkezesButton.addActionListener(e -> {
            String username = textField1.getText();
            String password = new String(passwordField1.getPassword());
            System.out.println("Username: " + username + ", Password: " + password);
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
