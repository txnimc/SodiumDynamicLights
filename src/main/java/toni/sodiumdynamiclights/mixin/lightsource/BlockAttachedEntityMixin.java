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
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockAttachedEntity.class)
public abstract class BlockAttachedEntityMixin extends Entity implements DynamicLightSource {
	public BlockAttachedEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Override
	public void tick() {
		super.tick();
		// We do not want to update the entity on the server.
		if (this.level().isClientSide()) {
			if (this.isRemoved()) {
				this.setDynamicLightEnabled(false);
			} else {
				if (!SodiumDynamicLights.get().config.getEntitiesLightSource().get() || !DynamicLightHandlers.canLightUp(this))
					this.resetDynamicLight();
				else
					this.dynamicLightTick();
				SodiumDynamicLights.updateTracking(this);
			}
		}
	}
}
