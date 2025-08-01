package com.globalista.makeitrain;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

@Environment(EnvType.CLIENT)
public class MakeItRain implements ClientModInitializer {
	public static final String MOD_ID = "make-it-rain";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		File configFile = FabricLoader.getInstance().getConfigDir().resolve("make-it-rain.json").toFile();
		Config config = Config.loadConfigFile(configFile);
		config.saveConfigFile(configFile);

		LOGGER.info("Make It Rain!");

	}
}