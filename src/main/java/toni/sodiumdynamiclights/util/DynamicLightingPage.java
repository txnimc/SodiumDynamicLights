package toni.sodiumdynamiclights.util;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.options.OptionGroup;
import net.caffeinemc.mods.sodium.client.gui.options.OptionImpl;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.CyclingControl;
import net.caffeinemc.mods.sodium.client.gui.options.control.TickBoxControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.SodiumOptionsStorage;
import net.minecraft.network.chat.Component;
import toni.sodiumdynamiclights.DynamicLightsConfig;
import toni.sodiumdynamiclights.DynamicLightsMode;
import toni.sodiumdynamiclights.ExplosiveLightingMode;

import java.util.ArrayList;
import java.util.List;

public class DynamicLightingPage extends OptionPage {
    private static final SodiumOptionsStorage mixinsOptionsStorage = new SodiumOptionsStorage();

    public DynamicLightingPage() {
        super(Component.translatable("sodium.dynamiclights.options.page"), create());
    }

    private static ImmutableList<OptionGroup> create() {
        final List<OptionGroup> groups = new ArrayList<>();
        var builder = OptionGroup.createBuilder();

        builder.add(OptionImpl.createBuilder(DynamicLightsMode.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.mode"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.mode.desc"))
                .setControl(option -> new CyclingControl<>(option, DynamicLightsMode.class, new Component[] {
                        DynamicLightsMode.OFF.getTranslatedText(),
                        DynamicLightsMode.FAST.getTranslatedText(),
                        DynamicLightsMode.SLOW.getTranslatedText(),
                        DynamicLightsMode.REALTIME.getTranslatedText()
                }))
                .setBinding((options, value) -> DynamicLightsConfig.DYNAMIC_LIGHTS_MODE.set(value),
                            (options) -> DynamicLightsConfig.DYNAMIC_LIGHTS_MODE.get())
                .build());

        groups.add(builder.build());
        builder = OptionGroup.createBuilder();

        builder.add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.self"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.self.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> DynamicLightsConfig.SELF_LIGHT_SOURCE.set(value),
                            (options) -> DynamicLightsConfig.SELF_LIGHT_SOURCE.get())
                .build());

        builder.add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.entities"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.entities.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> DynamicLightsConfig.ENTITIES_LIGHT_SOURCE.set(value),
                            (options) -> DynamicLightsConfig.ENTITIES_LIGHT_SOURCE.get())
                .build());

        builder.add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.blockentities"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.blockentities.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> DynamicLightsConfig.BLOCK_ENTITIES_LIGHT_SOURCE.set(value),
                            (options) -> DynamicLightsConfig.BLOCK_ENTITIES_LIGHT_SOURCE.get())
                .build());

        builder.add(OptionImpl.createBuilder(boolean.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.underwater"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.underwater.desc"))
                .setControl(TickBoxControl::new)
                .setBinding((options, value) -> DynamicLightsConfig.WATER_SENSITIVE_CHECK.set(value),
                            (options) -> DynamicLightsConfig.WATER_SENSITIVE_CHECK.get())
                .build());

        groups.add(builder.build());
        builder = OptionGroup.createBuilder();

        builder.add(OptionImpl.createBuilder(ExplosiveLightingMode.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.tnt"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.tnt.desc"))
                .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class, new Component[] {
                        ExplosiveLightingMode.OFF.getTranslatedText(),
                        ExplosiveLightingMode.SIMPLE.getTranslatedText(),
                        ExplosiveLightingMode.FANCY.getTranslatedText(),
                }))
                .setBinding((options, value) -> DynamicLightsConfig.TNT_LIGHTING_MODE.set(value),
                            (options) -> DynamicLightsConfig.TNT_LIGHTING_MODE.get())
                .build());

        builder.add(OptionImpl.createBuilder(ExplosiveLightingMode.class, mixinsOptionsStorage)
                .setName(Component.translatable("sodium.dynamiclights.options.creeper"))
                .setTooltip(Component.translatable("sodium.dynamiclights.options.creeper.desc"))
                .setControl(option -> new CyclingControl<>(option, ExplosiveLightingMode.class, new Component[] {
                        ExplosiveLightingMode.OFF.getTranslatedText(),
                        ExplosiveLightingMode.SIMPLE.getTranslatedText(),
                        ExplosiveLightingMode.FANCY.getTranslatedText(),
                }))
                .setBinding((options, value) -> DynamicLightsConfig.CREEPER_LIGHTING_MODE.set(value),
                            (options) -> DynamicLightsConfig.CREEPER_LIGHTING_MODE.get())
                .build());

        groups.add(builder.build());
        return ImmutableList.copyOf(groups);
    }
}