/*
 * Copyright © 2023 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin.sodium;

import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.util.SodiumDynamicLightHandler;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = {
		"me.jellysquid.mods.sodium.client.model.light.data.ArrayLightDataCache",
		"net.caffeinemc.mods.sodium.client.model.light.data.ArrayLightDataCache"
	}, remap = false)
public abstract class ArrayLightDataCacheMixin {
	@Dynamic
	@Inject(method = "get(III)I", at = @At("HEAD"), require = 0)
	private void sodiumdynamiclights$storeLightPos(int x, int y, int z, CallbackInfoReturnable<Integer> cir) {
		if (!SodiumDynamicLights.get().config.getDynamicLightsMode().isEnabled())
			return;

		// Store the current light position.
		// This is possible under smooth lighting scenarios, because AoFaceData in Sodium runs a get() call
		// before getting the lightmap.
		SodiumDynamicLightHandler.POS.get().set(x, y, z);
	}
}
