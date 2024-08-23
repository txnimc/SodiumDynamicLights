package toni.sodiumleafculling.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.SodiumClientMod;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import toni.sodiumleafculling.LeafCulling;
import toni.sodiumleafculling.LeafCullingQuality;
import toni.sodiumleafculling.PerformanceSettingsAccessor;

@Mixin(value = BlockOcclusionCache.class, priority = 100)
public class BlockOcclusionCacheMixin {

    @Inject(method = "shouldDrawSide", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void inject$shouldDrawSide(BlockState selfState, BlockGetter view, BlockPos selfPos, Direction facing, CallbackInfoReturnable<Boolean> cir, BlockPos.MutableBlockPos otherPos, BlockState otherState) {
         if (selfState.getBlock() instanceof LeavesBlock) {
             var leafculling = ((PerformanceSettingsAccessor) SodiumClientMod.options().performance);
             if (leafculling.sodiumleafculling$getQuality() == LeafCullingQuality.HOLLOW) {
                 var skipRendering = LeafCulling.shouldCullSide(view, selfPos, facing, 2);
                 if (skipRendering) {
                     cir.setReturnValue(false);
                     return;
                 }
             }

             if (otherState.getBlock() instanceof LeavesBlock && leafculling.sodiumleafculling$getQuality().isSolid()) {
                 var cullSelf = LeafCulling.surroundedByLeaves(view, selfPos);
                 var cullOther = LeafCulling.surroundedByLeaves(view, otherPos);

                 if (!cullSelf && cullOther) {
                     cir.setReturnValue(false);
                     return;
                 }

                 if (cullSelf && cullOther) {
                     cir.setReturnValue(false);
                     return;
                 }
             }
         }
    }
}
