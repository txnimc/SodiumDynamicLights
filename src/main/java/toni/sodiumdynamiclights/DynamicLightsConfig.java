/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import java.util.HashMap;



#if FABRIC
	import net.fabricmc.loader.api.FabricLoader;

	#if AFTER_21_1
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
    import net.neoforged.neoforge.client.gui.ConfigurationScreen;
	import net.neoforged.fml.config.ModConfig;
    import net.neoforged.neoforge.common.ModConfigSpec;
    import net.neoforged.neoforge.common.ModConfigSpec.*;
    #endif

    #if CURRENT_20_1
	import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
	import net.minecraftforge.fml.config.ModConfig;
	import net.minecraftforge.common.ForgeConfigSpec;
	import net.minecraftforge.common.ForgeConfigSpec.*;
    #endif
#endif

#if NEO
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.*;
#endif

#if FORGE
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
#endif

public class DynamicLightsConfig {
	public static final #if BEFORE_21_1 ForgeConfigSpec #else ModConfigSpec #endif SPECS;

	public static final EnumValue<DynamicLightsMode> DYNAMIC_LIGHTS_MODE;

	public static final BooleanValue ENTITIES_LIGHT_SOURCE;
	public static final BooleanValue SELF_LIGHT_SOURCE;
	public static final BooleanValue BLOCK_ENTITIES_LIGHT_SOURCE;
	public static final BooleanValue WATER_SENSITIVE_CHECK;

	public static final EnumValue<ExplosiveLightingMode> CREEPER_LIGHTING_MODE;
	public static final EnumValue<ExplosiveLightingMode> TNT_LIGHTING_MODE;

	public static final HashMap<String, Boolean> ENTITIES_SETTINGS;


	static {
		var BUILDER = new Builder();
		BUILDER.push("sodiumdynamiclights");

		DYNAMIC_LIGHTS_MODE = BUILDER
				.comment("Lighting mode")
				.defineEnum("mode", DynamicLightsMode.REALTIME);

		ENTITIES_LIGHT_SOURCE = BUILDER
				.comment("Enable entities light source.")
				.define("entities", true);

		SELF_LIGHT_SOURCE = BUILDER
				.comment("Enable first-person player light source.")
				.define("self", true);

		BLOCK_ENTITIES_LIGHT_SOURCE = BUILDER
				.comment("Enable block entities light source.")
				.define("block_entities", true);

		WATER_SENSITIVE_CHECK = BUILDER
				.comment("Enables water-sensitive light sources check. This means that water-sensitive items will not light up when submerged in water.")
				.define("water_sensitive_check", true);

		TNT_LIGHTING_MODE = BUILDER
				.comment("TNT lighting mode. May be off, simple or fancy.")
				.defineEnum("tnt", ExplosiveLightingMode.SIMPLE);

		CREEPER_LIGHTING_MODE = BUILDER
				.comment("Creeper lighting mode. May be off, simple or fancy.")
				.defineEnum("creeper", ExplosiveLightingMode.OFF);

		ENTITIES_SETTINGS = new HashMap<>();

		BUILDER.pop();
		SPECS = BUILDER.build();
	}

	/**
	 * Returns the dynamic lights mode.
	 *
	 * @return the dynamic lights mode
	 */
	public DynamicLightsMode getDynamicLightsMode() {
		return DYNAMIC_LIGHTS_MODE.get();
	}

	/**
	 * {@return the entities as light source setting holder}
	 */
	public BooleanValue getEntitiesLightSource() {
		return ENTITIES_LIGHT_SOURCE;
	}

	/**
	 * {@return the first-person player as light source setting holder}
	 */
	public BooleanValue getSelfLightSource() {
		return SELF_LIGHT_SOURCE;
	}

	/**
	 * {@return the block entities as light source setting holder}
	 */
	public BooleanValue getBlockEntitiesLightSource() {
		return BLOCK_ENTITIES_LIGHT_SOURCE;
	}

	/**
	 * {@return the water sensitive check setting holder}
	 */
	public BooleanValue getWaterSensitiveCheck() {
		return WATER_SENSITIVE_CHECK;
	}

	/**
	 * Returns the Creeper dynamic lighting mode.
	 *
	 * @return the Creeper dynamic lighting mode
	 */
	public EnumValue<ExplosiveLightingMode> getCreeperLightingMode() {
		return CREEPER_LIGHTING_MODE;
	}

	/**
	 * Returns the TNT dynamic lighting mode.
	 *
	 * @return the TNT dynamic lighting mode
	 */
	public EnumValue<ExplosiveLightingMode> getTntLightingMode() {
		return TNT_LIGHTING_MODE;
	}

	public HashMap<String, Boolean> getLightSettings() { return ENTITIES_SETTINGS; }
}
