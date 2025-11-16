/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.listener;

import de.clickism.clickgui.menu.MenuManager;
import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.gui.ChatInputListener;
import de.clickism.clickvillagers.gui.VillagerClaimMenu;
import de.clickism.clickvillagers.gui.VillagerEditMenu;
import de.clickism.clickvillagers.legacy.LegacyVillagerCompatibility;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.util.Utils;
import de.clickism.clickvillagers.villager.AnchorManager;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PartnerManager;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class InteractListener implements Listener {

    private final ClaimManager claimManager;
    private final PickupManager pickupManager;
    private final AnchorManager anchorManager;
    private final PartnerManager partnerManager;

    private final ChatInputListener chatInputListener;
    private final MenuManager menuManager;
    private final CooldownManager cooldownManager;

    @AutoRegistered
    public InteractListener(JavaPlugin plugin, ClaimManager claimManager, PickupManager pickupManager,
                            AnchorManager anchorManager, PartnerManager partnerManager,
                            ChatInputListener chatInputListener, MenuManager menuManager,
                            CooldownManager cooldownManager) {
        this.claimManager = claimManager;
        this.pickupManager = pickupManager;
        this.anchorManager = anchorManager;
        this.partnerManager = partnerManager;
        this.chatInputListener = chatInputListener;
        this.menuManager = menuManager;
        this.cooldownManager = cooldownManager;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    private void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager) && !(entity instanceof ZombieVillager)) return;
        LivingEntity villager = (LivingEntity) entity;
        if (villager instanceof ZombieVillager && !CONFIG.get(ALLOW_ZOMBIE_VILLAGERS)) {
            return;
        }
        LegacyVillagerCompatibility.convertDataIfLegacy(villager);
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (!player.isSneaking()) {
            // Handle trade
            handleTrade(player, villager, event);
            return;
        }
        event.setCancelled(true);
        ItemStack item = player.getInventory().getItemInMainHand();
        Material material = item.getType();
        if (material == Material.SHEARS) {
            // Handle anchor
            handleAnchor(player, villager);
            return;
        }
        if (Tag.ITEMS_SHOVELS.isTagged(material)) {
            // Handle claim/edit
            handleClaim(player, villager);
            return;
        }
        // Handle pickup/edit
        if (claimManager.hasOwner(villager)) {
            handleEdit(player, villager);
            return;
        }
        handlePickup(player, villager);
    }

    @EventHandler(ignoreCancelled = true)
    private void onVehicleInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Minecart) && !(entity instanceof Boat)) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        PlayerInventory inventory = player.getInventory();
        ItemStack item = inventory.getItemInMainHand();
        if (!pickupManager.isVillager(item)) return;
        event.setCancelled(true);
        if (!hasSpace(entity)) return;
        try {
            LivingEntity villager = pickupManager.spawnFromItemStack(item, entity.getLocation());
            item.setAmount(item.getAmount() - 1);
            inventory.setItemInMainHand(item);
            World world = player.getWorld();
            if (entity instanceof Minecart) {
                world.playSound(player, Sound.BLOCK_METAL_BREAK, 1, .5f);
            } else {
                world.playSound(player, Sound.BLOCK_WOOD_BREAK, 1, .5f);
            }
            entity.addPassenger(villager);
        } catch (IllegalArgumentException exception) {
            Message.READ_ERROR.send(player);
            ClickVillagers.LOGGER.severe("Failed to read villager data: " + exception.getMessage());
        }
    }

    private boolean hasSpace(Entity entity) {
        List<Entity> passengers = entity.getPassengers();
        if (entity instanceof RideableMinecart || entity instanceof ChestBoat) {
            return passengers.isEmpty();
        }
        if (entity instanceof Boat) {
            return passengers.size() < 2;
        }
        return false;
    }

    private void handleTrade(Player player, LivingEntity villager, Cancellable event) {
        if (!claimManager.hasOwner(villager)) return;
        if (claimManager.isTradeOpen(villager)) return;
        if (Permission.BYPASS_CLAIMS.has(player)) return;
        if (claimManager.isOwner(villager, player)) return;
        if (partnerManager.isPartner(claimManager.getOwnerUUID(villager), player.getName())) return;
        event.setCancelled(true);
        Message.BELONGS_TO.send(player, claimManager.getOwnerName(villager));
    }

    private void handleAnchor(Player player, LivingEntity villager) {
        if (Permission.ANCHOR.lacksAndNotify(player)) return;
        if (claimManager.hasOwner(villager) && !claimManager.isOwner(villager, player)
                && Permission.BYPASS_CLAIMS.lacks(player)) {
            Message.BELONGS_TO.send(player, claimManager.getOwnerName(villager));
            return;
        }
        Location location = villager.getLocation();
        World world = player.getWorld();
        if (anchorManager.isAnchored(villager)) {
            // Remove anchor
            anchorManager.removeAnchorEffect(villager);
            Message.ANCHOR_REMOVE.sendActionbarSilently(player);
            world.playSound(location, Sound.BLOCK_CHAIN_PLACE, 1, 1f);
            world.spawnParticle(Particle.WAX_OFF, location, 10, .2, 0, .2, 2);
        } else {
            // Add anchor
            anchorManager.addAnchorEffect(villager);
            Message.ANCHOR_ADD.sendActionbarSilently(player);
            world.playSound(player, Sound.BLOCK_CHAIN_PLACE, 1, .5f);
            world.spawnParticle(Particle.WAX_ON, location, 10, .2, 0, .2, 2);
            Block blockBelow = location.getBlock().getRelative(BlockFace.DOWN);
            world.spawnParticle(Particle.BLOCK, location, 30, blockBelow.getBlockData());
        }
    }

    private void handleClaim(Player player, LivingEntity villager) {
        if (claimManager.hasOwner(villager)) {
            handleEdit(player, villager);
            return;
        }
        if (Permission.CLAIM.lacksAndNotify(player)) return;
        if (cooldownManager.hasCooldown(player) && Permission.BYPASS_CLAIMS.lacks(player)) {
            Message.CLAIM_COOLDOWN.sendActionbar(player, cooldownManager.getRemainingCooldownSeconds(player));
            return;
        }
        new VillagerClaimMenu(player, villager, claimManager, pickupManager, partnerManager, chatInputListener,
                cooldownManager).open(menuManager);
        playOpenSound(player, villager);
    }

    private void handleEdit(Player player, LivingEntity villager) {
        if (claimManager.isOwner(villager, player) || Permission.BYPASS_CLAIMS.has(player)) {
            new VillagerEditMenu(player, villager, claimManager, pickupManager, partnerManager, chatInputListener)
                    .open(menuManager);
            playOpenSound(player, villager);
            return;
        }
        Message.BELONGS_TO.send(player, claimManager.getOwnerName(villager));
    }

    private void handlePickup(Player player, LivingEntity villager) {
        if (Permission.PICKUP.lacksAndNotify(player)) return;
        if (cooldownManager.hasCooldown(player) && Permission.BYPASS_CLAIMS.lacks(player)) {
            Message.PICK_UP_COOLDOWN.sendActionbar(player, cooldownManager.getRemainingCooldownSeconds(player));
            return;
        }
        ItemStack item;
        try {
            item = pickupManager.toItemStack(villager);
        } catch (IllegalArgumentException exception) {
            Message.WRITE_ERROR.send(player);
            ClickVillagers.LOGGER.severe("Failed to write villager data: " + exception.getMessage());
            return;
        }
        Utils.setHandOrGive(player, item);
        Message.PICK_UP_VILLAGER.sendActionbarSilently(player);
        pickupManager.sendPickupEffect(villager);
        cooldownManager.giveCooldown(player);
    }

    public static void playOpenSound(Player player, LivingEntity villager) {
        player.playSound(villager, Sound.BLOCK_CHEST_OPEN, 1, .8f);
    }
}
