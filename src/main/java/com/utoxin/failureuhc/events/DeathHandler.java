package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.FailureUHC;
import com.utoxin.failureuhc.utility.ConfigurationHandler;
import com.utoxin.failureuhc.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DeathHandler {
	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event) {
		if (FailureUHC.instance.gameStarted && ConfigurationHandler.deadSpectate && event.entityLiving instanceof EntityPlayerMP && !(event.entityLiving instanceof FakePlayer)) {
			LogHelper.info(String.format("Setting to spectator : %s", event.entity.toString()));
			((EntityPlayerMP) event.entityLiving).setGameType(WorldSettings.GameType.SPECTATOR);
		}
	}
}
