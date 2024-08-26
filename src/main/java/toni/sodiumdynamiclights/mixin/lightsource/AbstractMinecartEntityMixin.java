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
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds the tick method for dynamic light source tracking in minecart entities.
 *
 * @author LambdAurora
 * @version 2.0.2
 * @since 1.3.2
 */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements DynamicLightSource {
	@Shadow
	public abstract BlockState getDisplayBlockState();

	@Unique
	private int sodiumdynamiclights$luminance;

	public AbstractMinecartEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo ci) {
		// We do not want to update the entity on the server.
		if (this.level().isClientSide()) {
			if (this.isRemoved()) {
				this.setDynamicLightEnabled(false);
			} else {
				if (!SodiumDynamicLights.get().config.getEntitiesLightSource().get() || !DynamicLightHandlers.canLightUp(this))
					this.sodiumdynamiclights$luminance = 0;
				else
					this.dynamicLightTick();
				SodiumDynamicLights.updateTracking(this);
			}
		}
	}

	@Override
	public void dynamicLightTick() {
		this.sodiumdynamiclights$luminance = Math.max(
				Math.max(
						this.isOnFire() ? 15 : 0,
						this.getDisplayBlockState().getLightEmission()
				),
				DynamicLightHandlers.getLuminanceFrom(this)
		);
	}

	@Override
	public int getLuminance() {
		return this.sodiumdynamiclights$luminance;
	}
}
