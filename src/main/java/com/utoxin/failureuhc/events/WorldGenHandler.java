package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class WorldGenHandler {
	private int wallRadius;
	private int wallChunk;
	private int wallChunkSubX;
	private int wallChunkSubZ;
	private boolean generateSpawnPoint;
	private boolean runGeneration;
	private Random random = new Random();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void populateChunkPostEvent(PopulateChunkEvent.Post event) {
		generateSpawnPoint = true;
		generateWall(event.chunkX, event.chunkZ, event.world);
		generateWaitingArea(event.chunkX, event.chunkZ, event.world);
	}

	// Re-generates wall when chunks are saved, in case other generation has broken them
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void checkChunkOnSave(ChunkDataEvent.Save event) {
		ChunkCoordIntPair coords = event.getChunk().getChunkCoordIntPair();
		World world = event.getChunk().getWorld();
		generateSpawnPoint = false;

		if (generateWall(coords.chunkXPos, coords.chunkZPos, world)) {
			event.getChunk().setChunkModified();
		}
	}

	private boolean generateWall(int chunkX, int chunkZ, World world) {
		init(chunkX, chunkZ, world);

		if (!runGeneration) {
			return false;
		}

		boolean changesMade = false;
		BlockPos blockPos;

		if (isWallChunk(chunkX, chunkZ)) {
			IBlockState bedrock = Blocks.bedrock.getDefaultState();

			if (chunkX == wallChunk || chunkX == ((wallChunk * -1) - 1)) {
				for (int z = 0; z < 16; z++) {
					if (Math.abs(chunkZ * 16 + z) > wallRadius) {
						continue;
					}

					for (int y = 1; y < 256; y++) {
						blockPos = new BlockPos(chunkX * 16 + wallChunkSubX, y, chunkZ * 16 + z);

						if (world.getBlockState(blockPos) != bedrock) {
							changesMade = true;
							world.setBlockState(blockPos, bedrock);
						}
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

						if (world.getBlockState(blockPos) != bedrock) {
							changesMade = true;
							world.setBlockState(blockPos, bedrock);
						}
					}
				}
			}
		}

		return changesMade;
	}

	private void generateWaitingArea(int chunkX, int chunkZ, World world) {
		if (generateSpawnPoint) {
			BlockPos blockPos;
			int spawnChunkX = (ConfigurationHandler.wallRadius + 256) / 16;

			if (Math.abs(spawnChunkX - chunkX) <= 2 && Math.abs(chunkZ) <= 2) {
				generateFloor(chunkX, chunkZ, world);

				IBlockState barrier = Blocks.barrier.getDefaultState();

				if (chunkZ == -2 || chunkZ == 2) {
					for (int x = 0; x < 16; x++) {
						for (int y = 134; y < 256; y++) {
							blockPos = new BlockPos(chunkX * 16 + x, y, chunkZ * 16 + (chunkZ > 0 ? 15 : 0));
							world.setBlockState(blockPos, barrier);
						}
					}
				}

				int chunkXOffset = chunkX - spawnChunkX;
				if (chunkXOffset == -2 || chunkXOffset == 2) {
					for (int z = 0; z < 16; z++) {
						for (int y = 134; y < 256; y++) {
							blockPos = new BlockPos(chunkX * 16 + (chunkXOffset > 0 ? 15 : 0), y, chunkZ * 16 + z);
							world.setBlockState(blockPos, barrier);
						}
					}
				}
			}
		}
	}

	private void generateFloor(int chunkX, int chunkZ, World world) {
		IBlockState glass = Blocks.stained_glass.getStateFromMeta(random.nextInt(16));
		IBlockState edge = Blocks.stone.getStateFromMeta((random.nextInt(3)+1) * 2);
		IBlockState barrier = Blocks.barrier.getDefaultState();
		BlockPos blockPos;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				blockPos = new BlockPos(chunkX * 16 + x, 132, chunkZ * 16 + z);
				world.setBlockState(blockPos, glass);

				blockPos = new BlockPos(chunkX * 16 + x, 133, chunkZ * 16 + z);
				if (z == 0 || z == 15 || x == 0 || x == 15) {
					world.setBlockState(blockPos, edge);
				} else {
					world.setBlockState(blockPos, barrier);
				}
			}
		}
	}

	private void init(int chunkX, int chunkZ, World world) {
		wallRadius = ConfigurationHandler.wallRadius;

		switch (world.provider.getDimensionId()) {
			case 0: // Over World
				break;
			case -1: // Nether
				generateSpawnPoint = false;
				wallRadius = wallRadius / 8;
				break;
			default:
				generateSpawnPoint = false;
				return;
		}

		runGeneration = true;
		wallChunk = wallRadius / 16;
		wallChunkSubX = chunkX >= 0 ? (wallRadius % 16) : (16 - (wallRadius % 16));
		wallChunkSubZ = chunkZ >= 0 ? (wallRadius % 16) : (16 - (wallRadius % 16));
	}

	private boolean isWallChunk(int chunkX, int chunkZ) {
		int negativeWallChunk = wallChunk * -1 - 1;

		if (chunkX == wallChunk || chunkX == negativeWallChunk) {
			return chunkZ <= wallChunk && chunkZ >= negativeWallChunk;
		}

		if (chunkZ == wallChunk || chunkZ == negativeWallChunk) {
			return chunkX <= wallChunk && chunkX >= negativeWallChunk;
		}

		return false;
	}
}
