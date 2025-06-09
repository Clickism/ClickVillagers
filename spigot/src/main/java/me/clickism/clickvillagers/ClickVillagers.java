/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers;

import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import me.clickism.clickgui.menu.MenuManager;
import me.clickism.clickvillagers.command.ReloadCommand;
import me.clickism.clickvillagers.entity.EntitySaver;
import me.clickism.clickvillagers.entity.EntitySaverFactory;
import me.clickism.clickvillagers.gui.ChatInputListener;
import me.clickism.clickvillagers.hopper.HopperManager;
import me.clickism.clickvillagers.legacy.LegacyHopperCompatibility;
import me.clickism.clickvillagers.legacy.LegacyMessagesCompatibility;
import me.clickism.clickvillagers.listener.CooldownManager;
import me.clickism.clickvillagers.listener.DispenserListener;
import me.clickism.clickvillagers.listener.InteractListener;
import me.clickism.clickvillagers.listener.JoinListener;
import me.clickism.clickvillagers.villager.AnchorManager;
import me.clickism.clickvillagers.villager.ClaimManager;
import me.clickism.clickvillagers.villager.PartnerManager;
import me.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static me.clickism.clickvillagers.message.Message.*;
import static me.clickism.clickvillagers.ClickVillagersConfig.*;

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
            command.setExecutor(new ReloadCommand());
        }
        // Check updates
        if (CONFIG.get(CHECK_UPDATES)) {
            checkUpdates();
            new JoinListener(this, () -> newerVersion);
        }
        // Legacy conversions
        LegacyHopperCompatibility.startConversionIfLegacy(this);
        LegacyMessagesCompatibility.removeLegacyMessageFile(this);
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
}
