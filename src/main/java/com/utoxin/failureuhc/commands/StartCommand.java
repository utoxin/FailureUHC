package com.utoxin.failureuhc.commands;

import com.google.common.collect.Lists;
import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.events.TickHandler;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSpreadPlayers;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import java.util.Iterator;
import java.util.TreeMap;

public class StartCommand extends CommandBase {
	@Override
	public String getName() {
		return "startmatch";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "startmatch";
	}

	@Override
	public void execute(ICommandSender sender, String[] args) throws CommandException {
		for (Object object : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP playerObject = (EntityPlayerMP) object;

			// Remove experience
			playerObject.removeExperienceLevel(playerObject.experienceLevel + 1);

			// Clear inventories
			playerObject.inventory.func_174925_a(null, -1, -1, null);
			playerObject.inventoryContainer.detectAndSendChanges();
			if (!playerObject.capabilities.isCreativeMode) {
				playerObject.updateHeldItem();
			}

			// Reset all achievements
			Iterator iterator = Lists.reverse(AchievementList.achievementList).iterator();
			while (iterator.hasNext()) {
				Achievement achievement = (Achievement)iterator.next();
				playerObject.func_175145_a(achievement);
			}

			// Reset health
			playerObject.setHealth(20);

			// Funky math to reset hunger to defaults
			playerObject.getFoodStats().addStats(20, (5.0f - playerObject.getFoodStats().getSaturationLevel()) / 40.0f);

			// Remove any potion effects
			playerObject.clearActivePotions();
		}

		int minPlayerSpread = (ConfigurationHandler.wallRadius * 2) / Math.max(3, MinecraftServer.getServer().getConfigurationManager().playerEntityList.size() / 4);

		CommandSpreadPlayers spreadPlayers = new CommandSpreadPlayers();
		spreadPlayers.execute(sender, new String[]{"0", "0", String.format("%d", minPlayerSpread), String.format("%d", ConfigurationHandler.wallRadius), "false", "@a"});

		if (ConfigurationHandler.difficulty.equals("easy")) {
			MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.EASY);
		} else if (ConfigurationHandler.difficulty.equals("hard")) {
			MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.HARD);
		} else {
			MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.NORMAL);
		}

		for (WorldServer server : MinecraftServer.getServer().worldServers) {
			server.getGameRules().setOrCreateGameRule("naturalRegeneration", ConfigurationHandler.naturalRegeneration ? "true" : "false");
			server.getGameRules().setOrCreateGameRule("doDaylightCycle", ConfigurationHandler.doDaylightCycle ? "true" : "false");
		}

		FailureUHC.instance.gameStarted = true;

		TickHandler.scheduledMessages = new TreeMap<Long, IChatComponent>();
		TickHandler.scheduledMessages.put(MinecraftServer.getServer().getCurrentTime() + 10000, new ChatComponentTranslation("message.start.timer", 5));
		TickHandler.scheduledMessages.put(MinecraftServer.getServer().getCurrentTime() + 12000, new ChatComponentTranslation("message.start.timer", 4));
		TickHandler.scheduledMessages.put(MinecraftServer.getServer().getCurrentTime() + 14000, new ChatComponentTranslation("message.start.timer", 3));
		TickHandler.scheduledMessages.put(MinecraftServer.getServer().getCurrentTime() + 16000, new ChatComponentTranslation("message.start.timer", 2));
		TickHandler.scheduledMessages.put(MinecraftServer.getServer().getCurrentTime() + 18000, new ChatComponentTranslation("message.start.timer", 1));
		TickHandler.scheduledMessages.put(MinecraftServer.getServer().getCurrentTime() + 20000, new ChatComponentTranslation("message.start.go"));
		TickHandler.nextTimerMessageTime = MinecraftServer.getServer().getCurrentTime() + 20000 + ConfigurationHandler.episodeMinutes * 60 * 1000;
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}
}
