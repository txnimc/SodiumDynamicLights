/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin.lightsource;

import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.api.DynamicLightHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
#if AFTER_21_1
import net.minecraft.world.entity.decoration.BlockAttachedEntity;

@Mixin(BlockAttachedEntity.class)
#else
import net.minecraft.world.entity.decoration.HangingEntity;

@Mixin(HangingEntity.class)
#endif
public abstract class BlockAttachedEntityMixin extends Entity implements DynamicLightSource {
	public BlockAttachedEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		#if AFTER_21_1 super.tick(); #endif
		// We do not want to update the entity on the server.
		if (this.level().isClientSide()) {
			if (this.isRemoved()) {
				this.sdl$setDynamicLightEnabled(false);
			} else {
				if (!SodiumDynamicLights.get().config.getEntitiesLightSource().get() || !DynamicLightHandlers.canLightUp(this))
					this.sdl$resetDynamicLight();
				else
					this.sdl$dynamicLightTick();
				SodiumDynamicLights.updateTracking(this);
			}
		}
	}
}