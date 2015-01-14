package com.utoxin.failureuhc.utility;

import com.utoxin.failureuhc.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class ConfigurationHandler {
	public static Configuration configuration;

	// Spectator Settings
	public static boolean deadSpectate = false;
	public static boolean spectatorChat = false;

	// Worldgen Settings
	public static int wallRadius = 500;

	// Game Rule Settings
	public static boolean naturalRegeneration = false;

	public static void init(File configFile) {
		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		// Spectator Settings
		deadSpectate = configuration.getBoolean("deadSpectate", "Spectator Settings", false, "Do dead players get to spectate?");
		spectatorChat = configuration.getBoolean("spectatorChat", "Spectator Settings", false, "Can spectators use game chat?");

		// Worldgen
		wallRadius = configuration.getInt("wallRadius", "Worldgen Settings", 500, 50, 5000, "How far from 0,0 should the wall generate?");

		// Game Rules
		naturalRegeneration = configuration.getBoolean("naturalRegeneration", "Game Rule Settings", false, "Does health regen naturally?");

		if (configuration.hasChanged()) {
			configuration.save();
		}
	}

	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.modID.equalsIgnoreCase(Reference.MOD_ID)) {
			loadConfiguration();
		}
	}
}
