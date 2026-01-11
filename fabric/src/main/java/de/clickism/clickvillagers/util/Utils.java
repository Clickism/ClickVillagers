/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
//? if <1.20.5 {
/*import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import java.util.stream.Stream;
*///?}

public class Utils {
    /**
     * Title cases a string. (i.E.: "HELLO world" -> "Hello World")
     *
     * @param string the string to title case
     * @return the title cased string
     */
    public static String titleCase(String string) {
        return capitalize(string.toLowerCase());
    }

    /**
     * Capitalizes the first letter of each word in a string.
     *
     * @param string the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String string) {
        String[] words = string.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            capitalizedString.append(capitalizeWord(word)).append(" ");
        }
        return capitalizedString.toString().trim();
    }

    private static String capitalizeWord(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static void offerToHand(Player player, ItemStack itemStack) {
        Inventory inventory = player.getInventory();
        int selectedSlot = VersionHelper.getSelectedSlot(inventory);
        if (inventory.getItem(selectedSlot).isEmpty()) {
            inventory.add(selectedSlot, itemStack);
            return;
        }
        inventory.placeItemBackInInventory(itemStack);
    }

    /**
     * Formats an item stack with its amount into a string. (i.E.: "64 Diamond")
     *
     * @param item the item stack to format
     * @return the formatted item string
     */
    public static String formatItem(ItemStack item) {
        return item.getCount() + " " + formatItem(item.getItem());
    }

    public static String formatItem(Item item) {
        return titleCase(item.getDefaultInstance().getItemHolder().unwrapKey()
                //? if >=1.21.11 {
                .map(ResourceKey::identifier)
                //?} else
                //.map(ResourceKey::location)
                .map(Identifier::getPath)
                .map(s -> s.replace("_", " "))
                .orElse("?"));
    }

    public static String toRomanNumeral(int number) {
        return switch (number) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(number);
        };
    }

    //? if <1.20.5 {
    /*public static Stream<String> streamEnchantments(NbtList enchantments) {
        if (enchantments == null || enchantments.isEmpty()) return Stream.empty();
        return enchantments.stream()
                .map(element -> {
                    if (!(element instanceof NbtCompound compound)) return "?";
                    int level = compound.getShort("lvl");
                    Identifier id = Identifier.tryParse(compound.getString("id"));
                    if (id == null) return "?";
                    String enchantment = id.getPath().replace("_", " ");
                    String levelString = Utils.toRomanNumeral(level);
                    return Utils.titleCase(enchantment) + " " + levelString;
                });
    }
    *///?}
}
