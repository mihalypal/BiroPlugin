package com.github.mihalypal.biroplugin.UIForms;

import com.intellij.openapi.application.PathManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class BiroUIMainForm {
    private JPanel mainPanel;
    private JBLabel label;
    private JTextField textField;
    private JButton button;
    private JButton button2;
    private JButton logoutButton;

    public BiroUIMainForm() {
        mainPanel = new JBPanel<>();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        label = new JBLabel("Enter something:");
        textField = new JTextField();
        Dimension size = new Dimension(200, 30);
        textField.setPreferredSize(size);
        textField.setMaximumSize(size);
        textField.setMinimumSize(size);
        button = new JButton("Submit");
        button2 = new JButton("Árvíztűrő tükörfúrógép");
        logoutButton = new JButton("Logout");

        mainPanel.add(label);
        mainPanel.add(textField);
        mainPanel.add(button);
        mainPanel.add(button2);
        mainPanel.add(logoutButton);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputText = textField.getText();
                // Handle button click event
                System.out.println("Button clicked with input: " + inputText);
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle button click event
                System.out.println("Árvíztűrő tükörfúrógép pressed");
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
    }

    public void logout() {
        BiroUILoginForm loginForm = new BiroUILoginForm();
        JPanel parentPanel = (JPanel) mainPanel.getParent();
        parentPanel.removeAll();
        parentPanel.add(loginForm.getMainPanel());
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
