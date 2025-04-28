package com.github.mihalypal.biroplugin.appearanceChanges;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class CustomButtonUI extends BasicButtonUI {
    private final String status;

    public CustomButtonUI(String status) {
        this.status = status;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        JButton button = (JButton) c;
        Graphics2D g2 = (Graphics2D) g;
/*
        // Háttérszín és lekerekített sarkok
        //g2.setColor(Color.GREEN);
        //set g2 color with rgba code
        g2.setColor(new Color(0, 200, 0, 80));
        g2.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 0, 0);

        // Szöveg megrajzolása
        g2.setColor(Color.BLACK);
        g2.setFont(button.getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (button.getWidth() - fm.stringWidth(button.getText())) / 2;
        int y = (button.getHeight() + fm.getAscent()) / 2 - 2;
        g2.drawString(button.getText(), x, y);*/

        // Engedélyezzük az élsimítást a jobb megjelenés érdekében
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color backgroundColor = switch (status) {
            case "INFO" -> new Color(50, 130, 200, 180); // Kék
            case "COMPLETED" -> new Color(255, 165, 0, 180); // Narancssárga
            case "COMPLETED_ZERO" -> new Color(255, 165, 0, 180); // Narancssárga
            case "MAX" -> new Color(0, 200, 0, 200); // Zöld
            default -> new Color(100, 100, 100, 150); // Szürke default
        };

        // Háttérszín és lekerekített sarkok
        //g2.setColor(new Color(0, 200, 0, 150)); // Erősebb zöld, áttetsző háttér
        g2.setColor(backgroundColor);
        int arcSize = 4; // Lekerekítés mértéke
        g2.fillRoundRect(4, 4, button.getWidth()-8, button.getHeight()-8, arcSize, arcSize);

        // Eltávolítjuk a keretet (ha lenne)
        g2.setColor(new Color(0, 0, 0, 0)); // Átlátszó keret
        g2.drawRoundRect(0, 0, button.getWidth(), button.getHeight(), arcSize, arcSize);

        // Szöveg beállítása (vastagabb betűtípus)
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14)); // Vastagabb betű
        FontMetrics fm = g2.getFontMetrics();
        int x = (button.getWidth() - fm.stringWidth(button.getText())) / 2;
        int y = (button.getHeight() + fm.getAscent()) / 2 - 2;
        g2.drawString(button.getText(), x, y);
    }
}
