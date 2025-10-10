package com.nebulae.clickgui.client.data;

import java.util.Arrays;
import java.util.List;

public final class ClickGuiData {
    private ClickGuiData() {
    }

    public static List<CategoryData> createDefaultCategories() {
        return Arrays.asList(
                new CategoryData("Combat", "⚔", Arrays.asList(
                        new ModuleData("Aura", "Automates attacks with configurable delays."),
                        new ModuleData("Crystal", "Places and explodes crystals around enemies."),
                        new ModuleData("Velocity", "Reduces knockback when you are hit."),
                        new ModuleData("AutoTotem", "Manages totems of undying in your hands."),
                        new ModuleData("Criticals", "Forces critical hits when possible."),
                        new ModuleData("AntiBot", "Filters out fake entities to prevent aimbot issues."),
                        new ModuleData("Trigger", "Automatically attacks anything you are looking at.")
                )),
                new CategoryData("Movement", "➠", Arrays.asList(
                        new ModuleData("Fly", "Allows creative-style flight with speed controls."),
                        new ModuleData("LongJump", "Performs long boosted jumps for fast travel."),
                        new ModuleData("Speed", "Applies ground speed hacks with strafe boost."),
                        new ModuleData("InventoryMove", "Move even while a GUI is open."),
                        new ModuleData("NoSlow", "Prevent slowdowns when using items."),
                        new ModuleData("Spider", "Climb vertical surfaces like a spider."),
                        new ModuleData("HighJump", "Jump higher by charging vertical momentum."),
                        new ModuleData("ElytraBoost", "Boosts elytra flight with configurable curves.")
                )),
                new CategoryData("Render", "✹", Arrays.asList(
                        new ModuleData("FullBright", "Removes darkness by boosting gamma."),
                        new ModuleData("ESP", "Highlights players and mobs through walls."),
                        new ModuleData("Chams", "Renders entities with custom colors."),
                        new ModuleData("Trails", "Draws fading trails behind moving entities."),
                        new ModuleData("NameTags", "Expands nametag information and scale."),
                        new ModuleData("StorageESP", "Shows storage blocks with outlines."),
                        new ModuleData("Tracers", "Draws lines from the player to entities."),
                        new ModuleData("Swing", "Customizes arm swing animations."),
                        new ModuleData("Crosshair", "Adds a customizable crosshair overlay.")
                )),
                new CategoryData("Player", "☺", Arrays.asList(
                        new ModuleData("AutoArmor", "Automatically equips the best armor available."),
                        new ModuleData("AutoFish", "Casts and reels automatically when fish bite."),
                        new ModuleData("AutoTool", "Switches to the best tool for the block."),
                        new ModuleData("FastUse", "Removes delays on using consumables."),
                        new ModuleData("Mend", "Protects armor durability while mending."),
                        new ModuleData("XPThrow", "Throws XP bottles instantly for repairs."),
                        new ModuleData("AutoEat", "Maintains hunger by eating automatically."),
                        new ModuleData("SafeWalk", "Prevents walking off edges accidentally."),
                        new ModuleData("MiddleClickFriend", "Adds players as friends with middle click.")
                )),
                new CategoryData("World", "☘", Arrays.asList(
                        new ModuleData("AutoFarm", "Harvests crops and replants automatically."),
                        new ModuleData("PacketMine", "Breaks blocks using packet duplication."),
                        new ModuleData("AutoCity", "Automates obsidian breaking to city enemies."),
                        new ModuleData("HoleFill", "Fills nearby holes with obsidian."),
                        new ModuleData("Scaffold", "Build bridges while walking automatically."),
                        new ModuleData("ChestStealer", "Loots containers instantly."),
                        new ModuleData("AutoBuilder", "Repeats schematic placements."),
                        new ModuleData("Burrow", "Self traps the player in obsidian."),
                        new ModuleData("BedBomb", "Detonates beds safely in the nether."),
                        new ModuleData("AutoSmelter", "Manages furnace inputs and outputs.")
                )),
                new CategoryData("Client", "✧", Arrays.asList(
                        new ModuleData("ClickGUI", "Opens this interface for module configuration."),
                        new ModuleData("HudEditor", "Customize on-screen HUD elements."),
                        new ModuleData("ConfigManager", "Save and load module configurations."),
                        new ModuleData("Theme", "Switch between multiple accent color themes."),
                        new ModuleData("Notifications", "Shows alerts for events in the world."),
                        new ModuleData("Profiles", "Group module states into named profiles."),
                        new ModuleData("ScriptEngine", "Run Kotlin or JS scripts inside the client."),
                        new ModuleData("Friends", "Manage friends list and share settings."),
                        new ModuleData("Macro", "Create keybound command macros."),
                        new ModuleData("MusicPlayer", "Play local tracks with in-game controls.")
                ))
        );
    }
}
