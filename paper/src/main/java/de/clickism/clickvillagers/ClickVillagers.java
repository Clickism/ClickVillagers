/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.clickism.clickgui.menu.MenuManager;
import de.clickism.clickvillagers.command.Permission;
import de.clickism.clickvillagers.entity.SnapshotSaver;
import de.clickism.clickvillagers.gui.ChatInputListener;
import de.clickism.clickvillagers.hopper.HopperManager;
import de.clickism.clickvillagers.listener.*;
import de.clickism.clickvillagers.message.Message;
import de.clickism.clickvillagers.villager.AnchorManager;
import de.clickism.clickvillagers.villager.ClaimManager;
import de.clickism.clickvillagers.villager.PartnerManager;
import de.clickism.clickvillagers.villager.PickupManager;
import de.clickism.configured.papercommandadapter.PaperCommandAdapter;
import de.clickism.configured.papercommandadapter.command.GetCommand;
import de.clickism.configured.papercommandadapter.command.PathCommand;
import de.clickism.configured.papercommandadapter.command.ReloadCommand;
import de.clickism.configured.papercommandadapter.command.SetCommand;
import de.clickism.linen.core.Linen;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;
import static de.clickism.clickvillagers.message.Message.UPDATE;

@SuppressWarnings("UnstableApiUsage")
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
        Linen.initialize(this);
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
        PickupManager pickupManager = new PickupManager(this, new SnapshotSaver(), claimManager, anchorHandler);
        HopperManager hopperManager = new HopperManager(this, pickupManager, claimManager);
        // Set up config listeners
        HOPPER_TICK_RATE.onChange(ticks -> hopperManager.restartTasks());
        HOPPER_RECIPE.onChange(enabled -> {
            if (enabled) {
                hopperManager.registerHopperRecipe();
            } else {
                hopperManager.unregisterHopperRecipe();
            }
        });
        ChatInputListener chatInputListener = new ChatInputListener(this);

        CooldownManager cooldownManager = new CooldownManager(() -> COOLDOWN.get() * 1000L);

        new InteractListener(this, claimManager, pickupManager,
                anchorHandler, partnerManager, chatInputListener, menuManager, cooldownManager);
        new DispenserListener(this, pickupManager);
        new TradeListener(this);

        // Register Commands
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("clickvillagers");
            root.then(PaperCommandAdapter.ofConfig(CONFIG)
                    .requires(sender -> Permission.CONFIG.has(sender.getSender()))
                    .add(new SetCommand(Message.CONFIG_SET::send))
                    .add(new GetCommand(Message.CONFIG_GET::send))
                    .add(new ReloadCommand(Message.CONFIG_RELOAD::send))
                    .add(new PathCommand(Message.CONFIG_PATH::send))
                    .buildRoot());
            commands.registrar().register(root.build());
        });

        // Check updates
        if (CHECK_UPDATES.get()) {
            checkUpdates();
            new JoinListener(this, () -> newerVersion);
        }
        // Metrics
        setupBStats(hopperManager);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void checkUpdates() {
        LOGGER.info("Checking for updates...");
        new ModrinthUpdateChecker(PROJECT_ID, "paper", null).checkVersion(version -> {
            if (getPluginMeta().getVersion().equals(version)) return;
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
        int hopperTickRate = TICK_HOPPERS.get() ? HOPPER_TICK_RATE.get() : 0;
        metrics.addCustomChart(new SimplePie("hopper_tick_rate", () ->
                String.valueOf(hopperTickRate)));
        metrics.addCustomChart(new SimplePie("language", () ->
                String.valueOf(LANGUAGE.get())));
    }
}
