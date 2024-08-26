/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a dynamic light source.
 *
 * @author LambdAurora
 * @version 3.0.0
 * @since 1.0.0
 */
public interface DynamicLightSource {
	/**
	 * Returns the dynamic light source X coordinate.
	 *
	 * @return the X coordinate
	 */
	double getDynamicLightX();

	/**
	 * Returns the dynamic light source Y coordinate.
	 *
	 * @return the Y coordinate
	 */
	double getDynamicLightY();

	/**
	 * Returns the dynamic light source Z coordinate.
	 *
	 * @return the Z coordinate
	 */
	double getDynamicLightZ();

	/**
	 * Returns the dynamic light source world.
	 *
	 * @return the world instance
	 */
	Level getDynamicLightLevel();

	/**
	 * Returns whether the dynamic light is enabled or not.
	 *
	 * @return {@code true} if the dynamic light is enabled, else {@code false}
	 */
	default boolean isDynamicLightEnabled() {
		return SodiumDynamicLights.get().config.getDynamicLightsMode().isEnabled() && SodiumDynamicLights.get().containsLightSource(this);
	}

	/**
	 * Sets whether the dynamic light is enabled or not.
	 * <p>
	 * Note: please do not call this function in your mod or you will break things.
	 *
	 * @param enabled {@code true} if the dynamic light is enabled, else {@code false}
	 */
	@ApiStatus.Internal
	default void setDynamicLightEnabled(boolean enabled) {
		this.resetDynamicLight();
		if (enabled)
			SodiumDynamicLights.get().addLightSource(this);
		else
			SodiumDynamicLights.get().removeLightSource(this);
	}

	void resetDynamicLight();

	/**
	 * Returns the luminance of the light source.
	 * The maximum is 15, below 1 values are ignored.
	 *
	 * @return the luminance of the light source
	 */
	int getLuminance();

	/**
	 * Executed at each tick.
	 */
	void dynamicLightTick();

	/**
	 * Returns whether this dynamic light source should update.
	 *
	 * @return {@code true} if this dynamic light source should update, else {@code false}
	 */
	boolean shouldUpdateDynamicLight();

	boolean sodiumdynamiclights$updateDynamicLight(@NotNull LevelRenderer renderer);

	void sodiumdynamiclights$scheduleTrackedChunksRebuild(@NotNull LevelRenderer renderer);
}
