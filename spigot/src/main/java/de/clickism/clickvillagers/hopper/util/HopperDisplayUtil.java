package de.clickism.clickvillagers.hopper.util;

import de.clickism.clickvillagers.hopper.config.HopperConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;

public final class HopperDisplayUtil {
    private HopperDisplayUtil() {}

    private static final Transformation FRAME_TRANSFORMATION = new Transformation(
            new Vector3f(-.525f, -.235f, -.525f),
            new AxisAngle4f(),
            new Vector3f(1.05f, .1f, 1.05f),
            new AxisAngle4f()
    );

    public static void applyMark(Hopper hopper, HopperConfig config) {
        if (config.blockDisplay) {
            Block block = hopper.getBlock();
            BlockDisplay display = createBlockDisplay(block, config.displayViewRange);
            mark(hopper, display.getUniqueId());
        } else {
            mark(hopper, null);
        }
    }

    private static void mark(Hopper hopper, UUID displayUUID) {
        PersistentDataContainer data = hopper.getPersistentDataContainer();
        data.set(HopperItemFactory.VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN, true);
        if (displayUUID != null) {
            data.set(HopperItemFactory.DISPLAY_UUID_KEY, PersistentDataType.STRING, displayUUID.toString());
        } else {
            data.remove(HopperItemFactory.DISPLAY_UUID_KEY);
        }
        hopper.setCustomName(ChatColor.DARK_GRAY + "📥 Villager Hopper");
        hopper.update();
    }

    private static BlockDisplay createBlockDisplay(Block block, double range) {
        Location loc = block.getLocation().clone().add(.5, 1, .5);
        return block.getWorld().spawn(loc, BlockDisplay.class, display -> {
            display.setTransformation(FRAME_TRANSFORMATION);
            display.setShadowRadius(0f);
            display.setViewRange((float) range);
            display.setBlock(Material.EMERALD_BLOCK.createBlockData());
        });
    }

    public static void removeDisplayIfExists(PersistentDataContainer data) {
        String uuidString = data.get(HopperItemFactory.DISPLAY_UUID_KEY, PersistentDataType.STRING);
        if (uuidString == null) return;
        Entity entity = Bukkit.getEntity(UUID.fromString(uuidString));
        if (entity != null) entity.remove();
    }
}
