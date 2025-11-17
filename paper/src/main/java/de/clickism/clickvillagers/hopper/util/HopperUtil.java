package de.clickism.clickvillagers.hopper.util;

import de.clickism.clickvillagers.hopper.config.HopperConfig;
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

import java.util.*;

public final class HopperUtil {
    private HopperUtil() {}

    public static List<LivingEntity> getEligibleVillagers(Location hopperLoc, HopperConfig config, ClaimManager claimManager) {
        List<LivingEntity> list = new ArrayList<>();
        for (Entity e : getVillagersAboveHopper(hopperLoc)) {
            if (!(e instanceof LivingEntity living)) continue;
            EntityType type = living.getType();
            if (type != EntityType.VILLAGER && type != EntityType.ZOMBIE_VILLAGER) continue;
            if (type == EntityType.ZOMBIE_VILLAGER && !config.allowZombieVillagers) continue;
            if (config.ignoreBabies && living instanceof Villager v && !v.isAdult()) continue;
            if (config.ignoreClaimed && claimManager.hasOwner(living)) continue;

            Block block = living.getLocation().getBlock();
            if (block.getType() == Material.HOPPER || block.getRelative(BlockFace.DOWN).getType() == Material.HOPPER)
                list.add(living);
        }
        return list;
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
        return isVillagerHopper(data);
    }

    public static boolean isVillagerHopper(PersistentDataContainer data) {
        return data.has(HopperItemFactory.VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN);
    }

    public static void markHopper(Hopper hopper, HopperConfig config) {
        if (config.blockDisplay) {
            Block block = hopper.getBlock();
            BlockDisplay display = HopperDisplayUtil.createBlockDisplay(block, config.displayViewRange);
            HopperDisplayUtil.addBlockDisplay(hopper, display.getUniqueId());
        } else {
            HopperDisplayUtil.addBlockDisplay(hopper, null);
        }
    }

    public static void playPlaceSound(Block block, Player player) {
        World w = block.getWorld();
        Location l = block.getLocation();
        w.playSound(l, Sound.BLOCK_METAL_PLACE, 1, .5f);
        w.playSound(l, Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1, .5f);
        Message.VILLAGER_HOPPER_PLACE.sendActionbarSilently(player);
    }

    public static void playBreakSound(Block block, Player player) {
        World w = block.getWorld();
        Location l = block.getLocation();
        w.playSound(l, Sound.BLOCK_METAL_BREAK, 1, .5f);
        w.playSound(l, Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1, .5f);
        Message.VILLAGER_HOPPER_BREAK.sendActionbarSilently(player);
    }
}
