package com.utoxin.failureuhc.commands;

import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

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
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}
}
