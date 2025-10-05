package com.github.mihalypal.biroplugin.config;

import java.awt.*;
import java.util.Map;

public final class PluginConstants {
    public static final String BASE_URL = "https://biro3.inf.u-szeged.hu";
    public static final String SEMESTER_NAME = "2025/2026/1";
    public static final String DISCORD_WEBHOOK_URL_TEST = ConfigLoader.getProperty("DISCORD_WEBHOOK_URL_TEST"); // not used
    public static final Map<String, String> DISCORD_WEBHOOK_URLS = Map.of(
            "Visszajelzés", ConfigLoader.getProperty("DISCORD_WEBHOOK_URLS.Visszajelzés"),
            "Hiba", ConfigLoader.getProperty("DISCORD_WEBHOOK_URLS.Hiba"),
            "Javaslat", ConfigLoader.getProperty("DISCORD_WEBHOOK_URLS.Javaslat"),
            "Egyéb", ConfigLoader.getProperty("DISCORD_WEBHOOK_URLS.Egyéb"),
            "Automata_error_log", ConfigLoader.getProperty("DISCORD_WEBHOOK_URLS.Automata_error_log"),
            "Automata_statisztika_log", ConfigLoader.getProperty("DISCORD_WEBHOOK_URLS.Automata_statisztika_log")
    );
    public static Map<String, Color> colorMap = Map.of(
            "Visszajelzés", new Color(255, 255, 94),
            "Hiba", new Color(255, 94, 94),
            "Javaslat", new Color(94, 255, 94),
            "Egyéb", new Color(94, 94, 255)
    );

    private PluginConstants() {}
}
