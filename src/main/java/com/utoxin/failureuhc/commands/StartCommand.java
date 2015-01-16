package com.utoxin.failureuhc.commands;

import com.google.common.collect.Lists;
import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import com.utoxin.failureuhc.utility.LogHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;

import java.util.Iterator;

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
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		player.addChatMessage(new ChatComponentTranslation("message.start.test"));
		FailureUHC.instance.gameStarted = true;

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

			// Reset health and hunger to defaults
			playerObject.setHealth(20);

			// Funky math to reset hunger to defaults
			playerObject.getFoodStats().addStats(20, (5.0f - playerObject.getFoodStats().getSaturationLevel()) / 40.0f);

			// Remove any potion effects
			playerObject.clearActivePotions();
		}

		// TODO: Call or implement our own /spreadplayers command here

		switch (ConfigurationHandler.difficulty) {
			case "easy":
				MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.EASY);
				break;

			case "hard":
				MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.HARD);
				break;

			default:
				MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.NORMAL);
		}

		for (WorldServer server : MinecraftServer.getServer().worldServers) {
			server.setAllowedSpawnTypes(true, true);
			server.getGameRules().setOrCreateGameRule("naturalRegeneration", ConfigurationHandler.naturalRegeneration ? "true" : "false");
			server.getGameRules().setOrCreateGameRule("doDaylightCycle", ConfigurationHandler.doDaylightCycle ? "true" : "false");
		}
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}
}
