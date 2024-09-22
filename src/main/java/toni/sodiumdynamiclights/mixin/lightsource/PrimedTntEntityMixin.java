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
import toni.sodiumdynamiclights.ExplosiveLightingMode;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.api.DynamicLightHandlers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntEntityMixin extends Entity implements DynamicLightSource {
	@Shadow
	public abstract int getFuse();

	@Unique
	private int startFuseTimer = 80;
	@Unique
	private int sodiumdynamiclights$luminance;

	public PrimedTntEntityMixin(EntityType<?> type, Level level) {
		super(type, level);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
	private void onNew(EntityType<? extends PrimedTnt> type, Level level, CallbackInfo ci) {
		this.startFuseTimer = this.getFuse();
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void onTick(CallbackInfo ci) {
		// We do not want to update the entity on the server.
		if (this.level().isClientSide()) {
			if (!SodiumDynamicLights.get().config.getTntLightingMode().get().isEnabled())
				return;

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

	@Override
	public void sdl$dynamicLightTick() {
		if (this.isOnFire()) {
			this.sodiumdynamiclights$luminance = 15;
		} else {
			ExplosiveLightingMode lightingMode = SodiumDynamicLights.get().config.getTntLightingMode().get();
			if (lightingMode == ExplosiveLightingMode.FANCY) {
				var fuse = this.getFuse() / this.startFuseTimer;
				this.sodiumdynamiclights$luminance = (int) (-(fuse * fuse) * 10.0) + 10;
			} else {
				this.sodiumdynamiclights$luminance = 10;
			}
		}
	}

	@Override
	public int sdl$getLuminance() {
		return this.sodiumdynamiclights$luminance;
	}
}
