package toni.sodiumleafculling.mixins;

import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import net.caffeinemc.mods.sodium.client.gui.options.*;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import toni.sodiumleafculling.LeafCullingQuality;
import toni.sodiumleafculling.PerformanceSettingsAccessor;

import java.util.List;

@Mixin(value = SodiumGameOptionPages.class, remap = false, priority = 100)
public class SodiumGameOptionPagesMixin {
    @Unique
    private static final SodiumOptionsStorage leafcullingOpts = new SodiumOptionsStorage();

    @Inject(method = "performance", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/PlatformRuntimeInformation;isDevelopmentEnvironment()Z"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void inject$leafcullingoption(CallbackInfoReturnable<OptionPage> cir, List<OptionGroup> groups) {
        groups.add(OptionGroup.createBuilder()
            .add(OptionImpl.createBuilder(LeafCullingQuality.class, leafcullingOpts)
                .setName(Component.translatable("sodiumleafculling.options.leaf_culling.name"))
                .setTooltip(Component.translatable("sodiumleafculling.options.leaf_culling.tooltip"))
                .setControl(option -> new CyclingControl<>(option, LeafCullingQuality.class))
                .setBinding(
                    (opts, value) -> ((PerformanceSettingsAccessor) opts.performance).sodiumleafculling$setQuality(value),
                    opts -> ((PerformanceSettingsAccessor) opts.performance).sodiumleafculling$getQuality())
                .setImpact(OptionImpact.MEDIUM)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build()
            ).build()
        );
    }
}
