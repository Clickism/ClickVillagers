package de.clickism.clickvillagers.hopper.config;

import static de.clickism.clickvillagers.ClickVillagersConfig.*;

public class HopperConfig {

    public boolean tickingEnabled;
    public final boolean recipeEnabled;
    public int tickRate;

    public int limitPerChunk;

    public boolean allowZombieVillagers;
    public boolean ignoreBabies;
    public boolean ignoreClaimed;

    public float displayViewRange;
    public boolean blockDisplay;

    public HopperConfig() {
        this.recipeEnabled = CONFIG.get(HOPPER_RECIPE);

        reloadConfig();
    }

    public void reloadConfig() {
        this.tickingEnabled = CONFIG.get(TICK_HOPPERS);
        this.tickRate = CONFIG.get(HOPPER_TICK_RATE);
        this.limitPerChunk = CONFIG.get(HOPPER_LIMIT_PER_CHUNK);
        this.displayViewRange = CONFIG.get(HOPPER_BLOCK_DISPLAY_VIEW_RANGE);

        this.allowZombieVillagers = CONFIG.get(ALLOW_ZOMBIE_VILLAGERS);
        this.ignoreBabies = CONFIG.get(IGNORE_BABY_VILLAGERS);
        this.ignoreClaimed = CONFIG.get(IGNORE_CLAIMED_VILLAGERS);
        this.blockDisplay = CONFIG.get(HOPPER_BLOCK_DISPLAY);
    }
}
