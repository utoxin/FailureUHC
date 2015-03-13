package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class MobSpawnHandler {
	Random random = new Random();

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		// Prevent all mob spawns (other than villagers) before game start
		if (!FailureUHC.instance.gameStarted && event.entity instanceof EntityLivingBase && !(event.entity instanceof EntityPlayerMP) && !(event.entity instanceof EntityVillager)) {
			event.setCanceled(true);
		} else {
			if (!FailureUHC.instance.gameStarted && event.entity instanceof EntityPlayerMP && event.entity.dimension == 0) {
				((EntityPlayerMP)event.entity).playerNetServerHandler.setPlayerLocation(ConfigurationHandler.wallRadius + 256.5, 134, 0.5, 90, 0);

				BlockPos spawnPoint = new BlockPos(ConfigurationHandler.wallRadius + 256.5, 134, 1);
				((EntityPlayerMP) event.entity).setSpawnPoint(spawnPoint, true);
			}
		}

		// Set some rabbits to be killer bunnies
		if (FailureUHC.instance.gameStarted && event.entity instanceof EntityRabbit) {
			if (random.nextInt(100) < ConfigurationHandler.hostileRabbitPercentage && ((EntityRabbit) event.entity).getRabbitType() != 99) {
				((EntityRabbit) event.entity).setRabbitType(99);
			}
		}

		// Set up Title delays
		if (ConfigurationHandler.showTitleTimers && event.entity instanceof EntityPlayerMP) {
			S45PacketTitle s45packet = new S45PacketTitle(20, 160, 20);
			((EntityPlayerMP)event.entity).playerNetServerHandler.sendPacket(s45packet);
		}
	}
}
