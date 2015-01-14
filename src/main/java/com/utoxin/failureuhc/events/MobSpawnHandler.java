package com.utoxin.failureuhc.events;

import net.minecraft.entity.passive.EntityRabbit;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class MobSpawnHandler {
	Random randomizer = new Random();

	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent event) {
		if (!(event instanceof LivingSpawnEvent.AllowDespawn)) {
			if (event.entity instanceof EntityRabbit) {
				if (randomizer.nextInt(100) < 15 && ((EntityRabbit) event.entity).getRabbitType() != 99) {
					((EntityRabbit) event.entity).setRabbitType(99);
				}
			}
		}
	}
}
