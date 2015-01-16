package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class ChatHandler {
	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		if (!FailureUHC.instance.gameStarted) {
			return;
		}

		if (ConfigurationHandler.playerChat == false) {
			event.setCanceled(true);
		} else if (ConfigurationHandler.spectatorChat == false && event.player.theItemInWorldManager.getGameType() == WorldSettings.GameType.SPECTATOR) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		ChatComponentTranslation enabled = (ChatComponentTranslation) new ChatComponentTranslation("message.components.enabled").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN));
		ChatComponentTranslation disabled = (ChatComponentTranslation) new ChatComponentTranslation("message.components.disabled").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));
		ChatComponentTranslation playeronly = (ChatComponentTranslation) new ChatComponentTranslation("message.components.playeronly").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW));

		if (ConfigurationHandler.playerChat == false) {
			event.player.addChatMessage(new ChatComponentTranslation("message.settings.chat", disabled));
		} else if (ConfigurationHandler.spectatorChat == false) {
			event.player.addChatMessage(new ChatComponentTranslation("message.settings.chat", playeronly));
		} else {
			event.player.addChatMessage(new ChatComponentTranslation("message.settings.chat", enabled));
		}

		event.player.addChatMessage(new ChatComponentTranslation("message.settings.spectators", ConfigurationHandler.deadSpectate ? enabled : disabled));

		event.player.addChatMessage(new ChatComponentTranslation("message.settings.regeneration", ConfigurationHandler.naturalRegeneration ? enabled : disabled));
		event.player.addChatMessage(new ChatComponentTranslation("message.settings.radius", ConfigurationHandler.wallRadius));
		event.player.addChatMessage(new ChatComponentTranslation("message.settings.rabbits", ConfigurationHandler.hostileRabbitPercentage));
	}
}
