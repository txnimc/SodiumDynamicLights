///*
// * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
// *
// * This file is part of SodiumDynamicLights.
// *
// * Licensed under the MIT License. For more information,
// * see the LICENSE file.
// */
//
//package toni.sodiumdynamiclights.mixin;
//
//import net.minecraft.network.chat.Component;
//import toni.sodiumdynamiclights.gui.DynamicLightsOptionsOption;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.OptionInstance;
//import net.minecraft.client.Options;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.client.gui.screens.options.OptionsSubScreen;
//import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(VideoSettingsScreen.class)
//public abstract class VideoSettingsScreenMixin extends OptionsSubScreen {
//	@Unique
//	private OptionInstance<?> sodiumdynamiclights$option;
//
//	public VideoSettingsScreenMixin(Screen parent, Options gameOptions, Component title) {
//		super(parent, gameOptions, title);
//	}
//
//	@Inject(method = "<init>", at = @At("TAIL"))
//	private void onConstruct(Screen parent, Minecraft client, Options gameOptions, CallbackInfo ci) {
//		this.sodiumdynamiclights$option = DynamicLightsOptionsOption.getOption(this);
//	}
//
//	@ModifyArg(
//			method = "addOptions",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/client/gui/components/OptionsList;addSmall([Lnet/minecraft/client/OptionInstance;)V"
//			),
//			index = 0
//	)
//	private OptionInstance<?>[] addOptionButton(OptionInstance<?>[] old) {
//		var options = new OptionInstance<?>[old.length + 1];
//		System.arraycopy(old, 0, options, 0, old.length);
//		options[options.length - 1] = this.sodiumdynamiclights$option;
//		return options;
//	}
//
//	@Override
//	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
//		Tooltip.renderAll(graphics);
//	}
//}
