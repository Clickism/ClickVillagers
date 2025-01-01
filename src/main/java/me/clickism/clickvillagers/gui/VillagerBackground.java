package me.clickism.clickvillagers.gui;

import me.clickism.clickgui.menu.Button;
import me.clickism.clickgui.menu.MenuBackground;
import me.clickism.clickvillagers.message.MessageType;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

public class VillagerBackground implements MenuBackground {
    private static final Button DARK_BUTTON = Button.withIcon(Material.BLACK_STAINED_GLASS_PANE)
            .setName("&8x")
            .setOnClick((player, view, slot) -> MessageType.FAIL.playSound(player));
    private static final Button LIGHT_BUTTON = Button.withIcon(Material.GRAY_STAINED_GLASS_PANE)
            .setName("&8x")
            .setOnClick((player, view, slot) -> MessageType.FAIL.playSound(player));

    @Override
    public @Nullable Button getButton(int i) {
        return (i / 9) % 2 == 0 ? DARK_BUTTON : LIGHT_BUTTON;
    }
}
