package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatHandler {
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		if (ConfigurationHandler.spectatorChat == false && event.player.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR) {
			event.setCanceled(true);
		}
	}
}
