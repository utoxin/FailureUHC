package com.utoxin.failureuhc.events;

import com.utoxin.failureuhc.utility.ConfigurationHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldGenHandler {
	private int wallRadius;
	private int wallChunk;
	private int wallChunkSubX;
	private int wallChunkSubZ;
	private boolean generateSpawnpoint;
	private boolean runGeneration;

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void populateChunkPopulateEvent(PopulateChunkEvent.Populate event) {
		init(event.chunkX, event.chunkZ, event.world);

		// Ban terraingen likely to punch holes in the wall in any chunks near the wall
		if (nearWallChunk(event.chunkX, event.chunkZ)) {
			if (runGeneration && event.world.provider.getDimensionId() == 0) {
				if (event.type == PopulateChunkEvent.Populate.EventType.LAKE) {
					event.setResult(Event.Result.DENY);
				} else if (event.type == PopulateChunkEvent.Populate.EventType.LAVA) {
					event.setResult(Event.Result.DENY);
				} else if (event.type == PopulateChunkEvent.Populate.EventType.DUNGEON) {
					event.setResult(Event.Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void populateChunkPostEvent(PopulateChunkEvent.Post event) {
		generate(event.chunkX, event.chunkZ, event.world);
	}

	public void generate(int chunkX, int chunkZ, World world) {
		init(chunkX, chunkZ, world);

		if (!runGeneration) {
			return;
		}

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

	private void init(int chunkX, int chunkZ, World world) {
		generateSpawnpoint = false;
		runGeneration = false;
		wallRadius = ConfigurationHandler.wallRadius;

		switch (world.provider.getDimensionId()) {
			case 0: // Overworld
				generateSpawnpoint = true;
				break;
			case -1: // Nether
				wallRadius = wallRadius / 8;
				break;
			default:
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
			if (chunkZ <= wallChunk && chunkZ >= negativeWallChunk) {
				return true;
			} else {
				return false;
			}
		}

		if (chunkZ == wallChunk || chunkZ == negativeWallChunk) {
			if (chunkX <= wallChunk && chunkX >= negativeWallChunk) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}

	private boolean nearWallChunk(int chunkX, int chunkZ) {
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (isWallChunk(chunkX + x, chunkZ + z)) {
					return true;
				}
			}
		}

		return false;
	}
}
