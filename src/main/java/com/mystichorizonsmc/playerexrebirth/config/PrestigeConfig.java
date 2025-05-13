package com.mystichorizonsmc.playerexrebirth.config;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PrestigeConfig {

    public static int maxPrestige = 10;
    public static Map<String, Double> rewardMultipliers = new HashMap<>();
    public static List<ItemStack> prestigeItems = new ArrayList<>();
    public static List<String> prestigeCommands = new ArrayList<>();

    private static final File CONFIG_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("PlayerEXRebirth/config.jsonc")
            .toFile();

    public static void load() {
        if (!CONFIG_FILE.exists()) {
            saveDefault();
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            maxPrestige = json.has("maxPrestige") ? json.get("maxPrestige").getAsInt() : 10;

            rewardMultipliers.clear();
            if (json.has("rewards")) {
                JsonObject rewards = json.getAsJsonObject("rewards");
                for (Map.Entry<String, JsonElement> entry : rewards.entrySet()) {
                    rewardMultipliers.put(entry.getKey(), entry.getValue().getAsDouble());
                    if (!BuiltInRegistries.ATTRIBUTE.containsKey(new ResourceLocation(entry.getKey()))) {
                        System.out.println("[PlayerEXRebirth] Unknown attribute in config: " + entry.getKey());
                    }
                }
            }

            prestigeItems.clear();
            if (json.has("prestigeItems")) {
                JsonArray items = json.getAsJsonArray("prestigeItems");
                for (JsonElement el : items) {
                    JsonObject obj = el.getAsJsonObject();
                    String id = obj.get("item").getAsString();
                    int count = obj.has("count") ? obj.get("count").getAsInt() : 1;

                    Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(id));
                    if (item == Items.AIR) continue;

                    ItemStack stack = new ItemStack(item, count);

                    if (obj.has("nbt")) {
                        try {
                            String nbtString = obj.get("nbt").getAsString();
                            CompoundTag tag = TagParser.parseTag(nbtString);
                            stack.setTag(tag);
                        } catch (Exception e) {
                            System.err.println("[PlayerEXRebirth] Invalid NBT for item '" + id + "': " + e.getMessage());
                        }
                    }

                    prestigeItems.add(stack);
                }
            }

            prestigeCommands.clear();
            if (json.has("prestigeCommands")) {
                JsonArray commands = json.getAsJsonArray("prestigeCommands");
                for (JsonElement el : commands) {
                    prestigeCommands.add(el.getAsString());
                }
            }

        } catch (Exception e) {
            System.err.println("[PlayerEXRebirth] Failed to load config: " + e.getMessage());
        }
    }

    public static void saveDefault() {
        JsonObject json = new JsonObject();
        json.addProperty("maxPrestige", 10);

        JsonObject rewards = new JsonObject();
        rewards.addProperty("constitution", 0.5);
        rewards.addProperty("health_regeneration", 0.1);
        rewards.addProperty("lifesteal", 0.1);
        rewards.addProperty("dexterity", 1.0);
        rewards.addProperty("melee_critical_chance", 0.05);
        json.add("rewards", rewards);

        JsonArray items = new JsonArray();

        JsonObject item1 = new JsonObject();
        item1.addProperty("item", "minecraft:enchanted_book");
        item1.addProperty("count", 1);
        item1.addProperty("nbt", "{StoredEnchantments:[{id:\"minecraft:sharpness\",lvl:5s}]}");
        items.add(item1);

        JsonObject item2 = new JsonObject();
        item2.addProperty("item", "ars_nouveau:source_gem");
        item2.addProperty("count", 32);
        items.add(item2);

        JsonObject item3 = new JsonObject();
        item3.addProperty("item", "create:brass_hand");
        item3.addProperty("count", 1);
        item3.addProperty("nbt", "{display:{Name:'{\"text\":\"Modded Claw\"}'}}");
        items.add(item3);

        json.add("prestigeItems", items);

        JsonArray commands = new JsonArray();
        commands.add("say Congratulations <player> on reaching prestige <prestige>!");
        commands.add("title <player> title {\"text\":\"You have Prestiged!\",\"color\":\"gold\"}");
        json.add("prestigeCommands", commands);

        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), StandardCharsets.UTF_8)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(json, writer);
            }
        } catch (IOException e) {
            System.err.println("[PlayerEXRebirth] Failed to save default config: " + e.getMessage());
        }
    }
}
