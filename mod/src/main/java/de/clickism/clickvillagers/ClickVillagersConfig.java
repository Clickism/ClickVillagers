/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;

public interface ClickVillagersConfig {
    Config CONFIG =
            Config.of("config/ClickVillagers/config.yml")
                    .version(5)
                    .appendDefaults()
                    .header("""
                            ---------------------------------------------------------
                            ClickVillagers Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

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


    ConfigOption<Boolean> ENABLE_HOPPERS =
            CONFIG.option("enable_hoppers", true)
                    .header("""
                            ---------------------------------------------------------
                            Hopper Settings
                            ---------------------------------------------------------
                            """)
                    .description("Whether villager hoppers should be enabled.");

    ConfigOption<Boolean> IGNORE_BABY_VILLAGERS =
            CONFIG.option("ignore_baby_villagers", true)
                    .description("Whether baby villagers should be ignored by villager hoppers.");

    ConfigOption<Boolean> IGNORE_CLAIMED_VILLAGERS =
            CONFIG.option("ignore_claimed_villagers", true)
                    .description("Whether claimed villagers should be ignored by villager hoppers.");

    ConfigOption<Integer> PARTNER_LIMIT_PER_PLAYER =
            CONFIG.option("partner_limit_per_player", 10)
                    .header("""
                            ---------------------------------------------------------
                            Partner Settings
                            ---------------------------------------------------------
                            """)
                    .description("Maximum number of partners per player.");

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

    ConfigOption<Long> COOLDOWN =
            CONFIG.option("cooldown", 0L)
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

    ConfigOption<Boolean> ENABLE_PICKUP =
            CONFIG.option("enable_pickup", true)
                    .description("""
                            Whether players can pick up villagers.
                            Already picked up villagers will not be affected by this setting.
                            """);

    ConfigOption<Boolean> ENABLE_CLAIMS =
            CONFIG.option("enable_claims", true)
                    .description("""
                            Whether players can claim villagers.
                            Already claimed villagers will not be affected by this setting.
                            """);

    ConfigOption<Boolean> ENABLE_ANCHORS =
            CONFIG.option("enable_anchors", true)
                    .description("""
                            Whether players can anchor villagers.
                            """);

}