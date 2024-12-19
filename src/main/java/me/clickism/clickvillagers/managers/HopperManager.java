package me.clickism.clickvillagers.managers;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.data.DataManager;
import org.bukkit.*;
import org.bukkit.block.Hopper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.*;

public class HopperManager {

    private static DataManager data;

    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }

    static ClickVillagers plugin;

    public static ItemStack getVillagerHopper() {
        return villagerHopper;
    }

    private static ItemStack villagerHopper;

    public static void initializeItems() {
        data = ClickVillagers.getData();

        ItemStack villagerHopperItem = new ItemStack(Material.HOPPER);
        ItemMeta villagerHopperMeta = villagerHopperItem.getItemMeta();
        villagerHopperMeta.setDisplayName(ChatColor.GREEN + Messages.get("villager-hopper"));
        villagerHopperMeta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + Messages.get("villager-hopper-description")));
        villagerHopperMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        villagerHopperMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        villagerHopperItem.setItemMeta(villagerHopperMeta);
        villagerHopper = villagerHopperItem;

        if (!Settings.get("enable-villager-hoppers")) return;

        ShapelessRecipe hopperRecipe = new ShapelessRecipe(new NamespacedKey(plugin, "villager_hopper"), villagerHopperItem);
        hopperRecipe.addIngredient(Material.HOPPER);
        hopperRecipe.addIngredient(Material.EMERALD);
        Bukkit.addRecipe(hopperRecipe);
    }

    public static void initializeHoppersMap() {
        getHoppersList().forEach(loc -> {
            hoppersMap.put(loc, true);
        });
    }

    public static void checkHoppers() {
        if (!Settings.get("enable-villager-hoppers")) {
            return;
        }
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            List<Location> toBeRemoved = new ArrayList<>();
            getHoppersList().forEach(hopper -> {
                try {
                    if (hopper.getBlock().getType() != Material.HOPPER) toBeRemoved.add(hopper);
                    hopper.getWorld().getNearbyEntities(hopper.clone().add(.5, 1, .5), 1, 1, 1).forEach(entity -> {
                        if (entity instanceof Villager || entity instanceof ZombieVillager) {
                            if (((Ageable) entity).isAdult() || entity instanceof ZombieVillager) {
                                if (!VillagerData.isClaimed((LivingEntity) entity)) {
                                    if (isHopperEmpty(((Hopper) hopper.getBlock().getState()).getInventory())) {
                                        ((Hopper) hopper.getBlock().getState()).getInventory().addItem(VillagerManager.turnVillagerIntoHead((LivingEntity) entity));
                                    }
                                }
                            }
                        }
                    });
                } catch (NullPointerException ignored) { }
            });
            toBeRemoved.forEach(HopperManager::removeVillagerHopper);
        }, Settings.getInt("hopper-check-interval"), Settings.getInt("hopper-check-interval"));
    }

    private static boolean isHopperEmpty(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                return true;
            } else if (item.getType() == Material.AIR) {
                return true;
            }
        }
        return false;
    }
    public static void addVillagerHopper(Location location) {
        BlockDisplay frame = (BlockDisplay) location.getWorld().spawnEntity(location.clone().add(.5,1,.5), EntityType.BLOCK_DISPLAY);
        frame.setTransformation(new Transformation(new Vector3f(-.525f,-.235f,-.525f), new AxisAngle4f(), new Vector3f(1.05f,.1f,1.05f), new AxisAngle4f()));
        frame.setViewRange(.2f);
        frame.setShadowRadius(0f);
        frame.setBlock(Material.EMERALD_BLOCK.createBlockData());
        List<Location> hoppersList = getHoppersList();
        hoppersList.add(location);
        hoppersMap.put(location, true);

        data.getConfig().set("villager_hoppers." + location + ".display", frame.getUniqueId().toString());
        data.getConfig().set("hoppers_list", hoppersList);
        data.saveConfig();
    }

    public static void removeVillagerHopper(Location location) {
        List<Location> hoppersList = getHoppersList();
        hoppersList.remove(location);
        hoppersMap.remove(location);

        data.getConfig().set("hoppers_list", hoppersList);
        if (data.getConfig().get("villager_hoppers." + location + ".display") != null) {
            if (Bukkit.getEntity(UUID.fromString((String) data.getConfig().get("villager_hoppers." + location + ".display"))) != null) {
                Bukkit.getEntity(UUID.fromString((String) data.getConfig().get("villager_hoppers." + location + ".display"))).remove();
            }
            data.getConfig().set("villager_hoppers." + location + ".display", null);
            data.saveConfig();
        }
    }

    private static final HashMap<Location, Boolean> hoppersMap = new HashMap<>();

    public static boolean isVillagerHopper(Location location) {
        return hoppersMap.containsKey(location);
    }

    public static List<Location> getHoppersList() {
        if (data.getConfig().get("hoppers_list") != null) {
            return (List<Location>) data.getConfig().get("hoppers_list");
        }
        return new ArrayList<>();
    }
}
