package com.github.mihalypal.biroplugin.toolWindow;

import com.github.mihalypal.biroplugin.UIForm.BiroUIForm;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyToolWindowUI {

    private BiroUIForm biroUIForm;

    private JPanel mainPanel;
    private JBLabel label;
    private JTextField textField;
    private JButton button;
    private JButton button2;

    public MyToolWindowUI() {

        biroUIForm = new BiroUIForm();
        //mainPanel = biroUIForm.getMainPanel();

        //mainPanel = new JBPanel<>();
        //mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        /*label = new JBLabel("Enter something:");
        textField = new JTextField();
        Dimension size = new Dimension(200, 30);
        textField.setPreferredSize(size);
        textField.setMaximumSize(size);
        textField.setMinimumSize(size);
        button = new JButton("Submit");
        button2 = new JButton("Árvíztűrő tükörfúrógép");

        mainPanel.add(label);
        mainPanel.add(textField);
        mainPanel.add(button);
        mainPanel.add(button2);

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
        });*/
    }

    public JPanel getMainPanel() {
        return biroUIForm.getMainPanel();
    }
}