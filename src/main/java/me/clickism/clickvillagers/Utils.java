package me.clickism.clickvillagers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Utils {

    static ClickVillagers plugin;
    public static void setPlugin(ClickVillagers pl) {
        plugin = pl;
    }

    public static String capitalize(String text) {
        char[] array = text.toLowerCase().toCharArray();
        array[0] = Character.toUpperCase(array[0]);
        return String.copyValueOf(array);
    }

    public static void playConfirmSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f,1f);
        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1f,2f);
        }, 2L);
    }

    public static void playFailSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_RESONATE, 1f,.5f);
    }

    /**
     *
     * / &6 gold /
     * &a green /
     * &2 dark green /
     * &b aqua /
     * &c red /
     * &r reset /
     * &7 gray /
     * &8 dark gray /
     * &3 dark aqua /
     * &5 dark purple /
     * &4 dark red /
     * &1 dark blue /
     * &d light purple /
     * &e yellow /
     * &f white /
     * &l bold /
     * &9 blue /
     *
     * @return colored version
     **/
    public static String colorize(String text) {
        if (text == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
