package com.github.mihalypal.biroplugin.UIForm;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class BiroUIForm {

    private JPanel mainPanel;
    private JLabel loginLabel;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;


    public BiroUIForm() {

        /*
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        label = new JLabel("Bejelentkezés");

        label1 = new JLabel("Username:");
        textField1 = new JTextField();
        Dimension size = new Dimension(200, 30);
        textField1.setPreferredSize(size);
        textField1.setMaximumSize(size);
        textField1.setMinimumSize(size);

        label2 = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(size);
        passwordField.setMaximumSize(size);
        passwordField.setMinimumSize(size);

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(label);
        mainPanel.add(label1);
        mainPanel.add(textField1);
        mainPanel.add(label2);
        mainPanel.add(passwordField);
        mainPanel.add(loginButton);*/

        /*loginButton.addActionListener(e -> {
            String username = textField1.getText();
            String password = new String(passwordField.getPassword());
            System.out.println("Username: " + username + " Password: " + password);
            //JOptionPane.showMessageDialog(null, "Username: " + username + " Password: " + password);
            //JOptionPane.showMessageDialog(null, "Sikeres bejelentkezés", "Bejelentkezés", JOptionPane.INFORMATION_MESSAGE);

            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage("Username: " + username + " Password: " + password);
            optionPane.setMessageType(JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = optionPane.createDialog("Login");
            dialog.setAlwaysOnTop(true);
            dialog.setVisible(true);

        });*/

        // Set border for the labels
        usernameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        passwordLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        loginButton.addActionListener(e -> {
            String username = usernameTextField.getText();
            String password = new String(passwordField.getPassword());
            System.out.println("Username: " + username + " Password: " + password);

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Add meg a h-s azonosítód és a jelszavad!", "Hiba", JOptionPane.ERROR_MESSAGE);
            } else {
                if (username.length() == 7) {
                    boolean authenticated = authenticateUser(username, password);
                    if (authenticated)
                    JOptionPane.showMessageDialog(null, "Sikeres bejelentkezés", "Bejelentkezés", JOptionPane.INFORMATION_MESSAGE);
                    else
                    JOptionPane.showMessageDialog(null, "Hibás h-s azonosító vagy jelszó", "Hiba", JOptionPane.ERROR_MESSAGE);
                    // TODO: Call the API with the given username and password
                } else {
                    JOptionPane.showMessageDialog(null, "Hibás h-s azonosító", "Hiba", JOptionPane.ERROR_MESSAGE);
                }
            }


        });

    }

    private boolean authenticateUser(String username, String password) {
        try {
            URL url = new URL("http://127.0.0.1:5000/authenticate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response code: " + responseCode);
            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
