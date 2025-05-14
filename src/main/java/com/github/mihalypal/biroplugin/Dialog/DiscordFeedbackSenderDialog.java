package com.github.mihalypal.biroplugin.Dialog;

import com.github.mihalypal.biroplugin.Services.DiscordWebhook;
import com.github.mihalypal.biroplugin.Services.UserServices;
import com.github.mihalypal.biroplugin.config.PluginConstants;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

public class DiscordFeedbackSenderDialog extends DialogWrapper {
    private static final Logger LOG = Logger.getInstance(DiscordFeedbackSenderDialog.class);
    private static final int MAX_CHARS = 2000;

    private final Project project;
    private JTextArea feedbackArea;
    private JCheckBox checkBox;
    private JComboBox<String> comboBox;

    public DiscordFeedbackSenderDialog(Project project) {
        super(project);
        this.project = project;
        setTitle("Visszajelzés Küldése");
        setOKButtonText("Küldés");
        setCancelButtonText("Mégse");
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel label = new JLabel("<html>A visszajelzés alapvetően Anonim módon történik.<br>"
                                    + "Ha nem Anonim módon szeretnél visszajelezni, pipáld be a lenti checkboxot.<br>"
                                    + "Írd be a visszajelzésed (max " + MAX_CHARS + " karakter):</html>");
        panel.add(label, BorderLayout.NORTH);

        feedbackArea = new JTextArea();
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setRows(10);
        feedbackArea.setColumns(40);

        // Karakterkorlát beállítása DocumentFilter-rel
        AbstractDocument doc = (AbstractDocument) feedbackArea.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (fb.getDocument().getLength() + string.length() <= MAX_CHARS) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                int newLength = fb.getDocument().getLength() - length + text.length();
                if (newLength <= MAX_CHARS) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        checkBox = new JCheckBox("h-s azonosító megjelenítése a visszajelzésben");
        checkBox.setSelected(false);

        comboBox = new JComboBox<>(new String[]{"Visszajelzés", "Hiba", "Javaslat", "Egyéb"});

        bottomPanel.add(checkBox, BorderLayout.NORTH);
        bottomPanel.add(comboBox, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    @Override
    protected void doOKAction() {
        String message = feedbackArea.getText().trim().replace("\n", "\\n");
        if (message.isEmpty()) {
            Messages.showErrorDialog(project, "A visszajelzés nem lehet üres.", "Hiba");
            return;
        }
        sendToDiscord(message);
        super.doOKAction();
    }

    /**
     * Aszinkron HTTP POST küldés a Discord webhookra csak a "content" mezővel.
     */
    public void sendToDiscord(String message) {
        /*ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                URL url = new URL(PluginConstants.DISCORD_WEBHOOK_URL);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                String json = String.format("{\"content\": \"%s\"}", escapeJson(message));
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code != 200 && code != 204) {
                    LOG.warn("Discord webhook válaszkód: " + code);
                }
            } catch (Exception e) {
                LOG.error("Hiba a Discord webhook küldésekor", e);
            }
        });*/
        // Use the DiscordWebhook service to send the message, because it can send more complex messages
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            System.out.println(message);
            // get the current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
            String formattedDateTime = now.format(formatter);

            // create random Avatar URL
            String avatarUrl = "https://www.inf.u-szeged.hu/~gmark/biro/prog1/gyak11_to/img0";
            int randomNumber = (int) (Math.random() * 8) + 1;
            avatarUrl += randomNumber + ".gif";

            // chatbot name
            String chatbotName = "Feedback Sender";
            if (checkBox.isSelected()) {
                chatbotName = chatbotName + " (" + UserServices.getHIdentifier() + ")";
            }

            DiscordWebhook webhook = new DiscordWebhook(PluginConstants.DISCORD_WEBHOOK_URLS.get(Objects.requireNonNull(comboBox.getSelectedItem()).toString()));
            //webhook.setContent("Ez egy sima üzenet");
            webhook.setAvatarUrl((checkBox.isSelected()) ? avatarUrl : "");
            webhook.setUsername(chatbotName);
            webhook.setTts(true);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle(Objects.requireNonNull(comboBox.getSelectedItem()).toString())
                    .setDescription(message)
                    .setColor(PluginConstants.colorMap.get(Objects.requireNonNull(comboBox.getSelectedItem()).toString()))
                    .addField("Hallgató", (checkBox.isSelected()) ? UserServices.getHIdentifier() : "Anonim visszajelzés", true)
                    //.addField("Kategória", Objects.requireNonNull(comboBox.getSelectedItem()).toString(), true)
                    .setFooter(formattedDateTime, "")
                    /*.setAuthor((checkBox.isSelected()) ? UserServices.getHIdentifier() : "Anonim visszajelzés", "", "")*/);
            try {
                webhook.execute();
            } catch (UnknownHostException ex) {
                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    SwingUtilities.invokeLater(() -> {
                        Notifications.Bus.notify(
                                new Notification(
                                        "Attach to Process action",
                                        "Nincs internet kapcsolat!",
                                        "Kérlek ellenőrizd az internet kapcsolatodat.",
                                        NotificationType.INFORMATION
                                )
                        );
                    });
                });
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * Egyszerű JSON-escape a visszajelzéshez.
     */
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
