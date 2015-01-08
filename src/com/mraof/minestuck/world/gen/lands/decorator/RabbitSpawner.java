package com.mraof.minestuck.world.gen.lands.decorator;

import java.util.Random;

import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.mraof.minestuck.world.gen.ChunkProviderLands;

public class RabbitSpawner extends PostDecorator
{
	
	@Override
	public void generateAtChunk(World world, Random random, int chunkX, int chunkZ, ChunkProviderLands provider)
	{
		for(int i = 0; i < 8; i++)
			if(random.nextDouble() < 0.2)
			{
				int x = random.nextInt(16) + (chunkX << 4);
				int z = random.nextInt(16) + (chunkZ << 4);
				BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
				if(world.getBlockState(pos).getBlock().getMaterial().isLiquid())
					continue;
				
				EntityRabbit entity = new EntityRabbit(world);
				entity.setPosition(x, pos.getY(), z);
				entity.func_180482_a(null, null);
				world.spawnEntityInWorld(entity);
			}
	}
	
	@Override
	public float getPriority()
	{
		return 0.7F;
	}
	
}
