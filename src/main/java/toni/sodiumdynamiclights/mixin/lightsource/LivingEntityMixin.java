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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements DynamicLightSource {
	@Unique
	protected int sodiumdynamiclights$luminance;

	public LivingEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Override
	public void sdl$dynamicLightTick() {
		if (!SodiumDynamicLights.get().config.getEntitiesLightSource().get() || !DynamicLightHandlers.canLightUp(this)) {
			this.sodiumdynamiclights$luminance = 0;
			return;
		}

		if (this.isOnFire() || this.isCurrentlyGlowing()) {
			this.sodiumdynamiclights$luminance = 15;
		} else {
			this.sodiumdynamiclights$luminance = SodiumDynamicLights.getLivingEntityLuminanceFromItems((LivingEntity) (Object) this);
		}

		int luminance = DynamicLightHandlers.getLuminanceFrom(this);
		if (luminance > this.sodiumdynamiclights$luminance)
			this.sodiumdynamiclights$luminance = luminance;
	}

	@Override
	public int sdl$getLuminance() {
		return this.sodiumdynamiclights$luminance;
	}
}
