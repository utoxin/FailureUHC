package com.utoxin.failureuhc;

/**
 * FailureUHC
 * Copyright (C) 2015  Matthew Walker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.utoxin.failureuhc.commands.StartCommand;
import com.utoxin.failureuhc.events.*;
import com.utoxin.failureuhc.proxy.IProxy;
import com.utoxin.failureuhc.reference.Reference;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import com.utoxin.failureuhc.utility.LogHelper;
import com.utoxin.failureuhc.worldgen.WorldGenHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS)
public class FailureUHC {
	@Mod.Instance(Reference.MOD_ID)
	public static FailureUHC instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static IProxy proxy;

	// Global Values
	public boolean gameStarted = false;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigurationHandler.init(event.getSuggestedConfigurationFile());
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
		FMLCommonHandler.instance().bus().register(new ChatHandler());

		MinecraftForge.EVENT_BUS.register(new DeathHandler());
		MinecraftForge.EVENT_BUS.register(new ChatHandler());
		MinecraftForge.EVENT_BUS.register(new MobSpawnHandler());
		MinecraftForge.EVENT_BUS.register(new WorldHandler());

		GameRegistry.registerWorldGenerator(new WorldGenHandler(), 0);

		LogHelper.info("Pre Initialization Complete!");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		LogHelper.info("Initialization Complete!");
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LogHelper.info("Post Initialization Complete!");
	}

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		MinecraftServer.getServer().setDifficultyForAllWorlds(EnumDifficulty.PEACEFUL);

		//TODO: Figure out per-world storage
		MinecraftServer.getServer().worldServerForDimension(0).getPerWorldStorage();

		ServerCommandManager manager = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();

		manager.registerCommand(new StartCommand());
	}

	@NetworkCheckHandler
	public boolean networkCheck(Map<String, String> mods, Side side) {
		if (side.isClient()) {
			// Checking the mods in use on the client

			for (String mod : mods.keySet()) {
				switch (mod) {
					case "FML":
					case "Forge":
					case "mcp":
					case "FailureUHC":
						continue;

					default:
						if (ConfigurationHandler.modList.contains(mod.toLowerCase())) {
							LogHelper.info(String.format("Blocking connection because of non-allowed mod : %s", mod));
							return false;
						}
				}
			}
		}

		return true;
	}
}
