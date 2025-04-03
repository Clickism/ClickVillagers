/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbtFixer {
    //? if >=1.21.5 {
    // DataFixers didn't work so do it manually
    public static void applyDataFixes(NbtCompound nbt) {
        try {
            NbtCompound offersCompound = nbt.getCompound("Offers").orElseThrow();
            NbtList recipesList = offersCompound.getListOrEmpty("Recipes");
            for (NbtElement recipe : recipesList) {
                if (!(recipe instanceof NbtCompound recipeCompound)) continue;
                removeLevelsKey(recipeCompound.getCompoundOrEmpty("buy"));
                removeLevelsKey(recipeCompound.getCompoundOrEmpty("sell"));
            }
        } catch (NoSuchElementException ignored) {
        }
    }

    private static void removeLevelsKey(NbtCompound nbtCompound) {
        NbtCompound componentsCompound = nbtCompound.getCompound("components").orElse(null);
        if (componentsCompound == null) return;
        removeLevelsKeyFromEnchantments(componentsCompound, "minecraft:enchantments");
        removeLevelsKeyFromEnchantments(componentsCompound, "minecraft:stored_enchantments");
    }

    private static void removeLevelsKeyFromEnchantments(NbtCompound nbtCompound, String enchantmentKey) {
        NbtCompound enchantmentCompound = nbtCompound.getCompoundOrEmpty(enchantmentKey);
        NbtCompound levelsCompound = enchantmentCompound.getCompoundOrEmpty("levels");
        if (levelsCompound.isEmpty()) return;
        nbtCompound.put(enchantmentKey, levelsCompound);
    }
    //?}
}
