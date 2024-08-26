/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the dynamic lights mode.
 *
 * @author LambdAurora
 * @version 2.0.1
 * @since 1.0.0
 */
public enum DynamicLightsMode {
	OFF(0, ChatFormatting.RED, "OFF"),
	SLOW(500, ChatFormatting.YELLOW, "SLOW"),
	FAST(250, ChatFormatting.GOLD, "FAST"),
	REALTIME(0, ChatFormatting.GREEN, "REALTIME");

	private final int delay;
	private final Component translatedText;

	DynamicLightsMode(int delay, @NotNull ChatFormatting formatting, @NotNull String translatedText) {
		this.delay = delay;
		this.translatedText = Component.literal(translatedText).copy().withStyle(formatting);
	}

	/**
	 * Returns whether this mode enables dynamic lights.
	 *
	 * @return {@code true} if the mode enables dynamic lights, else {@code false}
	 */
	public boolean isEnabled() {
		return this != OFF;
	}

	/**
	 * Returns whether this mode has an update delay.
	 *
	 * @return {@code true} if the mode has an update delay, else {@code false}
	 */
	public boolean hasDelay() {
		return this.delay != 0;
	}

	/**
	 * Returns the update delay of this mode.
	 *
	 * @return the mode's update delay
	 */
	public int getDelay() {
		return this.delay;
	}

	/**
	 * Returns the next dynamic lights mode available.
	 *
	 * @return the next available dynamic lights mode
	 */
	public DynamicLightsMode next() {
		DynamicLightsMode[] v = values();
		if (v.length == this.ordinal() + 1)
			return v[0];
		return v[this.ordinal() + 1];
	}

	/**
	 * Returns the translated text of the dynamic lights mode.
	 *
	 * @return the translated text of the dynamic lights mode
	 */
	public @NotNull Component getTranslatedText() {
		return this.translatedText;
	}

	/**
	 * Gets the dynamic lights mode from its ResourceLocation.
	 *
	 * @param id the ResourceLocation of the dynamic lights mode
	 * @return the dynamic lights mode if found, else empty
	 */
	public static @NotNull Optional<DynamicLightsMode> byId(@NotNull String id) {
		return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
	}

	public @NotNull String getName() {
		return this.name().toLowerCase();
	}
}
