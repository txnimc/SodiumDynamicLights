/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import toni.sodiumdynamiclights.DynamicLightSource;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Shadow
	public abstract boolean isClientSide();

	@Shadow
	public abstract @Nullable BlockEntity getBlockEntity(BlockPos pos);

	@Inject(
			method = "tickBlockEntities",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/TickingBlockEntity;tick()V",
					shift = At.Shift.BEFORE
			)
	)
	private void onBlockEntityTick(CallbackInfo ci, @Local boolean isRemoved, @Local TickingBlockEntity blockEntityTickInvoker) {
		if (this.isClientSide() && SodiumDynamicLights.get().config.getBlockEntitiesLightSource().get() && !isRemoved) {
			var blockEntity = this.getBlockEntity(blockEntityTickInvoker.getPos());
			if (blockEntity != null)
				((DynamicLightSource) blockEntity).dynamicLightTick();
		}
	}
}
