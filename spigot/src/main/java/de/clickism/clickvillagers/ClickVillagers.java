/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import de.clickism.clickvillagers.command.ReloadCommand;
import de.clickism.clickvillagers.entity.EntitySaver;
import de.clickism.clickvillagers.entity.EntitySaverFactory;
import de.clickism.clickvillagers.gui.ChatInputListener;
import de.clickism.clickvillagers.hopper.HopperManager;
import de.clickism.clickvillagers.legacy.LegacyHopperCompatibility;
import de.clickism.clickvillagers.legacy.LegacyMessagesCompatibility;
import de.clickism.clickvillagers.listener.CooldownManager;
import de.clickism.clickvillagers.listener.DispenserListener;
import de.clickism.clickvillagers.listener.InteractListener;
import de.clickism.clickvillagers.listener.JoinListener;
import de.clickism.clickvillagers.villager.AnchorManager;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PartnerManager;
import de.clickism.clickvillagers.villager.PickupManager;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import me.clickism.clickgui.menu.MenuManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;
import static de.clickism.clickvillagers.message.Message.UPDATE;

public final class ClickVillagers extends JavaPlugin {

    public static final String PROJECT_ID = "clickvillagers";

    public static ClickVillagers INSTANCE;
    public static Logger LOGGER;

    private @Nullable String newerVersion;

    @Override
    public void onLoad() {
        INSTANCE = this;
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {
        CONFIG.load(); // Will also load messages
        // Load data
        PartnerManager partnerManager;
        try {
            partnerManager = new PartnerManager(this);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load partners: ", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Instantiate managers/listeners
        MenuManager menuManager = new MenuManager(this);
        ClaimManager claimManager = new ClaimManager(this);
        AnchorManager anchorHandler = new AnchorManager(this);
        EntitySaver entitySaver = EntitySaverFactory.create();
        PickupManager pickupManager = new PickupManager(this, entitySaver, claimManager, anchorHandler);
        HopperManager hopperManager = new HopperManager(this, pickupManager, claimManager);
        ChatInputListener chatInputListener = new ChatInputListener(this);

        CooldownManager cooldownManager = new CooldownManager(() -> CONFIG.get(COOLDOWN) * 1000L);

        new InteractListener(this, claimManager, pickupManager,
                anchorHandler, partnerManager, chatInputListener, menuManager, cooldownManager);
        new DispenserListener(this, pickupManager);
        // Register commands
        PluginCommand command = Bukkit.getPluginCommand("clickvillagers");
        if (command != null) {
            command.setExecutor(new ReloadCommand(hopperManager.getHopperConfig()));
        }
        // Check updates
        if (CONFIG.get(CHECK_UPDATES)) {
            checkUpdates();
            new JoinListener(this, () -> newerVersion);
        }
        // Legacy conversions
        var hopperCompatibility = new LegacyHopperCompatibility(hopperManager); // TODO: Check if the order is correct here
        hopperCompatibility.startConversionIfLegacy(this);
        LegacyMessagesCompatibility.removeLegacyMessageFile(this);
        // Metrics
        setupBStats(hopperManager);
    }

    private void checkUpdates() {
        LOGGER.info("Checking for updates...");
        new ModrinthUpdateChecker(PROJECT_ID, "spigot", null).checkVersion(version -> {
            if (getDescription().getVersion().equals(version)) return;
            newerVersion = version;
            LOGGER.info("New version available: " + version);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.isOp()) return;
                UPDATE.send(player, version);
            });
        });
    }

    private void setupBStats(HopperManager hopperManager) {
        int pluginId = 27919;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SingleLineChart("active_villager_hoppers",
                hopperManager::getActiveHopperCount));
        metrics.addCustomChart(new SimplePie("hopper_tick_rate", () ->
                String.valueOf(CONFIG.get(HOPPER_TICK_RATE))));
        metrics.addCustomChart(new SimplePie("language", () ->
                String.valueOf(CONFIG.get(LANGUAGE))));
    }
}
