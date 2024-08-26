/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.mixin;

import toni.sodiumdynamiclights.SodiumDynamicLights;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Adds a debug string for dynamic light sources tracking and updates.
 *
 * @author LambdAurora
 * @version 1.3.2
 * @since 1.3.2
 */
@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
	@Inject(method = "getGameInformation", at = @At("RETURN"))
	private void onGetLeftText(CallbackInfoReturnable<List<String>> cir) {
		var list = cir.getReturnValue();
		var ldl = SodiumDynamicLights.get();
		var builder = new StringBuilder("Dynamic Light Sources: ");
		builder.append(ldl.getLightSourcesCount())
				.append(" (U: ")
				.append(ldl.getLastUpdateCount());

		if (!ldl.config.getDynamicLightsMode().isEnabled()) {
			builder.append(" ; ");
			builder.append(ChatFormatting.RED);
			builder.append("Disabled");
			builder.append(ChatFormatting.RESET);
		}

		builder.append(')');
		list.add(builder.toString());
	}
}
