package me.clickism.clickvillagers.gui;

import me.clickism.clickgui.menu.Button;
import me.clickism.clickgui.menu.MenuView;
import me.clickism.clickvillagers.message.Message;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BackButton extends Button {
    protected BackButton(MenuView previous) {
        super(Message.BUTTON_BACK.toIcon(Material.MAP)
                .hideAllAttributes());
        setOnClick((player, view, slot) -> {
            previous.reopen();
            playSound(player);
        });
    }

    public static void playSound(Player player) {
        player.playSound(player, Sound.UI_LOOM_SELECT_PATTERN, 1, 1);
    }

    public static BackButton to(MenuView previous) {
        return new BackButton(previous);
    }
}
