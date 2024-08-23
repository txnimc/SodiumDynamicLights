package toni.sodiumleafculling;

import net.caffeinemc.mods.sodium.client.gui.options.TextProvider;
import net.minecraft.network.chat.Component;

public enum LeafCullingQuality implements TextProvider {
    NONE("options.leaf_culling.none"),
    HOLLOW("options.leaf_culling.hollow"),
    SOLID("options.leaf_culling.solid"),
    SOLID_AGGRESSIVE("options.leaf_culling.solid_aggressive");

    private final Component name;

    LeafCullingQuality(String name) {
        this.name = Component.translatable(name);
    }

    @Override
    public Component getLocalizedName() {
        return this.name;
    }

    public boolean isSolid() {
        return this == SOLID || this == SOLID_AGGRESSIVE;
    }
}