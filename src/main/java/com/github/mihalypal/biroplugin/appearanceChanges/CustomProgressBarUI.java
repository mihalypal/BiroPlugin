package com.github.mihalypal.biroplugin.appearanceChanges;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class CustomProgressBarUI extends BasicProgressBarUI {

    private final Color fillColor;
    private final Color backgroundColor;
    private final int arcSize = 20; // Lekerekítés mértéke
    private final int preferredHeight = 20; // Vastagabb progress bar

    public CustomProgressBarUI(Color fillColor, Color backgroundColor) {
        this.fillColor = fillColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Insets b = progressBar.getInsets();
        int width = progressBar.getWidth();
        int totalHeight = progressBar.getHeight();
        int height = Math.min(totalHeight, preferredHeight); // Progress bar magasságának beállítása

        int barRectWidth = width - (b.right + b.left);
        int fillWidth = (int) (barRectWidth * progressBar.getPercentComplete());

        // Középre igazítás vertikálisan
        int yOffset = (totalHeight - height) / 2;

        // Háttér szín
        g2.setColor(backgroundColor);
        g2.fillRoundRect(b.left, yOffset, barRectWidth, height, arcSize, arcSize);

        // Kitöltés szín (progress sáv)
        g2.setColor(fillColor);
        g2.fillRoundRect(b.left, yOffset, fillWidth, height, arcSize, arcSize);

        // **Szöveg kirajzolása UIManager beállítások alapján**
        if (progressBar.isStringPainted()) {
            g2.setFont(progressBar.getFont());

            // Szöveg színe: ha UIManager-ben van beállítva, azt használjuk, különben alapértelmezett
            Color textColor = UIManager.getColor("ProgressBar.selectionForeground");
            if (textColor == null) {
                textColor = progressBar.getForeground();
            }
            g2.setColor(textColor);

            String progressString = progressBar.getString();
            FontMetrics fontMetrics = g2.getFontMetrics();
            int stringWidth = fontMetrics.stringWidth(progressString);
            int stringHeight = fontMetrics.getAscent();
            int x = (width - stringWidth) / 2;
            int y = (totalHeight + stringHeight) / 2 - 2; // Vertikális középre igazítás
            g2.drawString(progressString, x, y);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);
        d.height = preferredHeight; // Progress bar vastagságának módosítása
        return d;
    }
}
