/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.api;

/**
 * Represents the entrypoint for SodiumDynamicLights API.
 *
 * @author LambdAurora
 * @version 3.0.0
 * @since 1.3.2
 */
public interface DynamicLightsInitializer {
	/**
	 * Called when SodiumDynamicLights is initialized to register custom dynamic light handlers and item light sources.
	 */
	void onInitializeDynamicLights();
}
