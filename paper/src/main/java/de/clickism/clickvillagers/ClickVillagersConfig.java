/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.clickvillagers.message.Message;
import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.configured.KeyGenerator;
import org.bukkit.entity.Villager;

import java.util.HashMap;
import java.util.Map;

public interface ClickVillagersConfig {
    Config CONFIG =
            Config.of("plugins/ClickVillagers/config.yml")
                    .version(7)
                    .keyGenerator(KeyGenerator.withAlternative(key -> key.replace('-', '_')))
                    .header("""
                            ---------------------------------------------------------
                            ClickVillagers Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

    ConfigOption<String> LANGUAGE =
            CONFIG.option("language", "en_US")
                    .description("""
                            Language of the plugin.
                            Available languages: en_US, de_DE, ru_RU, vi_VN
                            """)
                    .onChange(lang -> Message.LOCALIZATION
                            .language(lang)
                            .load());

    ConfigOption<Boolean> CHECK_UPDATES =
            CONFIG.option("check_updates", true)
                    .description("""
                            Whether to check for updates on server start.
                            Recommended!
                            """);

    ConfigOption<Boolean> CLAIMED_DAMAGE =
            CONFIG.option("claimed_damage", false)
                    .description("Whether claimed villagers can take damage.");

    ConfigOption<Boolean> CLAIMED_IMMUNE_KILL_COMMAND =
            CONFIG.option("claimed_immune_kill_command", true)
                    .description("""
                            Whether claimed villagers are immune to the /kill command.
                            Won't do anything if "claimed-villagers-take-damage" is enabled.
                            """);

    ConfigOption<Boolean> ALLOW_RESETTING_TRADES =
            CONFIG.option("allow_resetting_trades", false)
                    .description("""
                            Whether players can reset trades of villagers inside
                            the trading menu.
                            """);

    ConfigOption<Boolean> TICK_HOPPERS =
            CONFIG.option("tick_hoppers", true)
                    .header("""
                            ---------------------------------------------------------
                            Hopper Settings
                            ---------------------------------------------------------
                            """)
                    .description("Whether hoppers should tick.");

    ConfigOption<Integer> HOPPER_TICK_RATE =
            CONFIG.option("hopper_tick_rate", 20)
                    .description("""
                            The rate at which villager hoppers should tick. (in ticks)
                            You can increase this value to reduce lag, but it will also reduce
                            the speed at which villagers are picked up.
                            """);

    ConfigOption<Integer> HOPPER_LIMIT_PER_CHUNK =
            CONFIG.option("hopper_limit_per_chunk", -1)
                    .description("""
                            The maximum amount of villager hoppers a chunk can hold.
                            You can use this to prevent lag/spam.
                            -1 will disable the limit.
                            """);

    ConfigOption<Boolean> IGNORE_BABY_VILLAGERS =
            CONFIG.option("ignore_baby_villagers", true)
                    .description("Whether baby villagers should be ignored by villager hoppers.");

    ConfigOption<Boolean> IGNORE_CLAIMED_VILLAGERS =
            CONFIG.option("ignore_claimed_villagers", true)
                    .description("Whether claimed villagers should be ignored by villager hoppers.");

    ConfigOption<Boolean> HOPPER_RECIPE =
            CONFIG.option("hopper_recipe", true)
                    .description("""
                            Whether to register the villager hopper recipe.
                            If this is disabled, players won't be able to craft villager hoppers regularly.
                            """);

    ConfigOption<Boolean> HOPPER_BLOCK_DISPLAY =
            CONFIG.option("hopper_block_display", true)
                    .description("""
                            Weather to display the emerald frame on villager hoppers.
                            WARNING: It might be hard to distinguish villager hoppers from regular
                            hoppers when this setting is disabled.
                            """);

    ConfigOption<Float> HOPPER_BLOCK_DISPLAY_VIEW_RANGE =
            CONFIG.option("hopper_block_display_view_range", 1.0f)
                    .description("The range at which the emerald frame should be displayed..");

    ConfigOption<Integer> PARTNER_LIMIT_PER_PLAYER =
            CONFIG.option("partner_limit_per_player", 10)
                    .header("""
                            ---------------------------------------------------------
                            Partner Settings
                            ---------------------------------------------------------
                            """)
                    .description("Maximum number of partners per player.");

    ConfigOption<Boolean> VALIDATE_PARTNER_NAMES =
            CONFIG.option("validate_partner_names", true)
                    .description("""
                            Whether to validate partner names.
                            If this is enabled, players can only add players that played on the
                            server before as trading partners.
                            If this is disabled, players can add any name as a trading partner,
                            even if that player doesn't exist.
                            """);

    ConfigOption<Boolean> SHOW_TRADES =
            CONFIG.option("show_trades", true)
                    .header("""
                            ---------------------------------------------------------
                            Trade Settings
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Whether to show the trades of villagers when they are picked up.
                            Only relevant trades are shown.
                            """);

    ConfigOption<Boolean> FORMAT_TRADES =
            CONFIG.option("format_trades", true)
                    .description("""
                            Format relevant trades on picked up villagers.
                            This will hide trades that are most likely not useful,
                            and highlight important trades
                            with custom formatting and emojis.
                            """);

    ConfigOption<Integer> COOLDOWN =
            CONFIG.option("cooldown", 0)
                    .header("""
                            ---------------------------------------------------------
                            Other
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Cooldown for picking up and claiming villagers in seconds.
                            This is useful to prevent players from collecting villagers very quickly.
                            Claimed villagers will not be affected by the cooldown.
                            """);

    ConfigOption<Boolean> ALLOW_ZOMBIE_VILLAGERS =
            CONFIG.option("allow_zombie_villagers", true)
                    .description("""
                            Whether to allow zombie villagers to be picked up.
                            If this is disabled, zombie villagers will not be picked up by villager hoppers.
                            """);


    ConfigOption<Boolean> CLAIMED_VILLAGERS_BYPASS_PERMISSIONS =
            CONFIG.option("claimed_villagers_bypass_permissions", false)
                    .description("""
                            Whether picking up claimed villagers should bypass permissions.
                            If this is enabled, players can pick up villagers they claimed
                            even if they don't have the "clickvillagers.pickup" permission.
                            """);

    ConfigOption<Boolean> ENABLE_DISPENSERS =
            CONFIG.option("enable_dispensers", true)
                    .description("""
                            Whether dispensers can dispense picked up villagers.
                            """);

    ConfigOption<Map<String, Integer>> CUSTOM_MODEL_DATAS =
            CONFIG.option("custom_model_datas", (Map<String, Integer>) new HashMap<>(Map.of(
                            "baby", 0,
                            "zombie", 0,
                            "librarian", 0
                    )))
                    .mapOf(String.class, Integer.class)
                    .description("""
                            Set a custom model data for the picked up villagers based on professions.
                            This is useful for resource packs that want to change the model/texture of picked up villagers.
                            Value 0 will not change the model.
                            """);

    static String getCustomModelDataKey(Villager.Profession profession, boolean baby, boolean zombie) {
        if (baby) return "baby";
        if (zombie) return "zombie";
        return profession.toString().toLowerCase();
    }
}
