/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers;

import me.clickism.clickgui.menu.MenuManager;
import me.clickism.clickvillagers.config.ReloadCommand;
import me.clickism.clickvillagers.config.Setting;
import me.clickism.clickvillagers.gui.ChatInputListener;
import me.clickism.clickvillagers.hopper.HopperManager;
import me.clickism.clickvillagers.legacy.LegacyHopperCompatibility;
import me.clickism.clickvillagers.legacy.LegacyMessagesCompatibility;
import me.clickism.clickvillagers.listener.DispenserListener;
import me.clickism.clickvillagers.listener.InteractListener;
import me.clickism.clickvillagers.message.Message;
import me.clickism.clickvillagers.nbt.NBTHelper;
import me.clickism.clickvillagers.nbt.NBTHelperFactory;
import me.clickism.clickvillagers.util.MessageParameterizer;
import me.clickism.clickvillagers.util.UpdateChecker;
import me.clickism.clickvillagers.villager.AnchorManager;
import me.clickism.clickvillagers.villager.ClaimManager;
import me.clickism.clickvillagers.villager.PartnerManager;
import me.clickism.clickvillagers.villager.PickupManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClickVillagers extends JavaPlugin {

    public static final String RESOURCE_ID = "111424";

    public static ClickVillagers INSTANCE;
    public static Logger LOGGER;

    @Override
    public void onLoad() {
        INSTANCE = this;
        LOGGER = getLogger();
    }

    @Override
    public void onEnable() {
        // Load config/messages
        try {
            Setting.initialize(this);
            Message.initialize(this);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load config/messages: ", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Load data
        PartnerManager partnerManager;
        try {
            partnerManager = new PartnerManager(this);
        } catch (IOException exception) {
            LOGGER.log(Level.SEVERE, "Failed to load partners: ", exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Initialize NBT helper
        NBTHelper nbtHelper;
        try {
            nbtHelper = NBTHelperFactory.create();
        } catch (UnsupportedOperationException exception) {
            LOGGER.severe("This server version is not supported.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Instantiate managers/listeners
        MenuManager menuManager = new MenuManager(this);
        ClaimManager claimManager = new ClaimManager(this);
        AnchorManager anchorHandler = new AnchorManager(this);
        PickupManager pickupManager = new PickupManager(this, nbtHelper, claimManager, anchorHandler);
        HopperManager hopperManager = new HopperManager(this, pickupManager, claimManager);
        ChatInputListener chatInputListener = new ChatInputListener(this);

        new InteractListener(this, claimManager, pickupManager,
                anchorHandler, partnerManager, chatInputListener, menuManager);
        new DispenserListener(this, pickupManager);
        // Register commands
        PluginCommand command = Bukkit.getPluginCommand("clickvillagers");
        if (command != null) {
            command.setExecutor(new ReloadCommand());
        }
        // Check updates
        checkUpdates();
        // Legacy conversions
        LegacyHopperCompatibility.startConversionIfLegacy(this);
        LegacyMessagesCompatibility.removeLegacyMessageFile(this);
    }

    private void checkUpdates() {
        if (Setting.CHECK_UPDATE.isDisabled()) return;
        LOGGER.info("Checking for updates...");
        new UpdateChecker(this, RESOURCE_ID).checkVersion(version -> {
            if (getDescription().getVersion().equals(version)) return;
            LOGGER.info("New version available: " + version);
            MessageParameterizer parameterizer = Message.UPDATE.parameterizer()
                    .put("version", version);
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (!player.isOp()) return;
                parameterizer.send(player);
            });
        });
    }
}
