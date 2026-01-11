/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import net.minecraft.world.item.enchantment.Enchantment;
//? if >=1.21.11 {
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.permissions.PermissionSet;
//?}
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.server.MinecraftServer;
//? if >=1.21.9
import net.minecraft.server.players.NameAndId;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.server.players.CachedUserNameToIdResolver;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VersionHelper {
    public static void playSound(Player player, SoundEvent soundEvent, SoundSource category, float volume, float pitch) {
        //? if >=1.21.11 {
        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                soundEvent,
                category,
                volume,
                pitch
        );
        //?} elif >=1.20.5 {
        /*player.playSoundToPlayer(soundEvent, category, volume, pitch);
         *///?} else
        /*player.playSound(soundEvent, category, volume, pitch);*/
    }

    public static ItemStack getFirstBuyItem(MerchantOffer offer) {
        //? if <1.20.5 {
        /*return offer.getAdjustedFirstBuyItem();
         *///?} else
        return offer.getCostA();
    }

    public static ItemStack getSecondBuyItem(MerchantOffer offer) {
        //? if <1.20.5 {
        /*return offer.getSecondBuyItem();
         *///?} else
        return offer.getCostB();
    }

    public static ItemStack getSelectedStack(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedItem();
        //?} else
        /*return inventory.getMainHandStack();*/
    }

    public static int getSelectedSlot(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
        //?} else
        /*return inventory.selectedSlot;*/
    }

    public static MinecraftServer getServer(Entity entity) {
        //? if >=1.21.9 {
        return entity.level().getServer();
        //?} else
        /*return entity.getServer();*/
    }

    public static Level getWorld(Entity entity) {
        //? if >=1.21.9 {
        return entity.level();
        //?} else
        /*return entity.getWorld();*/
    }

    public static Optional<String> getPlayerName(UUID uuid, MinecraftServer server) {
        //? if >= 1.21.9 {
        return server.services().nameToIdCache().get(uuid)
                .map(NameAndId::name);
        //?} else {
        /*UserCache userCache = server.getUserCache();
        if (userCache == null) return Optional.empty();
        return userCache.getByUuid(uuid).map(GameProfile::getName);
        *///?}
    }

    public static boolean isOp(Player player) {
        //? if >=1.21.11 {
        var perms = player.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        //?} else
        /*return player.hasPermissionLevel(3);*/
    }

    public static boolean isOp(CommandSourceStack source) {
        //? if >=1.21.11 {
        var perms = source.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        //?} else
        /*return source.hasPermissionLevel(3);*/
    }
}
