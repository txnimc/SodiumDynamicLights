/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin.lightsource;

import net.minecraft.util.Mth;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.api.DynamicLightHandlers;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin implements DynamicLightSource {
	@Final
	@Shadow
	protected BlockPos worldPosition;

	@Shadow
	@Nullable
	protected Level level;

	@Shadow
	protected boolean remove;

	@Unique
	private int luminance = 0;
	@Unique
	private int lastLuminance = 0;
	@Unique
	private long lastUpdate = 0;
	@Unique
	private final LongOpenHashSet sodiumdynamiclights$trackedLitChunkPos = new LongOpenHashSet();

	@Override
	public double getDynamicLightX() {
		return this.worldPosition.getX() + 0.5;
	}

	@Override
	public double getDynamicLightY() {
		return this.worldPosition.getY() + 0.5;
	}

	@Override
	public double getDynamicLightZ() {
		return this.worldPosition.getZ() + 0.5;
	}

	@Override
	public Level getDynamicLightLevel() {
		return this.level;
	}

	@Inject(method = "setRemoved", at = @At("TAIL"))
	private void onRemoved(CallbackInfo ci) {
		this.setDynamicLightEnabled(false);
	}

	@Override
	public void resetDynamicLight() {
		this.lastLuminance = 0;
	}

	@Override
	public void dynamicLightTick() {
		// We do not want to update the entity on the server.
		if (this.level == null || !this.level.isClientSide())
			return;
		if (!this.remove) {
			this.luminance = DynamicLightHandlers.getLuminanceFrom((BlockEntity) (Object) this);
			SodiumDynamicLights.updateTracking(this);

			if (!this.isDynamicLightEnabled()) {
				this.lastLuminance = 0;
			}
		}
	}

	@Override
	public int getLuminance() {
		return this.luminance;
	}

	@Override
	public boolean shouldUpdateDynamicLight() {
		var mode = SodiumDynamicLights.get().config.getDynamicLightsMode();
		if (!mode.isEnabled())
			return false;
		if (mode.hasDelay()) {
			long currentTime = System.currentTimeMillis();
			if (currentTime < this.lastUpdate + mode.getDelay()) {
				return false;
			}

			this.lastUpdate = currentTime;
		}
		return true;
	}

	@Override
	public boolean sodiumdynamiclights$updateDynamicLight(@NotNull LevelRenderer renderer) {
		if (!this.shouldUpdateDynamicLight())
			return false;

		int luminance = this.getLuminance();

		if (luminance != this.lastLuminance) {
			this.lastLuminance = luminance;

			if (this.sodiumdynamiclights$trackedLitChunkPos.isEmpty()) {
				var chunkPos = new BlockPos.MutableBlockPos(Math.floorDiv(this.worldPosition.getX(), 16),
						Mth.floorDiv(this.worldPosition.getY(), 16),
						Mth.floorDiv(this.worldPosition.getZ(), 16));

				SodiumDynamicLights.updateTrackedChunks(chunkPos, null, this.sodiumdynamiclights$trackedLitChunkPos);

				var directionX = (this.worldPosition.getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
				var directionY = (this.worldPosition.getY() & 15) >= 8 ? Direction.UP : Direction.DOWN;
				var directionZ = (this.worldPosition.getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

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
					SodiumDynamicLights.updateTrackedChunks(chunkPos, null, this.sodiumdynamiclights$trackedLitChunkPos);
				}
			}

			// Schedules the rebuild of chunks.
			this.sodiumdynamiclights$scheduleTrackedChunksRebuild(renderer);
			return true;
		}
		return false;
	}

	@Override
	public void sodiumdynamiclights$scheduleTrackedChunksRebuild(@NotNull LevelRenderer renderer) {
		if (this.level == Minecraft.getInstance().level)
			for (long pos : this.sodiumdynamiclights$trackedLitChunkPos) {
				SodiumDynamicLights.scheduleChunkRebuild(renderer, pos);
			}
	}
}
