package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.TreeMap;

/**
 * FailureUHC
 * Copyright (C) 2015  Matthew Walker
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

public class TickHandler {
	static public TreeMap<Long, IChatComponent> scheduledMessages;
	static public long nextTimerMessageTime = 0;
	static public int periodsPassed = 0;

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		String title, subtitle;
		long currentTime = MinecraftServer.getServer().getCurrentTime();

		if (FailureUHC.instance.gameStarted && scheduledMessages.size() > 0 && scheduledMessages.firstKey() < currentTime) {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(scheduledMessages.get(scheduledMessages.firstKey()));
			scheduledMessages.remove(scheduledMessages.firstKey());

			if (scheduledMessages.size() == 0 && ConfigurationHandler.showTitleTimers) {
				title = String.format("{text:\"Start Episode %d\",bold:true}", 1);
				subtitle = String.format("{text:\"%s players alive...\",color:gray,italic:true}", "???");

				sendTitles(title, subtitle);
			}
		}

		if (FailureUHC.instance.gameStarted && nextTimerMessageTime > 0 && nextTimerMessageTime < currentTime) {
			periodsPassed++;
			nextTimerMessageTime += ConfigurationHandler.episodeMinutes * 60 * 1000;

			if (ConfigurationHandler.showTitleTimers) {
				title = String.format("{text:\"Start Episode %d\",bold:true}", periodsPassed + 1);
				subtitle = String.format("{text:\"%s players alive...\",color:gray,italic:true}", "???");

				sendTitles(title, subtitle);
			}

			if (ConfigurationHandler.showChatTimers) {
				if (periodsPassed < ConfigurationHandler.centerEpisode) {
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("message.timer.period", periodsPassed * ConfigurationHandler.episodeMinutes));
				} else {
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("message.timer.center", periodsPassed * ConfigurationHandler.episodeMinutes));
				}
			}
		}
	}

	public void sendTitles(String title, String subtitle) {
		for (Object object : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP playerObject = (EntityPlayerMP) object;

			IChatComponent subtitleComponent = IChatComponent.Serializer.jsonToComponent(subtitle);
			IChatComponent titleComponent = IChatComponent.Serializer.jsonToComponent(title);

			S45PacketTitle s45packettitle;

			try {
				s45packettitle = new S45PacketTitle(S45PacketTitle.Type.SUBTITLE, ChatComponentProcessor.func_179985_a(playerObject, subtitleComponent, playerObject));
				playerObject.playerNetServerHandler.sendPacket(s45packettitle);

				s45packettitle = new S45PacketTitle(S45PacketTitle.Type.TITLE, ChatComponentProcessor.func_179985_a(playerObject, titleComponent, playerObject));
				playerObject.playerNetServerHandler.sendPacket(s45packettitle);
			} catch (CommandException e) {
				e.printStackTrace();
			}
		}
	}
}
