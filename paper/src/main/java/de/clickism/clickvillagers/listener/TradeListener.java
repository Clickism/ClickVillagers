/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.listener;

import de.clickism.clickgui.menu.Icon;
import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.message.Message;
import io.papermc.paper.event.player.PlayerTradeEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static de.clickism.clickvillagers.ClickVillagersConfig.ALLOW_RESETTING_TRADES;

public class TradeListener implements Listener {
    public static final NamespacedKey BUTTON_RESET_TRADES_KEY = new NamespacedKey(ClickVillagers.INSTANCE, "button_reset_trades");
    private static final Icon RESET_TRADES_ICON = Message.BUTTON_RESET_TRADES.toIcon(Material.BARRIER)
            .applyToMeta(meta -> {
                var pdc = meta.getPersistentDataContainer();
                pdc.set(BUTTON_RESET_TRADES_KEY, PersistentDataType.BOOLEAN, true);
            })
            .addEnchantmentGlint();

    public TradeListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void onTradeInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getView().getTopInventory() instanceof MerchantInventory inventory)) return;
        var merchant = inventory.getMerchant();
        if (!(merchant instanceof Villager villager)) return;
        List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
        recipes.removeIf(this::isResetRecipe); // Remove existing reset recipes if any
        merchant.setRecipes(recipes); // Set recipes without reset recipe
        if (!ALLOW_RESETTING_TRADES.get()) return;
        if (areTradesLocked(villager)) return; // Locked trades
        recipes.add(getResetRecipe());
        merchant.setRecipes(recipes);
    }

    @EventHandler
    private void onTradeInventoryClose(InventoryCloseEvent event) {
        // Remove reset recipe button on close
        if (!(event.getView().getTopInventory() instanceof MerchantInventory inventory)) return;
        var merchant = inventory.getMerchant();
        List<MerchantRecipe> recipes = new ArrayList<>(merchant.getRecipes());
        recipes.removeIf(this::isResetRecipe);
        merchant.setRecipes(recipes);
    }

    @EventHandler
    private void onTradeSelect(TradeSelectEvent event) {
        if (!ALLOW_RESETTING_TRADES.get()) return;
        Merchant merchant = event.getInventory().getMerchant();
        MerchantRecipe selectedRecipe = merchant.getRecipes().get(event.getIndex());
        if (!isResetRecipe(selectedRecipe)) return;
        event.setCancelled(true);
        if (!(merchant instanceof Villager villager)) return;
        // Reset trades
        var player = (Player) event.getWhoClicked();
        if (areTradesLocked(villager)) {
            // Trades are locked, close inventory
            player.closeInventory();
        }
        villager.resetOffers();
        Message.TRADES_RESET.sendActionbarSilently(player);
        player.playSound(player, Sound.BLOCK_SMITHING_TABLE_USE, 1f, .5f);
        player.closeInventory();
    }

    @EventHandler
    private void onTrade(PlayerTradeEvent event) {
        if (!ALLOW_RESETTING_TRADES.get()) return;
        var recipe = event.getTrade();
        // Prevent taking the reset recipe
        if (isResetRecipe(recipe)) {
            event.setCancelled(true);
        }
    }

    private MerchantRecipe getResetRecipe() {
        MerchantRecipe recipe = new MerchantRecipe(RESET_TRADES_ICON.get(), 1);
        recipe.addIngredient(RESET_TRADES_ICON.get());
        recipe.addIngredient(RESET_TRADES_ICON.get());
        return recipe;
    }

    private boolean isResetRecipe(MerchantRecipe recipe) {
        ItemStack result = recipe.getResult();
        return result.getItemMeta().getPersistentDataContainer()
                .has(BUTTON_RESET_TRADES_KEY);
    }

    private boolean areTradesLocked(Villager villager) {
        return villager.getVillagerExperience() > 0;
    }
}
