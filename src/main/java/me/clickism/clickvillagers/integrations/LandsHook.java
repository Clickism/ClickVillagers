package me.clickism.clickvillagers.integrations;

import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.enums.FlagTarget;
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.clickism.clickvillagers.ClickVillagers;
import me.clickism.clickvillagers.managers.SkullManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LandsHook {

    private static boolean hasLands = false;
    private static LandsIntegration landsApi;
    private static RoleFlag clickVillagersFlag;

    public LandsHook(ClickVillagers plugin){
        hasLands = Bukkit.getPluginManager().getPlugin("Lands") != null;
        if(!hasLands) return;

        plugin.getLogger().info("Lands plugin found - Added a ClickVillagers RoleFlag");
        landsApi = LandsIntegration.of(plugin);
        clickVillagersFlag = RoleFlag.of(landsApi, FlagTarget.PLAYER, RoleFlagCategory.ACTION, "click_villagers");
        clickVillagersFlag
                .setDisplayName("Pick & Place Villagers")
                .setIcon(SkullManager.getGenericHeadItem())
                .setDescription("Allow a player to pick up and place villagers");
    }

    public static boolean isEnabled(){
        return hasLands;
    }

    public static boolean hasVillagerFlag(Player player, Location location){
        final LandWorld world = landsApi.getWorld(player.getWorld());
        if(world == null) return true;
        return world.hasRoleFlag(player.getUniqueId(), location, clickVillagersFlag);
    }
}