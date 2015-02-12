package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class MobSpawnHandler {
	Random randomizer = new Random();

	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event) {
		// Prevent all mob spawns (other than villagers) before game start
		if (FailureUHC.instance.gameStarted == false && event.entity instanceof EntityLivingBase && !(event.entity instanceof EntityPlayerMP) && !(event.entity instanceof EntityVillager)) {
			event.setCanceled(true);
		} else {
			if (FailureUHC.instance.gameStarted == false && event.entity instanceof EntityPlayerMP && event.entity.dimension == 0) {
				event.entity.setPosition(ConfigurationHandler.wallRadius + 257, 131, 1);

				BlockPos spawnPoint = new BlockPos(ConfigurationHandler.wallRadius + 257, 131, 1);
				((EntityPlayerMP) event.entity).setSpawnPoint(spawnPoint, true);
			}
		}

		// Set some rabbits to be killer bunnies
		if (FailureUHC.instance.gameStarted == true && event.entity instanceof EntityRabbit) {
			if (randomizer.nextInt(100) < ConfigurationHandler.hostileRabbitPercentage && ((EntityRabbit) event.entity).getRabbitType() != 99) {
				((EntityRabbit) event.entity).setRabbitType(99);
			}
		}
	}
}
