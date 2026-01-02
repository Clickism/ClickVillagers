package de.clickism.clickvillagers.hopper.util;

import de.clickism.clickvillagers.ClickVillagers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.UUID;

import static de.clickism.clickvillagers.ClickVillagersConfig.HOPPER_BLOCK_DISPLAY_VIEW_RANGE;

public final class HopperDisplayUtil {
    public static final NamespacedKey DISPLAY_UUID_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "display_uuid");
    private static final Transformation FRAME_TRANSFORMATION = new Transformation(
            new Vector3f(-.525f, -.235f, -.525f),
            new AxisAngle4f(),
            new Vector3f(1.05f, .1f, 1.05f),
            new AxisAngle4f()
    );

    private HopperDisplayUtil() {}

    public static void addBlockDisplayData(PersistentDataContainer data, UUID displayUUID) {
        if (displayUUID != null) {
            data.set(DISPLAY_UUID_KEY, PersistentDataType.STRING, displayUUID.toString());
        } else {
            data.remove(DISPLAY_UUID_KEY);
        }
    }

    public static BlockDisplay createBlockDisplay(Block block) {
        Location loc = block.getLocation().clone().add(0.5, 1, 0.5);
        return block.getWorld().spawn(loc, BlockDisplay.class, display -> {
            display.setTransformation(FRAME_TRANSFORMATION);
            display.setShadowRadius(0f);
            display.setViewRange(HOPPER_BLOCK_DISPLAY_VIEW_RANGE.get());
            display.setBlock(Material.EMERALD_BLOCK.createBlockData());
        });
    }

    public static void removeDisplayIfExists(PersistentDataHolder hopper) {
        String uuidString = hopper.getPersistentDataContainer()
                .get(DISPLAY_UUID_KEY, PersistentDataType.STRING);
        if (uuidString == null) return;
        Entity entity = Bukkit.getEntity(UUID.fromString(uuidString));
        if (entity != null) entity.remove();
    }
}
