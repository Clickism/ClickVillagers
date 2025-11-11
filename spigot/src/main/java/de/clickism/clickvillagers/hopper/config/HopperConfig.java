package de.clickism.clickvillagers.hopper.config;

import de.clickism.configured.Config;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class HopperConfig {

    public boolean tickingEnabled;
    public boolean recipeEnabled;
    public int tickRate;

    public boolean allowZombieVillagers;
    public boolean ignoreBabies;
    public boolean ignoreClaimed;

    public float displayViewRange;
    public boolean blockDisplay;

    public HopperConfig() {
        reloadConfig();
    }

    public void reloadConfig() {
        Config config = CONFIG;

        this.tickingEnabled = config.get(TICK_HOPPERS);
        this.recipeEnabled = config.get(HOPPER_RECIPE);
        this.tickRate = config.get(HOPPER_TICK_RATE);
        this.displayViewRange = config.get(HOPPER_BLOCK_DISPLAY_VIEW_RANGE);

        this.allowZombieVillagers = config.get(ALLOW_ZOMBIE_VILLAGERS);
        this.ignoreBabies = config.get(IGNORE_BABY_VILLAGERS);
        this.ignoreClaimed = config.get(IGNORE_CLAIMED_VILLAGERS);
        this.blockDisplay = config.get(HOPPER_BLOCK_DISPLAY);
    }
}
