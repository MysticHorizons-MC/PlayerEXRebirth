# 🌟 PlayerExRebirth

**PlayerExRebirth** is a powerful Fabric mod addon for [PlayerEx: Directors Cut](https://modrinth.com/mod/playerex-directors-cut), designed to expand the leveling system with **Prestige**, **Stat Resets**, **Rewards**, and full **customizability**. Ideal for RPG-focused servers or modpacks that want to create progression-based experiences.

---

## 🚀 Features

- ⚡ **Prestige System**  
  Players can "prestige" after reaching the maximum level (default 100), resetting their level in exchange for prestige ranks and rewards.

- 🔁 **Stat Reset Support**  
  Players can reset their stats (optional toggle) when prestiging.

- 🎁 **Custom Rewards**  
  Server owners can define prestige rewards such as:
    - Commands (run server-side)
    - Items (modded or vanilla)
    - Items with full NBT support

- 💫 **Toast Notifications & GUI**  
  When a player becomes eligible to prestige, a sleek OwoUI-based notification appears, and they can open a GUI showing:
    - Current Prestige Level
    - Bonuses and Rewards
    - Prestige button (if eligible)

- 🔧 **Highly Configurable**
    - Max prestige level
    - Prestige requirements
    - All rewards defined in `config/PlayerEXRebirth/config.jsonc`

---

## 📦 Installation

### 📁 Requirements

- Minecraft 1.20.1+
- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [PlayerEx: Director's Cut](https://modrinth.com/mod/playerex-directors-cut)
- [OwoLib (Owo UI)](https://modrinth.com/mod/owo-lib)
- [Cloth Config](https://modrinth.com/mod/cloth-config)
- [Placeholder API](https://modrinth.com/mod/placeholder-api)
- [Data Attributes](https://modrinth.com/mod/data-attributes-directors-cut)

### ✅ How to Add to Your Modpack

1. Download the latest version of **PlayerExRebirth** from [Modrinth](#) or your preferred source.
2. Place the `.jar` file into your modpack’s `mods/` folder (both **client** and **server**).
3. Ensure the mod has access to `config/PlayerEXRebirth/` — the config file will auto-generate on first run.
4. Customize `config.jsonc` to configure prestige levels, commands, and rewards.
5. Launch the game!

> ⚠ Make sure both client and server have the **exact same jar** to avoid network desync issues.

---

## 🛠 Configuration Overview

```jsonc
{
  // Max number of times a player can prestige
  "maxPrestige": 10,

  // Attribute multipliers per prestige level
  // Format: "attribute_id": multiplier * prestigeLevel
  "rewards": {
    "constitution": 0.5,
    "health_regeneration": 0.1,
    "lifesteal": 0.1,
    "dexterity": 1.0,
    "melee_critical_chance": 0.05
  },

  // Items to give upon prestige
  "prestigeItems": [
    {
      "item": "minecraft:enchanted_book",
      "count": 1,
      "nbt": "{StoredEnchantments:[{id:\"minecraft:sharpness\",lvl:5s}]}"
    },
    {
      "item": "ars_nouveau:source_gem",
      "count": 32
    },
    {
      "item": "create:brass_hand",
      "count": 1,
      "nbt": "{display:{Name:'{\"text\":\"Modded Claw\"}'}}"
    }
  ],

  // Commands to run after prestige
  // Placeholders: <player>, <prestige>
  "prestigeCommands": [
    "say Congratulations <player> on reaching prestige <prestige>!",
    "title <player> title {\"text\":\"You have Prestiged!\",\"color\":\"gold\"}"
  ]
}
