/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.villager;

import de.clickism.clickvillagers.util.ColoredItemHelper;
import de.clickism.clickvillagers.util.Utils;

import de.clickism.clickvillagers.util.VersionHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import org.jetbrains.annotations.Nullable;

//? if >1.20.6 {
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.resources.ResourceKey;
//?} else {
/*import net.minecraft.item.EnchantedBookItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
*///?}
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.world.item.Items.*;
import static de.clickism.clickvillagers.util.ColoredItemHelper.*;

public class TradeInfoProviders {

    private static final Function<ItemStack, String> ITEM_FORMATTER = stack -> {
        Item item = stack.getItem();
        String prefix = getPrefix(item);
        prefix = (prefix == null) ? "" : prefix + " ";
        boolean isTool = !stack.isStackable();
        String name = (isTool)
                ? Utils.formatItem(item)
                : Utils.formatItem(stack);
        return prefix + name;
    };

    public static final TradeInfoProvider ALL_TRADES = TradeInfoProvider.builder()
            .filterIngredients(item -> true)
            .filterResults(item -> true)
            .build();

    public static final TradeInfoProvider LIBRARIAN = TradeInfoProvider.builder()
            .acceptResults(ENCHANTED_BOOK)
            .ingredientFormatter(item -> {
                if (!item.is(EMERALD)) return null;
                return item.getCount() + " Emerald";
            })
            .resultFormatter(TradeInfoProviders::formatEnchantedBook)
            .build();

    public static final TradeInfoProvider FARMER = TradeInfoProvider.builder()
            .acceptIngredients(WHEAT, BEETROOT, CARROT, POTATO, PUMPKIN, MELON)
            .acceptResults(GOLDEN_CARROT, APPLE, BREAD)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider SMITH = TradeInfoProvider.builder()
            .acceptIngredients(COAL)
            .acceptResults(DIAMOND_AXE, DIAMOND_PICKAXE, DIAMOND_SHOVEL, DIAMOND_HOE, DIAMOND_SWORD, DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, SHIELD)
            .singleFormatter(ITEM_FORMATTER)
            .formatEnchantments()
            .build();

    public static final TradeInfoProvider BUTCHER = TradeInfoProvider.builder()
            .acceptIngredients(CHICKEN, PORKCHOP, RABBIT, SWEET_BERRIES, KELP)
            .acceptResults(COOKED_CHICKEN, COOKED_PORKCHOP)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider FISHERMAN = TradeInfoProvider.builder()
            .acceptIngredients(STRING, COAL)
            .acceptResults(CAMPFIRE, FISHING_ROD)
            .singleFormatter(ITEM_FORMATTER)
            .formatEnchantments()
            .build();

    public static final TradeInfoProvider LEATHERWORKER = TradeInfoProvider.builder()
            .acceptIngredients(LEATHER, FLINT, RABBIT_HIDE,
                    //? if >=1.20.5 {
                    TURTLE_SCUTE
                    //?} else
                    /*SCUTE*/
                    )
            .acceptResults(LEATHER_HORSE_ARMOR, SADDLE)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider CLERIC = TradeInfoProvider.builder()
            .acceptIngredients(GOLD_INGOT)
            .acceptResults(REDSTONE, LAPIS_LAZULI, GLOWSTONE, ENDER_PEARL, EXPERIENCE_BOTTLE)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider CARTOGRAPHER = TradeInfoProvider.builder()
            .acceptIngredients(PAPER)
            .acceptResults(ITEM_FRAME, MAP)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider FLETCHER = TradeInfoProvider.builder()
            .acceptIngredients(STICK, FLINT, STRING)
            .filterResults(item -> {
                Item itemType = item.getItem();
                if (itemType == ARROW) return true;
                if (itemType == BOW) {
                    return !item.getEnchantments().isEmpty();
                }
                return false;
            })
            .singleFormatter(ITEM_FORMATTER)
            .formatEnchantments()
            .build();

    public static final TradeInfoProvider MASON = TradeInfoProvider.builder()
            .acceptIngredients(CLAY_BALL, QUARTZ)
            .acceptResults(Stream.concat(
                    Stream.of(BRICK, QUARTZ_PILLAR, QUARTZ_BLOCK, TERRACOTTA),
                    Stream.concat(
                            ColoredItemHelper.DYED_TERRACOTTA.asList().stream(),
                            ColoredItemHelper.GLAZED_TERRACOTTA.asList().stream()
                    )
            ).toList())
//            .acceptResults(BRICK, QUARTZ_PILLAR, QUARTZ_BLOCK,
//                    // Normal terracotta
//                    TERRACOTTA,
//                    DYED_TERRACOTTA.asList().stream().toArray(Item[]::new),
//                    WHITE_TERRACOTTA,
//                    ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA, LIGHT_BLUE_TERRACOTTA,
//                    YELLOW_TERRACOTTA, LIME_TERRACOTTA, PINK_TERRACOTTA,
//                    GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, CYAN_TERRACOTTA,
//                    PURPLE_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA,
//                    GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA,
//                    // Glazed terracotta
//                    WHITE_GLAZED_TERRACOTTA, ORANGE_GLAZED_TERRACOTTA,
//                    MAGENTA_GLAZED_TERRACOTTA, LIGHT_BLUE_GLAZED_TERRACOTTA,
//                    YELLOW_GLAZED_TERRACOTTA, LIME_GLAZED_TERRACOTTA,
//                    PINK_GLAZED_TERRACOTTA, GRAY_GLAZED_TERRACOTTA,
//                    LIGHT_GRAY_GLAZED_TERRACOTTA, CYAN_GLAZED_TERRACOTTA,
//                    PURPLE_GLAZED_TERRACOTTA, BLUE_GLAZED_TERRACOTTA,
//                    BROWN_GLAZED_TERRACOTTA, GREEN_GLAZED_TERRACOTTA,
//                    RED_GLAZED_TERRACOTTA, BLACK_GLAZED_TERRACOTTA)
            .ingredientFormatter(ITEM_FORMATTER)
            .resultFormatter(item -> {
                Item material = item.getItem();
                if (material == BRICK || material == QUARTZ_PILLAR
                        || material == QUARTZ_BLOCK || material == EMERALD) {
                    return ITEM_FORMATTER.apply(item);
                }
                return "§6🪨 " + Utils.formatItem(item);
            })
            .build();

    public static final TradeInfoProvider SHEPHERD = TradeInfoProvider.builder()
            .acceptIngredients(
                    ColoredItemHelper.WOOL.white(),
                    ColoredItemHelper.WOOL.brown(),
                    ColoredItemHelper.WOOL.black(),
                    ColoredItemHelper.WOOL.gray()
            )
            .acceptResults(SHEARS, PAINTING)
            .singleFormatter(ITEM_FORMATTER)
            .build();

    //? if >=1.21.5 {
    private static final Map<ResourceKey<VillagerProfession>, TradeInfoProvider> PROVIDERS = Map.ofEntries(
    //?} else
    //private static final Map<VillagerProfession, TradeInfoProvider> PROVIDERS = Map.ofEntries(
            Map.entry(VillagerProfession.LIBRARIAN, LIBRARIAN),
            Map.entry(VillagerProfession.FARMER, FARMER),
            Map.entry(VillagerProfession.TOOLSMITH, SMITH),
            Map.entry(VillagerProfession.WEAPONSMITH, SMITH),
            Map.entry(VillagerProfession.ARMORER, SMITH),
            Map.entry(VillagerProfession.BUTCHER, BUTCHER),
            Map.entry(VillagerProfession.FISHERMAN, FISHERMAN),
            Map.entry(VillagerProfession.LEATHERWORKER, LEATHERWORKER),
            Map.entry(VillagerProfession.CLERIC, CLERIC),
            Map.entry(VillagerProfession.CARTOGRAPHER, CARTOGRAPHER),
            Map.entry(VillagerProfession.FLETCHER, FLETCHER),
            Map.entry(VillagerProfession.MASON, MASON),
            Map.entry(VillagerProfession.SHEPHERD, SHEPHERD)
    );

    //? if >=1.21.5 {
    public static TradeInfoProvider getProvider(ResourceKey<VillagerProfession> profession) {
        return PROVIDERS.getOrDefault(profession, ALL_TRADES);
    }
    //?} else {
    /*public static TradeInfoProvider getProvider(VillagerProfession profession) {
        return PROVIDERS.getOrDefault(profession, ALL_TRADES);
    }
    *///?}

    private static final Map<Item, String> PREFIX_MAP = Map.<Item, String>ofEntries(
            Map.entry(ENCHANTED_BOOK, "§d📖"),

            Map.entry(GOLDEN_CARROT, "§e🥕"),
            Map.entry(APPLE, "§c🍎"),

            Map.entry(WHEAT, "§e🌾"),
            Map.entry(CARROT, "§6🥕"),
            Map.entry(POTATO, "§6🥔"),
            Map.entry(PUMPKIN, "§6🎃"),
            Map.entry(MELON, "§a🍉"),

            Map.entry(COAL, "§8🪨"),
            Map.entry(FLINT, "§8🪨"),

            Map.entry(DIAMOND_SWORD, "§b🗡"),

            Map.entry(DIAMOND_AXE, "§b⛏"),
            Map.entry(DIAMOND_PICKAXE, "§b⛏"),
            Map.entry(DIAMOND_SHOVEL, "§b⛏"),
            Map.entry(DIAMOND_HOE, "§b⛏"),

            Map.entry(DIAMOND_HELMET, "§b👕"),
            Map.entry(DIAMOND_CHESTPLATE, "§b👕"),
            Map.entry(DIAMOND_LEGGINGS, "§b👕"),
            Map.entry(DIAMOND_BOOTS, "§b👕"),

            Map.entry(CHICKEN, "§e🐓"),
            Map.entry(PORKCHOP, "§e🐷"),
            Map.entry(RABBIT, "§e🐰"),
            Map.entry(SWEET_BERRIES, "§c🍇"),
            Map.entry(KELP, "§a🌿"),

            Map.entry(COOKED_CHICKEN, "§6🐓"),
            Map.entry(COOKED_PORKCHOP, "§6🐷"),

            Map.entry(STRING, "§f🪡"),
            Map.entry(CAMPFIRE, "§c🔥"),
            Map.entry(FISHING_ROD, "§6🎣"),

            Map.entry(SHIELD, "§6⛨"),

            Map.entry(LEATHER, "§6🐄"),
            Map.entry(RABBIT_HIDE, "§e🐇"),

            //? if >=1.20.5 {
            Map.entry(TURTLE_SCUTE, "§a🐢"),
             //?} else
            /*Map.entry(SCUTE, "§a🐢"),*/
            Map.entry(SADDLE, "§6🐴"),

            Map.entry(REDSTONE, "§c💎"),
            Map.entry(GOLD_INGOT, "§6💎"),
            Map.entry(LAPIS_LAZULI, "§9💎"),
            Map.entry(GLOWSTONE, "§e🌟"),
            Map.entry(ENDER_PEARL, "§3◎"),
            Map.entry(EXPERIENCE_BOTTLE, "§d🧪"),

            Map.entry(PAPER, "§f📄"),
            Map.entry(MAP, "§e📄"),
            Map.entry(ITEM_FRAME, "§6🖼"),
            Map.entry(PAINTING, "§6🖼"),

            Map.entry(STICK, "§6🪃"),
            Map.entry(ARROW, "§f➵"),
            Map.entry(BOW, "§6🏹"),

            Map.entry(CLAY_BALL, "§3🪨"),
            Map.entry(BRICK, "§c🧱"),
            Map.entry(QUARTZ, "§f💎"),
            Map.entry(QUARTZ_BLOCK, "§f💎"),
            Map.entry(QUARTZ_PILLAR, "§f💎"),

            Map.entry(ColoredItemHelper.WOOL.white(), "§f🧶"),
            Map.entry(ColoredItemHelper.WOOL.brown(), "§6🧶"),
            Map.entry(ColoredItemHelper.WOOL.black(), "§8🧶"),
            Map.entry(ColoredItemHelper.WOOL.gray(), "§7🧶"),
            Map.entry(SHEARS, "§c✂")
    );

    @Nullable
    private static String getPrefix(Item item) {
        return PREFIX_MAP.get(item);
    }

    //? if >1.20.6 {
    private static String formatEnchantedBook(ItemStack item) {
        ItemEnchantments enchants = item.get(DataComponents.STORED_ENCHANTMENTS);
        if (enchants == null) return "";
        String enchantments = enchants.entrySet().stream()
                .map(entry -> {
                    String enchantment = entry.getKey().unwrapKey()
                            .map(VersionHelper::identifier)
                            .map(Identifier::getPath)
                            .map(s -> s.replace("_", " "))
                            .orElse("?");
                    String level = Utils.toRomanNumeral(entry.getIntValue());
                    return Utils.titleCase(enchantment) + " " + level;
                })
                .collect(Collectors.joining(" + "));
        return "§d📖 " + enchantments;
    }
    //?} else {
    /*private static String formatEnchantedBook(ItemStack item) {
        NbtList enchants = EnchantedBookItem.getEnchantmentNbt(item);
        if (enchants == null || enchants.isEmpty()) return "";
        String string = Utils.streamEnchantments(enchants)
                .collect(Collectors.joining(" + "));
        return "§d📖 " + string;
    }
    *///?}
}
