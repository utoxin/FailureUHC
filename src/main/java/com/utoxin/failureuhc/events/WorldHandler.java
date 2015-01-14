package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldHandler {
	@SubscribeEvent
	public void onLoad(WorldEvent.Load event) {
		event.world.getGameRules().setOrCreateGameRule("naturalRegeneration", ConfigurationHandler.naturalRegeneration ? "true" : "false");
	}
}
