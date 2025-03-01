/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import me.clickism.clickvillagers.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TradeInfoProviders {

    private static final Function<ItemStack, String> ITEM_FORMATTER = item -> {
        String prefix = getPrefix(item.getType());
        if (prefix == null) {
            return TradeInfoProvider.DEFAULT_FORMATTER.apply(item);
        }
        return prefix + " " + Utils.formatItem(item);
    };

    public static final TradeInfoProvider ALL_TRADES = TradeInfoProvider.builder()
            .filterIngredients(item -> true)
            .filterResults(item -> true)
            .build();

    public static final TradeInfoProvider LIBRARIAN = TradeInfoProvider.builder()
            .acceptResults(Material.ENCHANTED_BOOK)
            .ingredientFormatter(item -> {
                if (item.getType() != Material.EMERALD) return null;
                return item.getAmount() + " Emeralds";
            })
            .resultFormatter(item -> {
                if (!(item.getItemMeta() instanceof EnchantmentStorageMeta meta)) return "?";
                return "&d📖 " + meta.getStoredEnchants().entrySet().stream()
                        .map(entry -> {
                            String enchantment = entry.getKey().getKey().getKey().replace("_", " ");
                            String level = toRomanNumeral(entry.getValue());
                            return Utils.titleCase(enchantment) + " " + level;
                        })
                        .collect(Collectors.joining(" + "));
            })
            .build();

    public static final TradeInfoProvider FARMER = TradeInfoProvider.builder()
            .acceptIngredients(Material.WHEAT, Material.BEETROOT, Material.CARROT, Material.POTATO, Material.PUMPKIN, Material.MELON)
            .acceptResults(Material.GOLDEN_CARROT, Material.APPLE, Material.BREAD)
            .itemFormatter(ITEM_FORMATTER)
            .build();

    private static final Set<Material> DIAMOND_TOOLS = Set.of(Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE,
            Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE, Material.DIAMOND_SWORD);

    private static final Set<Material> DIAMOND_ARMOR = Set.of(Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);

    private static final Set<Material> DIAMOND_ITEMS = Stream.concat(DIAMOND_TOOLS.stream(), DIAMOND_ARMOR.stream())
            .collect(Collectors.toSet());

    public static final TradeInfoProvider SMITH = TradeInfoProvider.builder()
            .acceptIngredients(Material.COAL)
            .acceptResults(Stream.concat(DIAMOND_ITEMS.stream(), Stream.of(Material.SHIELD))
                    .toArray(Material[]::new))
            .ingredientFormatter(ITEM_FORMATTER)
            .resultFormatter(item -> {
                Material material = item.getType();
                if (!DIAMOND_ITEMS.contains(material)) return ITEM_FORMATTER.apply(item);
                ItemMeta meta = item.getItemMeta();
                if (meta == null) return ITEM_FORMATTER.apply(item);
                String enchantments = meta.getEnchants().entrySet().stream()
                        .map(entry -> {
                            String enchantment = entry.getKey().getKey().getKey().replace("_", " ");
                            String level = toRomanNumeral(entry.getValue());
                            return "\n                        &d   &7" + Utils.titleCase(enchantment) + " " + level;
                        })
                        .collect(Collectors.joining());
                String icon = "";
                if (DIAMOND_TOOLS.contains(material)) {
                    icon = (material == Material.DIAMOND_SWORD) ? "🗡" : "⛏";
                } else {
                    icon = "👕";
                }
                String name = "&b" + icon + " " + Utils.formatMaterial(material);
                return name + enchantments;
            })
            .build();

    public static final TradeInfoProvider BUTCHER = TradeInfoProvider.builder()
            .acceptIngredients(Material.CHICKEN, Material.PORKCHOP, Material.RABBIT,
                    Material.SWEET_BERRIES, Material.KELP)
            .acceptResults(Material.COOKED_CHICKEN, Material.COOKED_PORKCHOP)
            .itemFormatter(ITEM_FORMATTER)
            .build();

    public static final TradeInfoProvider FISHERMAN = TradeInfoProvider.builder()
            .acceptIngredients(Material.STRING, Material.COAL)
            .acceptResults(Material.CAMPFIRE)
            .itemFormatter(ITEM_FORMATTER)
            .build();

    public static TradeInfoProvider getProvider(Villager.Profession profession) {
        return switch (profession) {
            case LIBRARIAN -> LIBRARIAN;
            case FARMER -> FARMER;
            case TOOLSMITH,
                 WEAPONSMITH,
                 ARMORER -> SMITH;
            case BUTCHER -> BUTCHER;
            default -> ALL_TRADES;
        };
    }

    @Nullable
    private static String getPrefix(Material material) {
        return switch (material) {
            case GOLDEN_CARROT -> "&e🥕";
            case APPLE -> "&c🍎";

            case WHEAT -> "&e🌾";
            case CARROT -> "&6🥕";
            case POTATO -> "&6🥔";
            case PUMPKIN -> "&6🎃";
            case MELON -> "&a🍉";

            case COAL -> "&8🪨";

            case CHICKEN -> "&e🐓";
            case PORKCHOP -> "&e🐷";
            case RABBIT -> "&e🐰";
            case SWEET_BERRIES -> "&c🍇";
            case KELP -> "&a🌿";

            case COOKED_CHICKEN -> "&6🐓";
            case COOKED_PORKCHOP -> "&6🐷";

            case STRING -> "&f🪢";
            case CAMPFIRE -> "&🔥";

            case SHIELD -> "&6🛡️";

            default -> null;
        };
    }

    private static String toRomanNumeral(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }
}
