package toni.sodiumleafculling.mixins;

import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.minecraft.world.level.block.LeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import toni.sodiumleafculling.LeafCulling;
import toni.sodiumleafculling.PerformanceSettingsAccessor;

@Mixin(value = BlockRenderer.class, remap = false, priority = 100)
public class BlockRendererMixin {
    @ModifyVariable(method = "processQuad", at = @At("STORE"))
    private BlendMode inject$processQuad(BlendMode blendMode, MutableQuadViewImpl quad) {
        var ctx = (AbstractBlockRenderContextAccessor) this;
        if (!(ctx.getState().getBlock() instanceof LeavesBlock))
            return blendMode;

        var quality = ((PerformanceSettingsAccessor) SodiumClientMod.options().performance).sodiumleafculling$getQuality();
        if (quality.isSolid() && LeafCulling.surroundedByLeaves(ctx.getSlice(), ctx.getPos()))
        {
            return BlendMode.SOLID;
        }

        return blendMode;
    }
}
