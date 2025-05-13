package com.mystichorizonsmc.playerexrebirth.client.config;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Environment(EnvType.CLIENT)
public class ClientPrestigeConfig {

    public static boolean showPrestigeButton = true;
    public static boolean enablePrestigeAnimations = true;
    public static String prestigeTextColor = "gold";
    public static boolean playSoundOnPrestige = true;
    public static boolean showPrestigeInTooltips = true;
    public static String tooltipFormat = "§6Prestige: §e<level>";

    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("PlayerEXRebirth/client_config.jsonc")
            .toFile();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefault();
            return;
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            showPrestigeButton = getBool(json, "showPrestigeButton", true);
            enablePrestigeAnimations = getBool(json, "enablePrestigeAnimations", true);

            JsonObject visual = json.has("visualFeedback") ? json.getAsJsonObject("visualFeedback") : new JsonObject();
            prestigeTextColor = visual.has("prestigeTextColor") ? visual.get("prestigeTextColor").getAsString() : "gold";
            playSoundOnPrestige = getBool(visual, "playSoundOnPrestige", true);

            JsonObject tooltips = json.has("tooltips") ? json.getAsJsonObject("tooltips") : new JsonObject();
            showPrestigeInTooltips = getBool(tooltips, "showPrestigeInTooltips", true);
            tooltipFormat = tooltips.has("format") ? tooltips.get("format").getAsString() : "§6Prestige: §e<level>";

        } catch (IOException e) {
            System.err.println("[PlayerEXRebirth] Failed to load client config: " + e.getMessage());
        }
    }

    public static void saveDefault() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  // Show a prestige button on the UI\n");
        sb.append("  \"showPrestigeButton\": true,\n\n");

        sb.append("  // Enable prestige animations and feedback\n");
        sb.append("  \"enablePrestigeAnimations\": true,\n\n");

        sb.append("  \"visualFeedback\": {\n");
        sb.append("    // Text color used in prestige messages\n");
        sb.append("    \"prestigeTextColor\": \"gold\",\n");
        sb.append("    // Whether to play a sound on prestige\n");
        sb.append("    \"playSoundOnPrestige\": true\n");
        sb.append("  },\n\n");

        sb.append("  \"tooltips\": {\n");
        sb.append("    // Show prestige info in tooltips\n");
        sb.append("    \"showPrestigeInTooltips\": true,\n");
        sb.append("    // Tooltip format (use <level> as placeholder)\n");
        sb.append("    \"format\": \"§6Prestige: §e<level>\"\n");
        sb.append("  }\n");
        sb.append("}\n");

        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                writer.write(sb.toString());
            }
        } catch (IOException e) {
            System.err.println("[PlayerEXRebirth] Failed to write client config: " + e.getMessage());
        }
    }

    private static boolean getBool(JsonObject obj, String key, boolean fallback) {
        return obj.has(key) && obj.get(key).isJsonPrimitive() && obj.get(key).getAsJsonPrimitive().isBoolean()
                ? obj.get(key).getAsBoolean()
                : fallback;
    }
}
