package me.clickism.clickvillagers;

import me.clickism.clickvillagers.config.Messages;
import me.clickism.clickvillagers.config.Settings;
import me.clickism.clickvillagers.events.BlockEvent;
import me.clickism.clickvillagers.events.InteractEvent;
import me.clickism.clickvillagers.events.UpdateChecker;
import me.clickism.clickvillagers.managers.HopperManager;
import me.clickism.clickvillagers.managers.VillagerManager;
import me.clickism.clickvillagers.managers.data.ConfigManager;
import me.clickism.clickvillagers.managers.data.DataManager;
import me.clickism.clickvillagers.managers.data.MessageManager;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClickVillagers extends JavaPlugin {

    private static DataManager data;
    private static ConfigManager config;
    private static MessageManager messages;
    private static String version;

    @Override
    public void onEnable() {
        data = new DataManager(this);
        config = new ConfigManager(this);
        messages = new MessageManager(this);

        version = getDescription().getVersion();

        Utils.setPlugin(this);
        BlockEvent.setPlugin(this);
        VillagerManager.setPlugin(this);
        HopperManager.setPlugin(this);

        Settings.initializeConfig();
        Messages.initializeConfig();
        HopperManager.initializeItems();
        HopperManager.initializeHoppersMap();

        UpdateChecker updateChecker = new UpdateChecker();
        updateChecker.check();
        if (updateChecker.isAvailable()) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.isOp()) {
                    p.sendMessage(Messages.get("update"));
                }
            });
        }

        Bukkit.getPluginManager().registerEvents(updateChecker, this);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockEvent(), this);

        new Location(Bukkit.getWorlds().get(0), 0, -70, 0).getChunk().setForceLoaded(true);

        HopperManager.checkHoppers();

        Bukkit.getLogger().info("ClickVillagers activated.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("ClickVillagers deactivated.");
    }

    public static DataManager getData() {
        return data;
    }
    public static ConfigManager getConfigManager() {
        return config;
    }
    public static MessageManager getMessageManager() {
        return messages;
    }
    public static String getVersion() {
        return version;
    }
}
