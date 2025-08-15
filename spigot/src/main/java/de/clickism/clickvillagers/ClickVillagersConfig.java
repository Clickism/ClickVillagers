/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.configured.Config;
import de.clickism.configured.ConfigOption;
import de.clickism.clickvillagers.message.Message;
import org.bukkit.entity.Villager;

import java.util.HashMap;
import java.util.Map;

public class ClickVillagersConfig {
    public static final Config CONFIG =
            Config.of("plugins/ClickVillagers/config.yml")
                    .version(5)
                    .oldKeyGenerator(key -> key.replace('_', '-'))
                    .header("""
                            ---------------------------------------------------------
                            ClickVillagers Config
                            NOTE: RELOAD/RESTART SERVER FOR CHANGES TO TAKE EFFECT
                            ---------------------------------------------------------
                            """);

    public static final ConfigOption<String> LANGUAGE =
            CONFIG.optionOf("language", "en_US")
                    .description("""
                            Language of the plugin.
                            Available languages: en_US, de_DE, ru_RU, vi_VN
                            """)
                    .appendDefaultValue()
                    .onLoad(lang -> Message.LOCALIZATION
                            .language(lang)
                            .load());

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

    public static final ConfigOption<Boolean> TICK_HOPPERS =
            CONFIG.optionOf("tick_hoppers", true)
                    .header("""
                            ---------------------------------------------------------
                            Hopper Settings
                            ---------------------------------------------------------
                            """)
                    .description("Whether hoppers should tick.")
                    .appendDefaultValue();

    public static final ConfigOption<Integer> HOPPER_TICK_RATE =
            CONFIG.optionOf("hopper_tick_rate", 20)
                    .description("""
                            The rate at which villager hoppers should tick. (in ticks)
                            You can increase this value to reduce lag, but it will also reduce
                            the speed at which villagers are picked up.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Integer> HOPPER_LIMIT_PER_CHUNK =
            CONFIG.optionOf("hopper_limit_per_chunk", -1)
                    .description("""
                            The maximum amount of villager hoppers a chunk can hold.
                            You can use this to prevent lag/spam.
                            -1 will disable the limit.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> IGNORE_BABY_VILLAGERS =
            CONFIG.optionOf("ignore_baby_villagers", true)
                    .description("Whether baby villagers should be ignored by villager hoppers.")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> IGNORE_CLAIMED_VILLAGERS =
            CONFIG.optionOf("ignore_claimed_villagers", true)
                    .description("Whether claimed villagers should be ignored by villager hoppers.")
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> HOPPER_RECIPE =
            CONFIG.optionOf("hopper_recipe", true)
                    .description("""
                            Whether to register the villager hopper recipe.
                            If this is disabled, players won't be able to craft villager hoppers regularly.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Boolean> HOPPER_BLOCK_DISPLAY =
            CONFIG.optionOf("hopper_block_display", true)
                    .description("""
                            Weather to display the emerald frame on villager hoppers.
                            WARNING: It might be hard to distinguish villager hoppers from regular
                            hoppers when this setting is disabled.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Float> HOPPER_BLOCK_DISPLAY_VIEW_RANGE =
            CONFIG.optionOf("hopper_block_display_view_range", 1.0f)
                    .description("The range at which the emerald frame should be displayed..")
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

    public static final ConfigOption<Boolean> VALIDATE_PARTNER_NAMES =
            CONFIG.optionOf("validate_partner_names", true)
                    .description("""
                            Whether to validate partner names.
                            If this is enabled, players can only add players that played on the
                            server before as trading partners.
                            If this is disabled, players can add any name as a trading partner,
                            even if that player doesn't exist.
                            """)
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

    public static final ConfigOption<Integer> COOLDOWN =
            CONFIG.optionOf("cooldown", 0)
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


    public static final ConfigOption<Boolean> CLAIMED_VILLAGERS_BYPASS_PERMISSIONS =
            CONFIG.optionOf("claimed_villagers_bypass_permissions", false)
                    .description("""
                            Whether picking up claimed villagers should bypass permissions.
                            If this is enabled, players can pick up villagers they claimed
                            even if they don't have the "clickvillagers.pickup" permission.
                            """)
                    .appendDefaultValue();

    public static final ConfigOption<Map<String, Integer>> CUSTOM_MODEL_DATAS =
            CONFIG.optionOf("custom_model_datas", new HashMap<>() {{
                        put("baby", 0);
                        put("zombie", 0);
                        put("librarian", 0);
                    }}, String.class,  Integer.class)
                    .description("""
                            Set a custom model data for the picked up villagers based on professions.
                            This is useful for resource packs that want to change the model/texture of picked up villagers.
                            Value 0 will not change the model.
                            """);

    public static String getCustomModelDataKey(Villager.Profession profession, boolean baby, boolean zombie) {
        if (baby) return "baby";
        if (zombie) return "zombie";
        return profession.toString().toLowerCase();
    }
}
