package de.clickism.clickvillagers.hopper.util;

import de.clickism.clickvillagers.hopper.HopperManager;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.villager.ClaimManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Hopper;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Queue;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public final class HopperUtil {
    private HopperUtil() {}

    public static Collection<LivingEntity> getEligibleVillagers(
            Location hopperLoc,
            ClaimManager claimManager,
            boolean ignoreBabies,
            boolean ignoreClaimed
    ) {
        Queue<LivingEntity> entities = new ArrayDeque<>();
        for (Entity entity : getVillagersAboveHopper(hopperLoc)) {
            EntityType type = entity.getType();
            // Check if entity is a villager
            if (type != EntityType.VILLAGER && type != EntityType.ZOMBIE_VILLAGER) continue;
            if (type == EntityType.ZOMBIE_VILLAGER && !CONFIG.get(ALLOW_ZOMBIE_VILLAGERS)) continue;
            Block block = entity.getLocation().getBlock();
            // Check if entity is a baby villager
            if (ignoreBabies && entity instanceof Villager && !((Ageable) entity).isAdult()) continue;
            // Check if entity is a claimed villager
            if (ignoreClaimed && claimManager.hasOwner((LivingEntity) entity)) continue;
            // Check if entity is in a hopper
            if (block.getType() != Material.HOPPER &&
                block.getRelative(BlockFace.DOWN).getType() != Material.HOPPER) continue;
            entities.add((LivingEntity) entity);
        }
        return entities;
    }

    public static Collection<Entity> getVillagersAboveHopper(Location loc) {
        World world = loc.getWorld();
        if (world == null) return Collections.emptyList();

        return loc.getWorld().getNearbyEntities(loc, 0.5, 1.0, 0.5);
    }

    public static boolean isVillagerHopperItem(ItemStack item) {
        if (item == null || item.getType() != Material.HOPPER) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return hasVillagerHopperData(data);
    }

    public static boolean hasVillagerHopperData(PersistentDataContainer data) {
        return data.has(HopperManager.VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN);
    }

    /**
     * Mark the given hopper as a villager hopper.
     *
     * @param hopper The hopper to mark.
     */
    public static void markHopper(Hopper hopper) {
        PersistentDataContainer data = hopper.getPersistentDataContainer();
        data.set(HopperManager.VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN, true);
        if (CONFIG.get(HOPPER_BLOCK_DISPLAY)) {
            Block block = hopper.getBlock();
            BlockDisplay display = HopperDisplayUtil.createBlockDisplay(block);
            HopperDisplayUtil.addBlockDisplayData(data, display.getUniqueId());
        } else {
            HopperDisplayUtil.addBlockDisplayData(data, null);
        }
        hopper.setCustomName(ChatColor.DARK_GRAY + "📥 " + ChatColor.BOLD + Message.VILLAGER_HOPPER);
        hopper.update();
    }

    public static void sendHopperPlaceMessage(Player player, Block block) {
        Message.VILLAGER_HOPPER_PLACE.sendActionbarSilently(player);
        Location location = block.getLocation();
        World world = player.getWorld();
        world.playSound(location, Sound.BLOCK_METAL_PLACE, 1, .5f);
        world.playSound(location, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, .5f);
    }

    public static void sendHopperBreakMessage(Player player, Block block) {
        Message.VILLAGER_HOPPER_BREAK.sendActionbarSilently(player);
        Location location = block.getLocation();
        World world = player.getWorld();
        world.playSound(location, Sound.BLOCK_METAL_BREAK, 1, .5f);
        world.playSound(location, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, .5f);
    }
}
