/*
 * Copyright 2026 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.platform.neoforge;
//? if neoforge {

/*import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.event.UpdateNotifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import static de.clickism.clickvillagers.ClickVillagersConfig.CHECK_UPDATES;

@Mod(ClickVillagers.MOD_ID)
public class NeoforgeEntrypoint {
    public NeoforgeEntrypoint(IEventBus eventBus) {
        NeoForge.EVENT_BUS.register(new NeoforgeEventListener());

        ClickVillagers.initialize();

        // Register commands
        NeoForge.EVENT_BUS.register(new NeoforgeEventListener.ConfigCommandRegisterListener());

        // Check for updates
        if (CHECK_UPDATES.get()) {
            String modVersion = ModList.get()
                    .getModContainerById(ClickVillagers.MOD_ID)
                    .map(container -> container.getModInfo().getVersion().toString())
                    .orElse(null);
            ClickVillagers.checkUpdates(modVersion, "neoforge");
            var notifier = new UpdateNotifier(ClickVillagers::newerVersion);
            NeoForge.EVENT_BUS.register(new NeoforgeEventListener.JoinListener(notifier));
        }
    }
}
*///?}
