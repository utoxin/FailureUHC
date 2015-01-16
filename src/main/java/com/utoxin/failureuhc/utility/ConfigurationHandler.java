package com.utoxin.failureuhc.utility;

import com.utoxin.failureuhc.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;

public class ConfigurationHandler {
	public static Configuration configuration;

	// Spectator Settings
	public static boolean deadSpectate = false;
	public static boolean spectatorChat = false;

	// Worldgen Settings
	public static int wallRadius = 500;
	public static int hostileRabbitPercentage = 15;

	// Game Rule Settings
	public static boolean naturalRegeneration = false;
	public static boolean doDaylightCycle = true;
	public static boolean playerChat = true;
	public static String difficulty = "normal";

	// Mod Permissions Settings
	public static boolean doWhitelist = true;
	public static ArrayList<String> modList = null;


	public static void init(File configFile) {
		if (configuration == null) {
			configuration = new Configuration(configFile);
			loadConfiguration();
		}
	}

	private static void loadConfiguration() {
		// Spectator Settings
		deadSpectate = configuration.getBoolean("deadSpectate", "Spectator Settings", false, "Do dead players get to spectate?");
		spectatorChat = configuration.getBoolean("spectatorChat", "Spectator Settings", false, "Can spectators use chat?");

		// Worldgen
		wallRadius = configuration.getInt("wallRadius", "Worldgen Settings", 500, 50, 5000, "How far from 0,0 should the wall generate?");
		hostileRabbitPercentage = configuration.getInt("hostileRabbitPercentage", "Worldgen Settings", 15, 0, 100, "What percentage of rabbits should be hostile?");

		// Game Rules
		naturalRegeneration = configuration.getBoolean("naturalRegeneration", "Game Rule Settings", false, "Does health regen naturally?");
		playerChat = configuration.getBoolean("playerChat", "Game Rule Settings", true, "Can anyone use chat during the game? (To control just spectators, use the spectatorChat setting.)");
		difficulty = configuration.getString("difficulty", "Game Rule Settings", "normal", "What should the difficulty be? (easy/normal/hard)");
		doDaylightCycle = configuration.getBoolean("doDaylightCycle", "Game Rule Settings", true, "Should the daylight cycle run during the game?");

		// Mod Permission Settings
		doWhitelist = configuration.getBoolean("doWhitelist", "Mod Permission Settings", true, "Should we check what mods a client has installed against our whitelist?");
		String[] baseModList = configuration.getStringList("modList", "Mod Permission Settings", new String[]{"FML", "Forge", "mcp", "FailureUHC"}, "What mods should be whitelisted? (Forge / FailureUHC are added automatically, and are here simply as examples.)");

		modList = new ArrayList<>();
		for (String mod : baseModList) {
			modList.add(mod.toLowerCase());
		}

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
