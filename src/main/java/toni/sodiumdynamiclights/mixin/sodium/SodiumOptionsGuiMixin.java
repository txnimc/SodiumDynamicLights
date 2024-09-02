package toni.sodiumdynamiclights.mixin.sodium;


#if FORGE
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import org.embeddedt.embeddium.gui.EmbeddiumVideoOptionsScreen;
#else
    #if AFTER_21_1
    import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
    import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
    #else
    import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
    import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
    #endif
#endif

import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.sodiumdynamiclights.util.DynamicLightingPage;

import java.util.List;


@Mixin(value = #if FORGE EmbeddiumVideoOptionsScreen.class #else SodiumOptionsGUI.class #endif, remap = false, priority = 100/* Prevents other forks of sodium extra stay above emb++*/)
public class SodiumOptionsGuiMixin {
    @Shadow
    @Final
    private List<OptionPage> pages;

    #if FORGE

    #else
    @Inject(method = "<init>", at = @At("RETURN"))
    private void inject$dynLightsPage(Screen prevScreen, CallbackInfo ci) {
        pages.add(new DynamicLightingPage());
    }
    #endif
}