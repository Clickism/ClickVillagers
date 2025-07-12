/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;

public class ClickVillagersConfig {
    public static final Config CONFIG =
            Config.of("config/ClickVillagers/config.yml")
                    .version(4)
                    .header("""
                            ---------------------------------------------------------
                            ClickVillagers Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

    public static final ConfigOption<Boolean> CHECK_UPDATES =
            CONFIG.optionOf("check_updates", true)
                    .description("""
                            Whether to check for updates on server start.
                            Recommended!
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> CLAIMED_DAMAGE =
            CONFIG.optionOf("claimed_damage", false)
                    .description("Whether claimed villagers can take damage.")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> CLAIMED_IMMUNE_KILL_COMMAND =
            CONFIG.optionOf("claimed_immune_kill_command", true)
                    .description("""
                            Whether claimed villagers are immune to the /kill command.
                            Won't do anything if "claimed-villagers-take-damage" is enabled.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> ENABLE_HOPPERS =
            CONFIG.optionOf("tick_hoppers", true)
                    .header("""
                            ---------------------------------------------------------
                            Hopper Settings
                            ---------------------------------------------------------
                            """)
                    .description("Whether hoppers should tick.")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> IGNORE_BABY_VILLAGERS =
            CONFIG.optionOf("ignore_baby_villagers", true)
                    .description("Whether baby villagers should be ignored by villager hoppers.")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> IGNORE_CLAIMED_VILLAGERS =
            CONFIG.optionOf("ignore_claimed_villagers", true)
                    .description("Whether claimed villagers should be ignored by villager hoppers.")
                    .appendDefaultValue();

    public static final ConfigOption<Integer> PARTNER_LIMIT_PER_PLAYER =
            CONFIG.optionOf("partner_limit_per_player", 10)
                    .header("""
                            ---------------------------------------------------------
                            Partner Settings
                            ---------------------------------------------------------
                            """)
                    .description("Maximum number of partners per player.")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> SHOW_TRADES =
            CONFIG.optionOf("show_trades", true)
                    .header("""
                            ---------------------------------------------------------
                            Trade Settings
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Whether to show the trades of villagers when they are picked up.
                            Only relevant trades are shown.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> FORMAT_TRADES =
            CONFIG.optionOf("format_trades", true)
                    .description("""
                            Format relevant trades on picked up villagers.
                            This will hide trades that are most likely not useful,
                            and highlight important trades
                            with custom formatting and emojis.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Long> COOLDOWN =
            CONFIG.optionOf("cooldown", 0L)
                    .header("""
                            ---------------------------------------------------------
                            Other
                            ---------------------------------------------------------
                            """)
                    .description("""
                            Cooldown for picking up and claiming villagers in seconds.
                            This is useful to prevent players from collecting villagers very quickly.
                            Claimed villagers will not be affected by the cooldown.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> ALLOW_ZOMBIE_VILLAGERS =
            CONFIG.optionOf("allow_zombie_villagers", true)
                    .description("""
                            Whether to allow zombie villagers to be picked up.
                            If this is disabled, zombie villagers will not be picked up by villager hoppers.
                            """)
                    .appendDefaultValue();

}