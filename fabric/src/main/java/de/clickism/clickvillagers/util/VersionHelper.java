/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import java.util.Optional;
import java.util.UUID;

//? if >=1.21.9
import net.minecraft.server.players.NameAndId;

//? if >=1.21.11 {
import net.minecraft.server.permissions.Permissions;
import net.minecraft.server.permissions.PermissionSet;
//?}

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
        /*player.playNotifySound(soundEvent, category, volume, pitch);
         *///?} else
        //player.playNotifySound(soundEvent, category, volume, pitch);
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
        //return inventory.getSelected();
    }

    public static int getSelectedSlot(Inventory inventory) {
        //? if >=1.21.5 {
        return inventory.getSelectedSlot();
        //?} else
        //return inventory.selected;
    }

    public static MinecraftServer getServer(Entity entity) {
        return entity.level().getServer();
    }

    public static Level getWorld(Entity entity) {
        return entity.level();
    }

    public static Optional<String> getPlayerName(UUID uuid, MinecraftServer server) {
        //? if >= 1.21.9 {
        return server.services().nameToIdCache().get(uuid)
                .map(NameAndId::name);
        //?} else {
        /*var cache = server.getProfileCache();
        if (cache == null) return Optional.empty();
        return cache.get(uuid).map(GameProfile::getName);
        *///?}
    }

    public static boolean isOp(Player player) {
        //? if >=1.21.11 {
        var perms = player.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        //?} else
        //return player.hasPermissions(3);
    }

    public static boolean isOp(CommandSourceStack source) {
        //? if >=1.21.11 {
        var perms = source.permissions();
        return perms.hasPermission(Permissions.COMMANDS_ADMIN)
               || perms.hasPermission(Permissions.COMMANDS_OWNER);
        //?} else
        //return source.hasPermission(3);
    }

    public static boolean isOpOrInSinglePlayer(CommandSourceStack source) {
        var player = source.getPlayer();
        if (player != null && player.level().getServer().isSingleplayer()) {
            return true;
        }
        return isOp(source);
    }

    public static Identifier identifier(ResourceKey<?> key) {
        //? if >=1.21.11 {
        return key.identifier();
        //?} else
        //return key.location();
    }
}
