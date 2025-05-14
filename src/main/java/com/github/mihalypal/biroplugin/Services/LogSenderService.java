package com.github.mihalypal.biroplugin.Services;

import com.github.mihalypal.biroplugin.config.PluginConstants;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogSenderService {

    public static void sendError(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        String formattedDateTime = now.format(formatter);

        DiscordWebhook webhook = new DiscordWebhook(PluginConstants.DISCORD_WEBHOOK_URLS.get("Automata_error_log"));
        webhook.setUsername("Automatic Error Logger");
        webhook.setTts(true);

        // Create a new embed for each chunk
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Error Log")
                .setDescription(message)
                .setColor(PluginConstants.colorMap.get("Hiba"))
                .addField("Hallgató", (UserServices.isLogSendingWithIdentifier()) ? UserServices.getHIdentifier() : "Anonim Log", true)
                .setFooter(formattedDateTime, ""));
        try {
            webhook.execute();
        } catch (IOException ex) {
            return;
        }
    }

    public static void sendStatistic(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");
        String formattedDateTime = now.format(formatter);

        DiscordWebhook webhook = new DiscordWebhook(PluginConstants.DISCORD_WEBHOOK_URLS.get("Automata_statisztika_log"));
        webhook.setUsername("Automatic Statistical Logger");
        webhook.setTts(true);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Statistic Log")
                .setDescription(message)
                .setColor(PluginConstants.colorMap.get("Egyéb"))
                .addField("Hallgató", (UserServices.isLogSendingWithIdentifier()) ? UserServices.getHIdentifier() : "Anonim Log", true)
                //.addField("Kategória", Objects.requireNonNull(comboBox.getSelectedItem()).toString(), true)
                .setFooter(formattedDateTime, "")
                /*.setAuthor((checkBox.isSelected()) ? UserServices.getHIdentifier() : "Anonim visszajelzés", "", "")*/);
        try {
            webhook.execute();
        } catch (IOException ex) {
            return;
        }
    }

}
