package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.VillagerData;
import me.clickism.clickvillagers.managers.VillagerManager;
import me.clickism.clickvillagers.menu.Buttons;
import me.clickism.clickvillagers.menu.ChangeBiomeMenu;
import me.clickism.clickvillagers.menu.ClaimVillagerMenu;
import me.clickism.clickvillagers.menu.EditVillagerMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ClickEvent implements Listener {

    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }
    static ClickVillagers plugin;

    private static final Map<Player, LivingEntity> lastClickedVillager = new HashMap<>();
    public static void setLastClickedVillager(Player player, LivingEntity villager) {
        lastClickedVillager.put(player, villager);
        ChatEvent.cancelSelecting(player);
    }

    public static LivingEntity getLastClickedVillager(Player player) {
        return lastClickedVillager.get(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getView().getPlayer();
        if (e.getClickedInventory() == null || e.getClickedInventory().equals(p.getInventory())) return;
        if (e.getCurrentItem() == null) return;
        if (!e.getCurrentItem().hasItemMeta()) return;

        if (e.getView().getTitle().equals(ClaimVillagerMenu.getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem().equals(Buttons.CLAIM.item())) {
                //Claim
                VillagerData.setOwner(getLastClickedVillager(p), p);
                p.openInventory(EditVillagerMenu.get(getLastClickedVillager(p)));
                p.sendMessage(Messages.get("confirm-claim"));
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, .3f, 1f);
            }
        } else if (e.getView().getTitle().equals(EditVillagerMenu.getTitle(getLastClickedVillager(p)))) {
            e.setCancelled(true);
            if (e.getCurrentItem().equals(Buttons.UNCLAIM.item())) {
                //Unclaim
                VillagerData.removeOwner(getLastClickedVillager(p));
                p.closeInventory();
                p.sendMessage(Messages.get("confirm-unclaim"));
                Utils.playFailSound(p);
            } else if (e.getCurrentItem().equals(Buttons.TRADABLE.item())) {
                //Make not tradable
                VillagerData.setTradable(getLastClickedVillager(p), false);
                p.openInventory(EditVillagerMenu.get(getLastClickedVillager(p)));
                Utils.playConfirmSound(p);
            } else if (e.getCurrentItem().equals(Buttons.NOT_TRADABLE.item())) {
                //Make tradable
                VillagerData.setTradable(getLastClickedVillager(p), true);
                p.openInventory(EditVillagerMenu.get(getLastClickedVillager(p)));
                Utils.playConfirmSound(p);
            } else if (e.getCurrentItem().equals(Buttons.BIOME.item())) {
                //Open biome inventory
                p.openInventory(ChangeBiomeMenu.get());
                Utils.playConfirmSound(p);
            } else if (e.getCurrentItem().getItemMeta().getDisplayName().equals(Buttons.PICK.item().getItemMeta().getDisplayName())) {
                //Pick villager up
                p.closeInventory();
                if (!p.hasPermission("clickvillagers.pickup")) {
                    p.sendMessage(Messages.get("no-permission"));
                    Utils.playFailSound(p);
                    return;
                }
                ItemStack head = VillagerManager.turnVillagerIntoHead(getLastClickedVillager(p));
                if (p.getInventory().getItemInMainHand().getType() == Material.AIR) {
                    p.getInventory().setItem(p.getInventory().getHeldItemSlot(), head);
                } else {
                    p.getInventory().addItem(head).forEach((i, item) -> {
                        p.getWorld().dropItem(p.getLocation(), item);
                    });
                }
                p.sendMessage(Messages.get("picked-villager"));
                Utils.playConfirmSound(p);
            } else if (e.getCurrentItem().equals(Buttons.ADD_TRADE_PARTNER.item())) {
                // Add/remove trade partner
                p.closeInventory();
                if (!Settings.get("enable-partners")) {
                    p.sendMessage(Messages.get("partner-disabled"));
                    Utils.playFailSound(p);
                    return;
                }
                if (!p.hasPermission("clickvillagers.partner")) {
                    p.sendMessage(Messages.get("no-permission"));
                    Utils.playFailSound(p);
                    return;
                }
                ChatEvent.startSelecting(p, getLastClickedVillager(p));
            }
        } else if (e.getView().getTitle().equals(ChangeBiomeMenu.getTitle())) {
            e.setCancelled(true);
            Villager.Type type = ChangeBiomeMenu.getType(e.getCurrentItem());
            if (type != null) {
                //Change biome
                if (getLastClickedVillager(p) instanceof Villager) {
                    ((Villager) getLastClickedVillager(p)).setVillagerType(type);
                } else if (getLastClickedVillager(p) instanceof ZombieVillager) {
                    ((ZombieVillager) getLastClickedVillager(p)).setVillagerType(type);
                }
                Utils.playConfirmSound(p);
                p.sendMessage(Messages.get("biome-change") + Utils.capitalize(type.name()));
            }
        }
    }
}
