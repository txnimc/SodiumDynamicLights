/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin;

import net.minecraft.network.chat.Component;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import toni.sodiumdynamiclights.accessor.DynamicLightHandlerHolder;
import toni.sodiumdynamiclights.api.DynamicLightHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> implements DynamicLightHandlerHolder<T> {
	@Shadow
	public abstract Component getDescription();

	@Unique
	private DynamicLightHandler<T> sodiumdynamiclights$lightHandler;
	@Unique
	private Boolean sodiumdynamiclights$setting;

	@Override
	public @Nullable DynamicLightHandler<T> sodiumdynamiclights$getDynamicLightHandler() {
		return this.sodiumdynamiclights$lightHandler;
	}

	@Override
	public void sodiumdynamiclights$setDynamicLightHandler(DynamicLightHandler<T> handler) {
		this.sodiumdynamiclights$lightHandler = handler;
	}

	@Override
	public boolean sodiumdynamiclights$getSetting() {
		if (this.sodiumdynamiclights$setting == null) {
			var self = (EntityType<?>) (Object) this;
			var id = BuiltInRegistries.ENTITY_TYPE.getKey(self);
			if (id.getNamespace().equals("minecraft") && id.getPath().equals("pig") && self != EntityType.PIG) {
				return false;
			}

			var path = "light_sources.settings.entities." + id.getNamespace() + '.' + id.getPath().replace('/', '.');

			var config = SodiumDynamicLights.get().config.getLightSettings();
			if (!config.containsKey(path)) {
				this.sodiumdynamiclights$setting = false;
				return false;
			}

			this.sodiumdynamiclights$setting = config.getOrDefault(path, false);
		}

		return this.sodiumdynamiclights$setting;
	}

	@Override
	public Component sodiumdynamiclights$getName() {
		var name = this.getDescription();
		if (name == null) {
			return Component.translatable("sodiumdynamiclights.dummy");
		}
		return name;
	}
}
