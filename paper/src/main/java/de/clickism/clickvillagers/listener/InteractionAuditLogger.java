/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.listener;

import de.clickism.clickvillagers.ClickVillagers;
import de.clickism.clickvillagers.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;

import static de.clickism.clickvillagers.ClickVillagersConfig.DISCORD_WEBHOOK_ENABLED;
import static de.clickism.clickvillagers.ClickVillagersConfig.DISCORD_WEBHOOK_URL;
import static de.clickism.clickvillagers.ClickVillagersConfig.LOG_VILLAGER_INTERACTIONS;

public class InteractionAuditLogger {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public VillagerDetails capture(LivingEntity villager) {
        Location location = villager.getLocation();
        String customName = villager.getCustomName();
        return new VillagerDetails(
                villager.getUniqueId(),
                villager.getType().key().asMinimalString(),
                Utils.getVillagerProfession(villager).key().asMinimalString(),
                customName == null ? "unnamed" : customName,
                location.getWorld() == null ? "unknown" : location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ()
        );
    }

    public void pickup(Player player, VillagerDetails villager) {
        publish("PICKUP", player, villager);
    }

    public void place(Player player, LivingEntity villager) {
        publish("PLACE", player, capture(villager));
    }

    private void publish(String action, Player player, VillagerDetails villager) {
        String message = String.format(Locale.ROOT,
                "[Villager %s] player=%s player_uuid=%s villager_uuid=%s type=%s profession=%s name=%s location=%s %.1f %.1f %.1f",
                action, player.getName(), player.getUniqueId(), villager.uuid(), villager.type(),
                villager.profession(), villager.name(), villager.world(), villager.x(), villager.y(), villager.z());

        if (LOG_VILLAGER_INTERACTIONS.get()) {
            ClickVillagers.LOGGER.info(message);
        }
        if (!DISCORD_WEBHOOK_ENABLED.get()) return;

        String webhookUrl = DISCORD_WEBHOOK_URL.get().trim();
        if (webhookUrl.isEmpty()) {
            ClickVillagers.LOGGER.warning("Discord interaction webhook is enabled, but discord_webhook_url is empty.");
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(webhookUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"content\":\"" + escapeJson(message)
                            + "\",\"allowed_mentions\":{\"parse\":[]}}"))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .thenAccept(response -> {
                        if (response.statusCode() < 200 || response.statusCode() >= 300) {
                            ClickVillagers.LOGGER.warning("Discord interaction webhook returned HTTP " + response.statusCode());
                        }
                    })
                    .exceptionally(exception -> {
                        ClickVillagers.LOGGER.warning("Failed to send Discord interaction webhook: " + exception.getMessage());
                        return null;
                    });
        } catch (IllegalArgumentException exception) {
            ClickVillagers.LOGGER.warning("Invalid Discord interaction webhook URL: " + exception.getMessage());
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    public record VillagerDetails(UUID uuid, String type, String profession, String name,
                                  String world, double x, double y, double z) {
    }
}
