package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.Utils;
import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.managers.VillagerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatEvent implements Listener {

    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }
    static ClickVillagers plugin;

    private static final int TIMEOUT = 10;
    private static boolean timer = false;
    private static final Map<Player, Integer> choosing = new ConcurrentHashMap<>();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (choosing.containsKey(e.getPlayer())) {
            e.setCancelled(true);
            if (e.getMessage().contains(" ") || e.getMessage().length() < 3) {
                e.getPlayer().sendMessage(Messages.get("invalid-player"));
            } else {
                makeSelection(e.getPlayer(), e.getMessage());
            }
        }
    }

    public static void startSelecting(Player player, LivingEntity entity) {
        StringBuilder partners = new StringBuilder(ChatColor.WHITE + " ");
        List<String> partnersList = VillagerData.getPartners(VillagerData.getOwner(entity));
        if (!partnersList.isEmpty()) {
            for (int i = 0; i < partnersList.size(); i++) {
                if (i == partnersList.size() - 1) {
                    partners.append(partnersList.get(i));
                } else {
                    partners.append(partnersList.get(i)).append(ChatColor.GOLD).append(" / ").append(ChatColor.WHITE);
                }
            }
            player.sendMessage(String.format(Messages.get("partners"), VillagerData.getOwner(entity)) + partners);
        }
        // Select
        startTimer();
        player.sendMessage(Messages.get("start-select"));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, .3f, 1f);
        choosing.put(player, TIMEOUT);
    }

    public static void cancelSelecting(Player player) {
       if (choosing.remove(player) != null) {
           player.sendMessage(Messages.get("partner-timeout"));
           Utils.playFailSound(player);
       }
    }

    public static void makeSelection(Player player, String selection) {
        choosing.remove(player);
        String owner = VillagerData.getOwner(ClickEvent.getLastClickedVillager(player));
        if (VillagerData.isPartner(selection, owner)) {
            // Remove partner
            VillagerData.removePartner(owner, selection);
            player.sendMessage(Messages.get("remove-partner") + selection);
            Utils.playFailSound(player);
        } else {
            // Add partner
            if (VillagerData.getPartners(owner).size() >= Settings.getInt("partner-limit")) {
                player.sendMessage(String.format(Messages.get("max-partners"), Settings.getInt("partner-limit")));
                Utils.playFailSound(player);
                return;
            }
            VillagerData.addPartner(owner, selection);
            player.sendMessage(Messages.get("add-partner") + selection);
            Utils.playConfirmSound(player);
        }
    }

    private static void startTimer() {
        if (!timer) {
            timer = true;
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                choosing.forEach((p,i) -> {
                    if (i > 0) {
                        choosing.put(p, i - 1);
                    } else {
                        cancelSelecting(p);
                    }
                });
                if (choosing.isEmpty()) {
                    timer = false;
                    task.cancel();
                }
            }, 20L, 20L);
        }
    }
}
