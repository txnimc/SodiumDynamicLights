/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin.lightsource;

import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.api.DynamicLightHandlers;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin implements DynamicLightSource {
	@Shadow
	public Level level;

	@Shadow
	public abstract double getX();

	@Shadow
	public abstract double getEyeY();

	@Shadow
	public abstract double getZ();

	@Shadow
	public abstract double getY();

	@Shadow
	public abstract boolean isOnFire();

	@Shadow
	public abstract EntityType<?> getType();

	@Shadow
	public abstract BlockPos getOnPos();

	@Shadow
	public abstract boolean isRemoved();

	@Shadow
	public abstract ChunkPos chunkPosition();

	@Unique
	protected int sodiumdynamiclights$luminance = 0;
	@Unique
	private int sodiumdynamiclights$lastLuminance = 0;
	@Unique
	private long sodiumdynamiclights$lastUpdate = 0;
	@Unique
	private double sodiumdynamiclights$prevX;
	@Unique
	private double sodiumdynamiclights$prevY;
	@Unique
	private double sodiumdynamiclights$prevZ;
	@Unique
	private LongOpenHashSet sodiumdynamiclights$trackedLitChunkPos = new LongOpenHashSet();

	@Inject(method = "tick", at = @At("TAIL"))
	public void onTick(CallbackInfo ci) {
		// We do not want to update the entity on the server.
		if (this.level.isClientSide()) {
			if (this.isRemoved()) {
				this.sdl$setDynamicLightEnabled(false);
			} else {
				this.sdl$dynamicLightTick();
				if ((!SodiumDynamicLights.get().config.getEntitiesLightSource().get() && this.getType() != EntityType.PLAYER)
						|| !DynamicLightHandlers.canLightUp((Entity) (Object) this))
					this.sodiumdynamiclights$luminance = 0;
				SodiumDynamicLights.updateTracking(this);
			}
		}
	}

	@Inject(method = "remove", at = @At("TAIL"))
	public void onRemove(CallbackInfo ci) {
		if (this.level.isClientSide())
			this.sdl$setDynamicLightEnabled(false);
	}

	@Override
	public double sdl$getDynamicLightX() {
		return this.getX();
	}

	@Override
	public double sdl$getDynamicLightY() {
		return this.getEyeY();
	}

	@Override
	public double sdl$getDynamicLightZ() {
		return this.getZ();
	}

	@Override
	public Level sdl$getDynamicLightLevel() {
		return this.level;
	}

	@Override
	public void sdl$resetDynamicLight() {
		this.sodiumdynamiclights$lastLuminance = 0;
	}

	@Override
	public boolean sdl$shouldUpdateDynamicLight() {
		var mode = SodiumDynamicLights.get().config.getDynamicLightsMode();
		if (!mode.isEnabled())
			return false;
		if (mode.hasDelay()) {
			long currentTime = System.currentTimeMillis();
			if (currentTime < this.sodiumdynamiclights$lastUpdate + mode.getDelay()) {
				return false;
			}

			this.sodiumdynamiclights$lastUpdate = currentTime;
		}
		return true;
	}

	@Override
	public void sdl$dynamicLightTick() {
		this.sodiumdynamiclights$luminance = this.isOnFire() ? 15 : 0;

		int luminance = DynamicLightHandlers.getLuminanceFrom((Entity) (Object) this);
		if (luminance > this.sodiumdynamiclights$luminance)
			this.sodiumdynamiclights$luminance = luminance;
	}

	@Override
	public int sdl$getLuminance() {
		return this.sodiumdynamiclights$luminance;
	}

	@Override
	public boolean sodiumdynamiclights$updateDynamicLight(@NotNull LevelRenderer renderer) {
		if (!this.sdl$shouldUpdateDynamicLight())
			return false;
		double deltaX = this.getX() - this.sodiumdynamiclights$prevX;
		double deltaY = this.getY() - this.sodiumdynamiclights$prevY;
		double deltaZ = this.getZ() - this.sodiumdynamiclights$prevZ;

		int luminance = this.sdl$getLuminance();

		if (Math.abs(deltaX) > 0.1D || Math.abs(deltaY) > 0.1D || Math.abs(deltaZ) > 0.1D || luminance != this.sodiumdynamiclights$lastLuminance) {
			this.sodiumdynamiclights$prevX = this.getX();
			this.sodiumdynamiclights$prevY = this.getY();
			this.sodiumdynamiclights$prevZ = this.getZ();
			this.sodiumdynamiclights$lastLuminance = luminance;

			var newPos = new LongOpenHashSet();

			if (luminance > 0) {
				var entityChunkPos = this.chunkPosition();
				var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, SectionPos.blockToSectionCoord(this.getEyeY()), entityChunkPos.z);

				SodiumDynamicLights.scheduleChunkRebuild(renderer, chunkPos);
				SodiumDynamicLights.updateTrackedChunks(chunkPos, this.sodiumdynamiclights$trackedLitChunkPos, newPos);

				var directionX = (this.getOnPos().getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
				var directionY = ((int) Mth.floor(this.getEyeY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
				var directionZ = (this.getOnPos().getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

				for (int i = 0; i < 7; i++) {
					if (i % 4 == 0) {
						chunkPos.move(directionX); // X
					} else if (i % 4 == 1) {
						chunkPos.move(directionZ); // XZ
					} else if (i % 4 == 2) {
						chunkPos.move(directionX.getOpposite()); // Z
					} else {
						chunkPos.move(directionZ.getOpposite()); // origin
						chunkPos.move(directionY); // Y
					}
					SodiumDynamicLights.scheduleChunkRebuild(renderer, chunkPos);
					SodiumDynamicLights.updateTrackedChunks(chunkPos, this.sodiumdynamiclights$trackedLitChunkPos, newPos);
				}
			}

			// Schedules the rebuild of removed chunks.
			this.sodiumdynamiclights$scheduleTrackedChunksRebuild(renderer);
			// Update tracked lit chunks.
			this.sodiumdynamiclights$trackedLitChunkPos = newPos;
			return true;
		}
		return false;
	}

	@Override
	public void sodiumdynamiclights$scheduleTrackedChunksRebuild(@NotNull LevelRenderer renderer) {
		if (Minecraft.getInstance().level == this.level)
			for (long pos : this.sodiumdynamiclights$trackedLitChunkPos) {
				SodiumDynamicLights.scheduleChunkRebuild(renderer, pos);
			}
	}
}
