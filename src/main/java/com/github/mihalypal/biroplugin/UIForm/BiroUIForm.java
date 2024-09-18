package com.github.mihalypal.biroplugin.UIForm;

import com.intellij.ui.JBColor;

import javax.swing.*;

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

        loginButton.setBackground(JBColor.BLUE);
        loginButton.setOpaque(true);
        loginButton.setContentAreaFilled(true);

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
