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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements DynamicLightSource {
	@Shadow
	public abstract boolean isSpectator();

	@Unique
	protected int sodiumdynamiclights$luminance;
	@Unique
	private Level sodiumdynamiclights$lastWorld;

	protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Override
	public void sdl$dynamicLightTick() {
		if (!DynamicLightHandlers.canLightUp(this)) {
			this.sodiumdynamiclights$luminance = 0;
			return;
		}

		if (this.isOnFire() || this.isCurrentlyGlowing()) {
			this.sodiumdynamiclights$luminance = 15;
		} else {
			this.sodiumdynamiclights$luminance = Math.max(
					DynamicLightHandlers.getLuminanceFrom(this),
					SodiumDynamicLights.getLivingEntityLuminanceFromItems(this)
			);
		}

		if (this.isSpectator())
			this.sodiumdynamiclights$luminance = 0;

		if (this.sodiumdynamiclights$lastWorld != this.level()) {
			this.sodiumdynamiclights$lastWorld = this.level();
			this.sodiumdynamiclights$luminance = 0;
		}
	}

	@Override
	public int sdl$getLuminance() {
		return this.sodiumdynamiclights$luminance;
	}
}
