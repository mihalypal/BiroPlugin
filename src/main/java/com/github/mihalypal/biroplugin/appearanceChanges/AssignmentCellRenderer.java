package com.github.mihalypal.biroplugin.appearanceChanges;

//import javax.swing.*;
//import java.awt.*;
//
//public class AssignmentCellRenderer extends DefaultListCellRenderer {
//    @Override
//    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        String[] parts = value.toString().split(";");
//        String assignmentName = parts[0];
//        String deadline = parts[1];
//
//        // Option 1: Name on the left, deadline on the right
//        label.setText(String.format("<html><body style='width: 100%%;'>%s<span style='float: right;'>%s</span></body></html>", assignmentName, deadline));
//
//        // Option 2: Name above, deadline below with an empty line
//        // label.setText(String.format("<html>%s<br>%s<br><br></html>", assignmentName, deadline));
//
//        return label;
//    }
//}

import javax.swing.*;
import java.awt.*;

public class AssignmentCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel();
        JLabel deadlineLabel = new JLabel();

        String[] parts = value.toString().split(";");
        String assignmentName = parts[0];
        String deadline = parts[1];

        nameLabel.setText(assignmentName);
        deadlineLabel.setText(deadline);
        deadlineLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(nameLabel, BorderLayout.WEST);
        panel.add(deadlineLabel, BorderLayout.EAST);

        if (isSelected) {
            panel.setBackground(list.getSelectionBackground());
            nameLabel.setForeground(list.getSelectionForeground());
            deadlineLabel.setForeground(list.getSelectionForeground());
        } else {
            panel.setBackground(list.getBackground());
            nameLabel.setForeground(list.getForeground());
            deadlineLabel.setForeground(list.getForeground());
        }

        return panel;
    }
}