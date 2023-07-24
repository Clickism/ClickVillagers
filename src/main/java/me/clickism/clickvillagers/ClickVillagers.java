package me.clickism.clickvillagers;

import me.clickism.clickvillagers.events.BlockEvent;
import me.clickism.clickvillagers.events.InteractEvent;
import org.bukkit.*;
import org.bukkit.block.Hopper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public final class ClickVillagers extends JavaPlugin {

    private static DataManager data;
    private static Plugin plugin;

    public static ItemStack getVillagerHeadItem(Villager villager){
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        try {
            if (villager.getProfession() == Villager.Profession.FISHERMAN) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/61d644761f706d31c99a593c8d5f7cbbd4372d73fbee8464f482fa6c139d97d4"));
            } else if (villager.getProfession() == Villager.Profession.ARMORER) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/f522db92f188ebc7713cf35b4cbaed1cfe2642a5986c3bde993f5cfb3727664c"));
            } else if (villager.getProfession() == Villager.Profession.BUTCHER) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/c6774d2df515eceae9eed291c1b40f94adf71df0ab81c7191402e1a45b3a2087"));
            } else if (villager.getProfession() == Villager.Profession.CLERIC) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/a8856eaafad96d76fa3b5edd0e3b5f45ee49a3067306ad94df9ab3bd5b2d142d"));
            } else if (villager.getProfession() == Villager.Profession.CARTOGRAPHER) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/94248dd0680305ad73b214e8c6b00094e27a4ddd8034676921f905130b858bdb"));
            } else if (villager.getProfession() == Villager.Profession.FARMER) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/d01e035a3d8d6126072bcbe52a97913ace93552a99995b5d4070d6783a31e909"));
            } else if (villager.getProfession() == Villager.Profession.FLETCHER) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/d831830a7bd3b1ab05beb98dc2f9fc5ea550b3cf649fd94d483da7cd39f7c063"));
            } else if (villager.getProfession() == Villager.Profession.LEATHERWORKER) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/f76cf8b7378e889395d538e6354a17a3de6b294bb6bf8db9c701951c68d3c0e6"));
            } else if (villager.getProfession() == Villager.Profession.MASON) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/2c02c3ffd5705ab488b305d57ff0168e26de70fd3f739e839661ab947dff37b1"));
            } else if (villager.getProfession() == Villager.Profession.SHEPHERD) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/19e04a752596f939f581930414561b175454d45a0506501e7d2488295a5d5de"));
            } else if (villager.getProfession() == Villager.Profession.TOOLSMITH) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/7dfa07fd1244eb8945f4ededd00426750b77ef5dfbaf03ed775633459ece415a"));
            } else if (villager.getProfession() == Villager.Profession.WEAPONSMITH) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/5e409b958bc4fe045e95d325e6e97a533137e33fec7042ac027b30bb693a9d42"));
            } else if (villager.getProfession() == Villager.Profession.LIBRARIAN) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/e66a53fc707ce1ff88a576ef40200ce8d49fae4acad1e3b3789c7d1cc1cc541a"));
            } else if (villager.getProfession() == Villager.Profession.NITWIT) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/35e799dbfaf98287dfbafce970612c8f075168977aacc30989d34a4a5fcdf429"));
            } else {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/d14bff1a38c9154e5ec84ce5cf00c58768e068eb42b2d89a6bbd29787590106b"));
            }
            if (!villager.isAdult()) {
                profile.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/3d7788826b9ac4deaf383b387947085211447ed50fdc21bf71c230048dd5986f"));
            }
        } catch (MalformedURLException ignored) { }
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwnerProfile(profile);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getVillagerHopper() {
        return villagerHopper;
    }

    private static ItemStack villagerHopper;

    @Override
    public void onEnable() {
        data = new DataManager(this);
        plugin = this;

        Utils.setPlugin(this);
        BlockEvent.setPlugin(this);

        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockEvent(), this);

        ItemStack villagerHopperItem = new ItemStack(Material.HOPPER);
        ItemMeta villagerHopperMeta = villagerHopperItem.getItemMeta();
        villagerHopperMeta.setDisplayName(ChatColor.GREEN + "Villager Hopper");
        villagerHopperMeta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + "Use this to automatically pick up villagers."));
        villagerHopperMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        villagerHopperMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        villagerHopperItem.setItemMeta(villagerHopperMeta);
        villagerHopper = villagerHopperItem;

        ShapelessRecipe hopperRecipe = new ShapelessRecipe(new NamespacedKey(this, "villager_hopper"), villagerHopperItem);
        hopperRecipe.addIngredient(Material.HOPPER);
        hopperRecipe.addIngredient(Material.EMERALD);
        Bukkit.addRecipe(hopperRecipe);

        checkHoppers();
        Bukkit.getLogger().info("ClickVillagers activated.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("ClickVillagers deactivated.");
    }

    public static DataManager getData() {
        return data;
    }

    private static HashMap<UUID, Villager> tempVillagerMap = new HashMap<>();

    public static ItemStack getVillagerHead(Villager villager) {
        ItemStack item = getVillagerHeadItem(villager);
        ItemMeta meta = item.getItemMeta();
        if (Utils.capitalize(villager.getProfession().toString()).equals("None")) {
            meta.setDisplayName(ChatColor.YELLOW + "Villager");
        } else {
            meta.setDisplayName(ChatColor.YELLOW + Utils.capitalize(villager.getProfession().toString()) + " Villager");
        }
        if (!villager.isAdult()) {
            meta.setDisplayName(ChatColor.YELLOW + "Baby Villager");
        }
        meta.setLore(Collections.singletonList(ChatColor.DARK_GRAY + "Right click to place the villager back."));
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING, villager.getUniqueId().toString());
        item.setItemMeta(meta);
        villager.setInvisible(true);
        villager.setInvulnerable(true);
        villager.setGravity(false);
        villager.setAI(false);
        villager.teleport(new Location(villager.getWorld(), 1, -70, 1));
        tempVillagerMap.put(villager.getUniqueId(), villager);
        return item;
    }

    @Nullable
    public static Villager getVillagerFromHead(ItemStack head) {
        ItemMeta meta = head.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING)) {
            UUID uuid = UUID.fromString(dataContainer.get(new NamespacedKey(plugin, "villager_uuid"), PersistentDataType.STRING));
            if (Bukkit.getEntity(uuid) != null) {
                return (Villager) Bukkit.getEntity(uuid);
            } else if (tempVillagerMap.containsKey(uuid)) {
                Villager villager = tempVillagerMap.get(uuid);
                tempVillagerMap.remove(uuid);
                return villager;
            }
            return (Villager) Bukkit.getWorlds().get(0).spawnEntity(new Location(Bukkit.getWorlds().get(0), 1, -70, 1), EntityType.VILLAGER);
        }
        return null;
    }

    private static void checkHoppers() {
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            getHoppersList().forEach(hopper -> {
                hopper.getWorld().getNearbyEntities(hopper.clone().add(.5,1,.5), 1,1,1).forEach(entity -> {
                    if (entity instanceof Villager) {
                        if (((Villager) entity).isAdult()) {
                            if (isHopperEmpty(((Hopper) hopper.getBlock().getState()).getInventory())) {
                                ((Hopper) hopper.getBlock().getState()).getInventory().addItem(getVillagerHead((Villager) entity));
                            }
                        }
                    }
                });
            });
        }, 20L, 20L);
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
        data.getConfig().set("villager_hoppers." + location + ".display", frame.getUniqueId().toString());
        data.getConfig().set("hoppers_list", hoppersList);
        data.saveConfig();
    }

    public static void removeVillagerHopper(Location location) {
        List<Location> hoppersList = getHoppersList();
        hoppersList.remove(location);
        data.getConfig().set("hoppers_list", hoppersList);
        if (data.getConfig().get("villager_hoppers." + location + ".display") != null) {
            Bukkit.getEntity(UUID.fromString((String) data.getConfig().get("villager_hoppers." + location + ".display"))).remove();
            data.getConfig().set("villager_hoppers." + location + ".display", null);
            data.saveConfig();
        }
    }

    public static boolean isVillagerHopper(Location location) {
        return getHoppersList().contains(location);
    }

    public static List<Location> getHoppersList() {
        if (data.getConfig().get("hoppers_list") != null) {
            return (List<Location>) data.getConfig().get("hoppers_list");
        }
        return new ArrayList<>();
    }
}
