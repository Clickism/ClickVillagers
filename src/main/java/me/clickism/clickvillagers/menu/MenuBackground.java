package me.clickism.clickvillagers.menu;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuBackground {

    private static final List<Integer> line1 = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8));
    private static final List<Integer> line2 = new ArrayList<>(Arrays.asList(9,10,11,12,13,14,15,16,17));
    private static final List<Integer> line3 = new ArrayList<>(Arrays.asList(18,19,20,21,22,23,24,25,26));
    private static final List<Integer> line4 = new ArrayList<>(Arrays.asList(27,28,29,30,31,32,33,34,35));
    private static final List<Integer> line5 = new ArrayList<>(Arrays.asList(36,37,38,39,40,41,42,43,44));

    public static Inventory getBackground(String title) {
        Inventory inv = Bukkit.createInventory(null, 27, title);
        line1.forEach(slot -> {
            inv.setItem(slot, Buttons.BLANK_GRAY.item().clone());
        });
        line2.forEach(slot -> {
            inv.setItem(slot, Buttons.BLANK_BLACK.item().clone());
        });
        line3.forEach(slot -> {
            inv.setItem(slot, Buttons.BLANK_GRAY.item().clone());
        });
        return inv;
    }
}
