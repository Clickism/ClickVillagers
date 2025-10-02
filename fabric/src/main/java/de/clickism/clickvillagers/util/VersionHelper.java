/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.MinecraftServer;
//? if >=1.21.9
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.UserCache;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VersionHelper {
    public static void playSound(PlayerEntity player, SoundEvent soundEvent, SoundCategory category, float volume, float pitch) {
        //? if >=1.20.5 {
        player.playSoundToPlayer(soundEvent, category, volume, pitch);
        //?} else
        /*player.playSound(soundEvent, category, volume, pitch);*/
    }

    public static ItemStack getFirstBuyItem(TradeOffer offer) {
        //? if <1.20.5 {
        /*return offer.getAdjustedFirstBuyItem();
        *///?} else
        return offer.getDisplayedFirstBuyItem();
    }

    public static ItemStack getSecondBuyItem(TradeOffer offer) {
        //? if <1.20.5 {
        /*return offer.getSecondBuyItem();
        *///?} else
        return offer.getDisplayedSecondBuyItem();
    }

    public static ItemStack getSelectedStack(PlayerInventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedStack();
         //?} else
        /*return inventory.getMainHandStack();*/
    }

    public static int getSelectedSlot(PlayerInventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
         //?} else
        /*return inventory.selectedSlot;*/
    }

    public static MinecraftServer getServer(Entity entity) {
        //? if >=1.21.9 {
        return entity.getEntityWorld().getServer();
        //?} else
        /*return entity.getServer();*/
    }

    public static World getWorld(Entity entity) {
        //? if >=1.21.9 {
        return entity.getEntityWorld();
        //?} else
        /*return entity.getWorld();*/
    }

    public static Optional<String> getPlayerName(UUID uuid, MinecraftServer server) {
        //? if >= 1.21.9 {
        return server.getApiServices().nameToIdCache().getByUuid(uuid)
                .map(PlayerConfigEntry::name);
        //?} else {
        /*UserCache userCache = server.getUserCache();
        if (userCache == null) return Optional.empty();
        return userCache.getByUuid(uuid).map(GameProfile::getName);
        *///?}
    }
}
