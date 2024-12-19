package me.clickism.clickvillagers;

import me.clickism.clickvillagers.callback.VillagerPlaceCallback;
import me.clickism.clickvillagers.callback.VillagerUseEntityCallback;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickVillagers implements ModInitializer {
	public static final String MOD_ID = "clickvillagers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	@Override
	public void onInitialize() {
		UseEntityCallback.EVENT.register(new VillagerUseEntityCallback());
		UseBlockCallback.EVENT.register(new VillagerPlaceCallback());
	}
	
}