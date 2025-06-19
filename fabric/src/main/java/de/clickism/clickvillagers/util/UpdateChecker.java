/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clickvillagers.util;

import com.google.gson.*;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class UpdateChecker {

    private static final String API_URL = "https://api.modrinth.com/v2/project/{id}/version";

    private final String projectId;
    private final String loader;
    @Nullable
    private final String minecraftVersion;

    /**
     * Create a new update checker for the given project.
     *
     * @param projectId        the project ID
     * @param loader           the loader
     * @param minecraftVersion the minecraft version, or null for any version
     */
    public UpdateChecker(String projectId, String loader, @Nullable String minecraftVersion) {
        this.projectId = projectId;
        this.loader = loader;
        this.minecraftVersion = minecraftVersion;
    }

    /**
     * Check the latest version of the project for the given loader and minecraft version
     * and call the consumer with it.
     *
     * @param consumer the consumer
     */
    public void checkVersion(Consumer<String> consumer) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL.replace("{id}", projectId)))
                    .GET()
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAcceptAsync(response -> {
                        if (response.statusCode() != 200) return;
                        JsonArray versionsArray = JsonParser.parseString(response.body()).getAsJsonArray();
                        String latestVersion = getLatestVersion(versionsArray);
                        if (latestVersion == null) return;
                        consumer.accept(latestVersion);
                    });
        } catch (Exception ignored) {
        }
    }

    /**
     * Get the latest compatible version from the versions array.
     *
     * @param versions the versions array
     * @return the latest compatible version
     */
    @Nullable
    protected String getLatestVersion(JsonArray versions) {
        return versions.asList().stream()
                .map(JsonElement::getAsJsonObject)
                .filter(this::isVersionCompatible)
                .map(version -> version.get("version_number").getAsString())
                .map(UpdateChecker::getRawVersion)
                .max(String::compareTo)
                .orElse(null);
    }

    /**
     * Check if the version is compatible for the given loader and minecraft version.
     *
     * @param version the version
     * @return true if the version is valid
     */
    protected boolean isVersionCompatible(JsonObject version) {
        JsonArray versions = version.get("game_versions").getAsJsonArray();
        JsonArray loaders = version.get("loaders").getAsJsonArray();
        return (minecraftVersion == null || versions.contains(new JsonPrimitive(minecraftVersion)))
                && loaders.contains(new JsonPrimitive(loader));
    }

    /*
     * Get the raw version from a version string.
     * i.E: "fabric-1.2+1.17.1" -> "1.2"
     */
    public static String getRawVersion(String version) {
        if (version.isEmpty()) return version;
        version = version.replaceAll("^\\D+", "");
        String[] split = version.split("\\+");
        return split[0];
    }
}
