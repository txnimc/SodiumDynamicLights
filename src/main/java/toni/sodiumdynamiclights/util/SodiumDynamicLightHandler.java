/*
 * Copyright © 2023 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.util;

import toni.sodiumdynamiclights.SodiumDynamicLights;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SodiumDynamicLightHandler {
	// Stores the current light position being used by ArrayLightDataCache#get
	// We use ThreadLocal because Sodium's chunk builder is multithreaded, otherwise it will break
	// catastrophically.
	ThreadLocal<BlockPos.MutableBlockPos> POS = ThreadLocal.withInitial(BlockPos.MutableBlockPos::new);

	static int getLightmap(BlockPos pos, int word, int lightmap) {
		if (!SodiumDynamicLights.get().config.getDynamicLightsMode().isEnabled())
			return lightmap;

		// Equivalent to world.getBlockState(pos).isOpaqueFullCube(world, pos)
		if (/*LightDataAccess.unpackFO(word)*/ (word >>> 30 & 1) != 0)
			return lightmap;

		double dynamic = SodiumDynamicLights.get().getDynamicLightLevel(pos);
		return SodiumDynamicLights.get().getLightmapWithDynamicLight(dynamic, lightmap);
	}
}
