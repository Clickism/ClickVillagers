/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clickvillagers.villager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.clickism.clickvillagers.listener.AutoRegistered;
import me.clickism.clickvillagers.serialization.JSONDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PartnerManager implements Listener {

    private static final String FILE_NAME = "partners.json";

    private final JavaPlugin plugin;
    private final JSONDataManager dataManager;

    private final Map<UUID, Set<String>> partners = new HashMap<>();

    @AutoRegistered
    public PartnerManager(JavaPlugin plugin) throws IOException {
        this.plugin = plugin;
        File directory = new File(plugin.getDataFolder(), "data");
        this.dataManager = new JSONDataManager(plugin, directory, FILE_NAME, true);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        load();
    }

    public Set<String> getPartners(UUID uuid) {
        return partners.getOrDefault(uuid, Set.of());
    }

    public boolean isPartner(UUID uuid, String partner) {
        return partners.getOrDefault(uuid, Set.of())
                .contains(partner);
    }

    public void addPartner(UUID uuid, String partner) {
        partners.computeIfAbsent(uuid, key -> new HashSet<>())
                .add(partner);
    }

    public boolean removePartner(UUID uuid, String partner) {
        return partners.getOrDefault(uuid, Set.of())
                .remove(partner);
    }

    // Save data on plugin disable
    @EventHandler
    private void onDisable(PluginDisableEvent event) {
        if (!event.getPlugin().equals(plugin)) return;
        save();
    }

    private void save() {
        JsonObject json = new JsonObject();
        partners.forEach((uuid, partners) -> {
            JsonArray array = new JsonArray();
            partners.forEach(array::add);
            json.add(uuid.toString(), array);
        });
        dataManager.save(json);
    }

    private void load() {
        JsonObject root = dataManager.getRoot();
        root.entrySet().forEach(entry -> {
            UUID uuid = UUID.fromString(entry.getKey());
            Set<String> partners = new HashSet<>();
            JsonArray array = entry.getValue().getAsJsonArray();
            array.forEach(partner -> partners.add(partner.getAsString()));
            this.partners.put(uuid, partners);
        });
    }
}
