package com.github.mihalypal.biroplugin.appearanceChanges;

import com.github.mihalypal.biroplugin.Model.Submission;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CustomPanelRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {
        Submission s = (Submission) value;
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton eyeBtn = new JButton(AllIcons.Actions.Preview);
        JButton docBtn = new JButton(AllIcons.FileTypes.Text);
        eyeBtn.setEnabled(false);  // csak kirajzolunk
        docBtn.setEnabled(false);
        eyeBtn.setText("Megoldás");
        docBtn.setText("Riport");

        panel.add(eyeBtn);
        panel.add(docBtn);

        panel.setBackground(isSelected
                ? table.getSelectionBackground()
                : table.getBackground());
        return panel;
    }
}


