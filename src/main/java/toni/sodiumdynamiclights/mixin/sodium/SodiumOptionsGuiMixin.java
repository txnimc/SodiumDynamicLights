package toni.sodiumdynamiclights.mixin.sodium;


import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.sodiumdynamiclights.util.DynamicLightingPage;

import java.util.List;

@Mixin(value = SodiumOptionsGUI.class, remap = false, priority = 100/* Prevents other forks of sodium extra stay above emb++*/)
public class SodiumOptionsGuiMixin {
    @Shadow
    @Final
    private List<OptionPage> pages;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$dynLightsPage(Screen prevScreen, CallbackInfo ci) {
        pages.add(new DynamicLightingPage());
    }
}