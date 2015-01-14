package com.utoxin.failureuhc.client.gui;

import com.utoxin.failureuhc.reference.Reference;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ModConfig extends GuiConfig {
	public ModConfig(GuiScreen guiScreen) {
		super(guiScreen,
			new ConfigElement(ConfigurationHandler.configuration.getCategory("Client Configs")).getChildElements(),
			Reference.MOD_ID,
			false,
			false,
			GuiConfig.getAbridgedConfigPath(ConfigurationHandler.configuration.toString()));
	}
}
