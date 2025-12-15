/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.hopper;

import de.clickism.clickgui.menu.Icon;
import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class HopperManager {

    public static final NamespacedKey VILLAGER_HOPPER_KEY =
            new NamespacedKey(ClickVillagers.INSTANCE, "villager_hopper");
    public static final ItemStack HOPPER_ITEM =
            Icon.of(Material.HOPPER)
                    .setName(ChatColor.GREEN + Message.VILLAGER_HOPPER.toString())
                    .setLore(Message.VILLAGER_HOPPER.getLore())
                    .addEnchantmentGlint()
                    .applyToMeta(meta ->
                            meta.getPersistentDataContainer().set(VILLAGER_HOPPER_KEY, PersistentDataType.BOOLEAN, true)
                    ).get();
    private static final NamespacedKey HOPPER_ITEM_RECIPE_KEY =
            new NamespacedKey(ClickVillagers.INSTANCE, "villager_hopper");

    private final Plugin plugin;

    private final HopperStorage storage = new HopperStorage();
    private final HopperTicker ticker;
    private BukkitTask tickerTask;

    public HopperManager(Plugin plugin, PickupManager pickupManager, ClaimManager claimManager) {
        this.plugin = plugin;
        this.ticker = new HopperTicker(pickupManager, claimManager, storage);
        new BlockListener(plugin, storage);
        new ChunkListener(plugin, storage);
        if (CONFIG.get(TICK_HOPPERS)) {
            restartTasks();
        }
        if (CONFIG.get(HOPPER_RECIPE)) {
            registerHopperRecipe();
        }
    }

    private void startTasks() {
        int tickRate = CONFIG.get(HOPPER_TICK_RATE);
        tickerTask = Bukkit.getScheduler()
                .runTaskTimer(plugin, ticker::tickAll, tickRate, tickRate);
    }

    private void stopTasks() {
        if (tickerTask != null) {
            tickerTask.cancel();
        }
    }

    public void restartTasks() {
        stopTasks();
        if (CONFIG.get(TICK_HOPPERS)) {
            startTasks();
        }
    }

    public int getActiveHopperCount() {
        return this.storage.getTotalCount();
    }

    public void registerHopperRecipe() {
        ShapelessRecipe hopperRecipe = new ShapelessRecipe(HOPPER_ITEM_RECIPE_KEY, HOPPER_ITEM);
        hopperRecipe.addIngredient(Material.HOPPER);
        hopperRecipe.addIngredient(Material.EMERALD);
        try {
            Bukkit.addRecipe(hopperRecipe);
        } catch (Exception ignored) {
            // Recipe already registered
        }
    }

    public void unregisterHopperRecipe() {
        Bukkit.removeRecipe(HOPPER_ITEM_RECIPE_KEY);
    }
}
