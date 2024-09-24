/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.accessor;

import net.minecraft.network.chat.Component;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
@ApiStatus.NonExtendable
public interface DynamicLightHandlerHolder<T> {
	@Nullable DynamicLightHandler<T> sodiumdynamiclights$getDynamicLightHandler();

	void sodiumdynamiclights$setDynamicLightHandler(DynamicLightHandler<T> handler);

	boolean sodiumdynamiclights$getSetting();

	Component sodiumdynamiclights$getName();

	@SuppressWarnings("unchecked")
	static <T extends Entity> DynamicLightHandlerHolder<T> cast(EntityType<T> entityType) {
		return (DynamicLightHandlerHolder<T>) entityType;
	}

	@SuppressWarnings("unchecked")
	static <T extends BlockEntity> DynamicLightHandlerHolder<T> cast(BlockEntityType<T> entityType) {
		return (DynamicLightHandlerHolder<T>) entityType;
	}
}
