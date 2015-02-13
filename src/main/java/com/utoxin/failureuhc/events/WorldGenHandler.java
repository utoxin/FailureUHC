package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.utility.ConfigurationHandler;
import com.utoxin.failureuhc.utility.LogHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class WorldGenHandler {
	@SubscribeEvent
	public void populateChunkEvent(PopulateChunkEvent.Post event) {
		generate(event.chunkX, event.chunkZ, event.world);
	}

	public void generate(int chunkX, int chunkZ, World world) {
		int wallRadius;
		boolean generateSpawnpoint = false;

		switch (world.provider.getDimensionId()) {
			case 0: // Overworld
				wallRadius = ConfigurationHandler.wallRadius;
				generateSpawnpoint = true;
				break;
			case -1: // Nether
				wallRadius = ConfigurationHandler.wallRadius / 8;
				break;
			default:
				return;
		}

		int wallChunk = wallRadius / 16;
		int wallChunkSubX = chunkX >= 0 ? (wallRadius % 16) : (16 - (wallRadius % 16));
		int wallChunkSubZ = chunkZ >= 0 ? (wallRadius % 16) : (16 - (wallRadius % 16));

		BlockPos blockPos;
		IBlockState bedrock = Blocks.bedrock.getDefaultState();

		if (chunkX == wallChunk || chunkX == ((wallChunk * -1) - 1)) {
			for (int z = 0; z < 16; z++) {
				if (Math.abs(chunkZ * 16 + z) > wallRadius) {
					continue;
				}

				for (int y = 1; y < 256; y++) {
					blockPos = new BlockPos(chunkX * 16 + wallChunkSubX, y, chunkZ * 16 + z);
					world.setBlockState(blockPos, bedrock);
				}
			}
		}

		if (chunkZ == wallChunk || chunkZ == ((wallChunk * -1) - 1)) {
			for (int x = 0; x < 16; x++) {
				if (Math.abs(chunkX * 16 + x) > wallRadius) {
					continue;
				}

				for (int y = 1; y < 256; y++) {
					blockPos = new BlockPos(chunkX * 16 + x, y, chunkZ * 16 + wallChunkSubZ);
					world.setBlockState(blockPos, bedrock);
				}
			}
		}

		if (generateSpawnpoint) {
			int spawnChunkX = (ConfigurationHandler.wallRadius + 256) / 16;

			if (Math.abs(spawnChunkX - chunkX) <= 2 && Math.abs(chunkZ) <= 2) {
				IBlockState glass = Blocks.stained_glass.getStateFromMeta(15);
				IBlockState barrier = Blocks.barrier.getDefaultState();

				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						blockPos = new BlockPos(chunkX * 16 + x, 129, chunkZ * 16 + z);
						world.setBlockState(blockPos, barrier);
						blockPos = new BlockPos(chunkX * 16 + x, 128, chunkZ * 16 + z);
						world.setBlockState(blockPos, glass);
					}
				}

				if (chunkZ == -2 || chunkZ == 2) {
					for (int x = 0; x < 16; x++) {
						for (int y = 130; y < 256; y++) {
							blockPos = new BlockPos(chunkX * 16 + x, y, chunkZ * 16 + (chunkZ > 0 ? 15 : 0));
							world.setBlockState(blockPos, barrier);
						}
					}
				}

				int chunkXOffset = chunkX - spawnChunkX;
				if (chunkXOffset == -2 || chunkXOffset == 2) {
					for (int z = 0; z < 16; z++) {
						for (int y = 130; y < 256; y++) {
							blockPos = new BlockPos(chunkX * 16 + (chunkXOffset > 0 ? 15 : 0), y, chunkZ * 16 + z);
							world.setBlockState(blockPos, barrier);
						}
					}
				}
			}
		}
	}
}
