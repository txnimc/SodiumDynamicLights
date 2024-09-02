package toni.sodiumdynamiclights.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toni.sodiumdynamiclights.SodiumDynamicLights;

#if AFTER_21_1
import net.minecraft.client.DeltaTracker;
#else
import com.mojang.blaze3d.vertex.PoseStack;
#endif

@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "renderLevel", at = @At("HEAD"))

    #if AFTER_21_1
    private void beforeRender(DeltaTracker tickCounter, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f positionMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
    #else
    private void beforeRender(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
    #endif
        #if FORGELIKE
        Minecraft.getInstance().getProfiler().incrementCounter("dynamic_lighting");
        SodiumDynamicLights.get().updateAll((LevelRenderer) (Object) this);
        #endif
    }
}