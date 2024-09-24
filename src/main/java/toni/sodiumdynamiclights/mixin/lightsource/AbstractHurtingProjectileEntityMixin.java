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
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileEntityMixin extends Entity implements DynamicLightSource {
	public AbstractHurtingProjectileEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Override
	public void sdl$dynamicLightTick() {
		if (!this.sdl$isDynamicLightEnabled())
			this.sdl$setDynamicLightEnabled(true);
	}

	@Override
	public int sdl$getLuminance() {
		if (SodiumDynamicLights.get().config.getEntitiesLightSource().get() && DynamicLightHandlers.canLightUp(this))
			return 14;
		return 0;
	}
}
