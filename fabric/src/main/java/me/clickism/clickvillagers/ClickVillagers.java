package me.clickism.clickvillagers;

import me.clickism.clickvillagers.callback.VehicleUseEntityCallback;
import me.clickism.clickvillagers.callback.VillagerUseBlockCallback;
import me.clickism.clickvillagers.callback.VillagerUseEntityCallback;
import me.clickism.clickvillagers.config.Config;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ClickVillagers implements ModInitializer {
	public static final String MOD_ID = "clickvillagers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register(new VillagerUseEntityCallback());
		UseEntityCallback.EVENT.register(new VehicleUseEntityCallback());
		UseBlockCallback.EVENT.register(new VillagerUseBlockCallback());
		try {
			new Config("ClickVillagers.json");
		} catch (IOException e) {
			LOGGER.error("Failed to load config file", e);
		}
	}
	
}