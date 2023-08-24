package me.clickism.clickvillagers.events;

import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class UpdateChecker implements Listener {

    private String url = "https://api.spigotmc.org/legacy/update.php?resource=";
    private String id = "111424";

    private boolean isAvailable;

    public UpdateChecker() {

    }

    public boolean isAvailable() {
        return isAvailable;
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if(event.getPlayer().isOp())
            if(isAvailable)
                event.getPlayer().sendMessage(Messages.get("update"));
    }

    public void check() {
        isAvailable = checkUpdate();
    }

    private boolean checkUpdate() {
        Bukkit.getLogger().info("Check for updates...");
        try {
            String localVersion = ClickVillagers.getVersion();
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url + id).openConnection();
            connection.setRequestMethod("GET");
            String raw = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

            String remoteVersion;
            if(raw.contains("-")) {
                remoteVersion = raw.split("-")[0].trim();
            } else {
                remoteVersion = raw;
            }

            if(!localVersion.equalsIgnoreCase(remoteVersion))
                return true;

        } catch (Exception e) {
            return false;
        }
        return false;
    }

}