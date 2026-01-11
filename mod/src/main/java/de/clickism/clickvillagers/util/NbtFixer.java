/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbtFixer {
    //? if >=1.21.5 {
    // DataFixers didn't work so do it manually
    public static void applyDataFixes(CompoundTag nbt) {
        try {
            CompoundTag offersCompound = nbt.getCompound("Offers").orElseThrow();
            ListTag recipesList = offersCompound.getListOrEmpty("Recipes");
            for (Tag recipe : recipesList) {
                if (!(recipe instanceof CompoundTag recipeCompound)) continue;
                removeLevelsKey(recipeCompound.getCompoundOrEmpty("buy"));
                removeLevelsKey(recipeCompound.getCompoundOrEmpty("sell"));
            }
        } catch (NoSuchElementException ignored) {
        }
    }

    private static void removeLevelsKey(CompoundTag nbtCompound) {
        CompoundTag componentsCompound = nbtCompound.getCompound("components").orElse(null);
        if (componentsCompound == null) return;
        removeLevelsKeyFromEnchantments(componentsCompound, "minecraft:enchantments");
        removeLevelsKeyFromEnchantments(componentsCompound, "minecraft:stored_enchantments");
    }

    private static void removeLevelsKeyFromEnchantments(CompoundTag nbtCompound, String enchantmentKey) {
        CompoundTag enchantmentCompound = nbtCompound.getCompoundOrEmpty(enchantmentKey);
        CompoundTag levelsCompound = enchantmentCompound.getCompoundOrEmpty("levels");
        if (levelsCompound.isEmpty()) return;
        nbtCompound.put(enchantmentKey, levelsCompound);
    }
    //?}
}
