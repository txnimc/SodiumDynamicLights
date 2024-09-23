/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import toni.sodiumdynamiclights.accessor.WorldRendererAccessor;
import toni.sodiumdynamiclights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import toni.sodiumdynamiclights.api.item.ItemLightSources;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import net.minecraft.server.packs.PackType;

#if AFTER_21_1
import net.neoforged.fml.config.ModConfig;
#endif

#if FABRIC
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

	#if AFTER_21_1
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
    import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
    import net.neoforged.neoforge.client.gui.ConfigurationScreen;
	import net.neoforged.fml.config.ModConfig;
    import net.neoforged.neoforge.common.ModConfigSpec;
    import net.neoforged.neoforge.common.ModConfigSpec.*;
    #endif

    #if CURRENT_20_1
    import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
	import net.minecraftforge.fml.config.ModConfig;
	import net.minecraftforge.common.ForgeConfigSpec;
	import net.minecraftforge.common.ForgeConfigSpec.*;
    #endif

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
#endif

#if NEO
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
#endif

#if FORGE
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.ForgeConfig;
#endif

/**
 *
 * Represents the SodiumDynamicLights mod.
 *
 * @author LambdAurora
 * @version 2.3.2
 * @since 1.0.0
 */

#if FORGELIKE
@Mod("sodiumdynamiclights")
#endif
public class SodiumDynamicLights #if FABRIC implements ClientModInitializer #endif {
	public static final String NAMESPACE = "sodiumdynamiclights";
	private static final double MAX_RADIUS = 7.75;
	private static final double MAX_RADIUS_SQUARED = MAX_RADIUS * MAX_RADIUS;
	private static SodiumDynamicLights INSTANCE;
	public final Logger logger = LoggerFactory.getLogger(NAMESPACE);
	public final DynamicLightsConfig config = new DynamicLightsConfig();
	private final Set<DynamicLightSource> dynamicLightSources = new HashSet<>();
	private final ReentrantReadWriteLock lightSourcesLock = new ReentrantReadWriteLock();
	private long lastUpdate = System.currentTimeMillis();
	private int lastUpdateCount = 0;

	public SodiumDynamicLights(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) {
		#if NEO
		modEventBus.addListener(this::clientSetup);
		modContainer.registerConfig(ModConfig.Type.CLIENT, DynamicLightsConfig.SPECS);
		#endif

		#if FORGE
		var context = FMLJavaModLoadingContext.get();
		var modEventBus = context.getModEventBus();
		modEventBus.addListener(this::clientSetup);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DynamicLightsConfig.SPECS);
        #endif
	}

	#if FABRIC @Override #endif
	public void onInitializeClient() {
		INSTANCE = this;
		this.log("Initializing SodiumDynamicLights...");

		#if FABRIC
			ClientLifecycleEvents.CLIENT_STOPPING.register((mc) -> {
				DynamicLightsConfig.SPECS.save();
			});

			#if AFTER_21_1
			NeoForgeConfigRegistry.INSTANCE.register(SodiumDynamicLights.NAMESPACE, ModConfig.Type.CLIENT, DynamicLightsConfig.SPECS);
			#else
			ForgeConfigRegistry.INSTANCE.register(SodiumDynamicLights.NAMESPACE, ModConfig.Type.CLIENT, DynamicLightsConfig.SPECS);
			#endif

			FabricLoader.getInstance().getEntrypointContainers("dynamiclights", DynamicLightsInitializer.class)
					.stream().map(EntrypointContainer::getEntrypoint)
					.forEach(DynamicLightsInitializer::onInitializeDynamicLights);

			ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
				@Override
				public ResourceLocation getFabricId() {
					#if AFTER_21_1
					return ResourceLocation.fromNamespaceAndPath(NAMESPACE, "dynamiclights_resources");
					#else
					return new ResourceLocation(NAMESPACE, "dynamiclights_resources");
					#endif
				}

				@Override
				public void onResourceManagerReload(ResourceManager manager) {
					ItemLightSources.load(manager);
				}
			});

			WorldRenderEvents.START.register(context -> {
				Minecraft.getInstance().getProfiler().incrementCounter("dynamic_lighting");
				this.updateAll(context.worldRenderer());
			});
		#endif

		#if FORGELIKE
			registerReloadListener(PackType.CLIENT_RESOURCES, new SimplePreparableReloadListener() {
				@Override
				protected Object prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
					return null;
				}

				@Override
				protected void apply(Object object, ResourceManager resourceManager, ProfilerFiller profiler) {
					ItemLightSources.load(resourceManager);
				}
			});
		#endif

		DynamicLightHandlers.registerDefaultHandlers();
	}

	#if FORGELIKE
		@SubscribeEvent
		public void clientSetup(FMLClientSetupEvent event) {
			onInitializeClient();
		}

		private static List<PreparableReloadListener> serverDataReloadListeners = Lists.newArrayList();

		public static void registerReloadListener(PackType type, SimplePreparableReloadListener listener) {
			if (type == PackType.SERVER_DATA) {
				serverDataReloadListeners.add(listener);
			} else if (type == PackType.CLIENT_RESOURCES) {
				registerClient(listener);
			}
		}

		private static void registerClient(PreparableReloadListener listener) {
			((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
		}

		@SubscribeEvent
		public static void addReloadListeners(AddReloadListenerEvent event) {
			for (PreparableReloadListener listener : serverDataReloadListeners) {
				event.addListener(listener);
			}
		}
    #endif

	/**
	 * Updates all light sources.
	 *
	 * @param renderer the renderer
	 */
	public void updateAll(@NotNull LevelRenderer renderer) {
		if (!this.config.getDynamicLightsMode().isEnabled())
			return;

		long now = System.currentTimeMillis();
		if (now >= this.lastUpdate + 50) {
			this.lastUpdate = now;
			this.lastUpdateCount = 0;

			this.lightSourcesLock.readLock().lock();
			for (var lightSource : this.dynamicLightSources) {
				if (lightSource.sodiumdynamiclights$updateDynamicLight(renderer)) this.lastUpdateCount++;
			}
			this.lightSourcesLock.readLock().unlock();
		}
	}

	/**
	 * Returns the last number of dynamic light source updates.
	 *
	 * @return the last number of dynamic light source updates
	 */
	public int getLastUpdateCount() {
		return this.lastUpdateCount;
	}

	/**
	 * Returns the lightmap with combined light levels.
	 *
	 * @param pos the position
	 * @param lightmap the vanilla lightmap coordinates
	 * @return the modified lightmap coordinates
	 */
	public int getLightmapWithDynamicLight(@NotNull BlockPos pos, int lightmap) {
		return this.getLightmapWithDynamicLight(this.getDynamicLightLevel(pos), lightmap);
	}

	/**
	 * Returns the lightmap with combined light levels.
	 *
	 * @param entity the entity
	 * @param lightmap the vanilla lightmap coordinates
	 * @return the modified lightmap coordinates
	 */
	public int getLightmapWithDynamicLight(@NotNull Entity entity, int lightmap) {
		int posLightLevel = (int) this.getDynamicLightLevel(entity.getOnPos());
		int entityLuminance = ((DynamicLightSource) entity).sdl$getLuminance();

		return this.getLightmapWithDynamicLight(Math.max(posLightLevel, entityLuminance), lightmap);
	}

	/**
	 * Returns the lightmap with combined light levels.
	 *
	 * @param dynamicLightLevel the dynamic light level
	 * @param lightmap the vanilla lightmap coordinates
	 * @return the modified lightmap coordinates
	 */
	public int getLightmapWithDynamicLight(double dynamicLightLevel, int lightmap) {
		if (dynamicLightLevel > 0) {
			// lightmap is (skyLevel << 20 | blockLevel << 4)

			// Get vanilla block light level.
			int blockLevel = LightTexture.block(lightmap);
			if (dynamicLightLevel > blockLevel) {
				// Equivalent to a << 4 bitshift with a little quirk: this one ensure more precision (more decimals are saved).
				int luminance = (int) (dynamicLightLevel * 16.0);
				lightmap &= 0xfff00000;
				lightmap |= luminance & 0x000fffff;
			}
		}

		return lightmap;
	}

	/**
	 * Returns the dynamic light level at the specified position.
	 *
	 * @param pos the position
	 * @return the dynamic light level at the specified position
	 */
	public double getDynamicLightLevel(@NotNull BlockPos pos) {
		double result = 0;
		this.lightSourcesLock.readLock().lock();
		for (var lightSource : this.dynamicLightSources) {
			result = maxDynamicLightLevel(pos, lightSource, result);
		}
		this.lightSourcesLock.readLock().unlock();

		return Mth.clamp(result, 0, 15);
	}

	/**
	 * Returns the dynamic light level generated by the light source at the specified position.
	 *
	 * @param pos the position
	 * @param lightSource the light source
	 * @param currentLightLevel the current surrounding dynamic light level
	 * @return the dynamic light level at the specified position
	 */
	public static double maxDynamicLightLevel(@NotNull BlockPos pos, @NotNull DynamicLightSource lightSource, double currentLightLevel) {
		int luminance = lightSource.sdl$getLuminance();
		if (luminance > 0) {
			// Can't use Entity#squaredDistanceTo because of eye Y coordinate.
			double dx = pos.getX() - lightSource.sdl$getDynamicLightX() + 0.5;
			double dy = pos.getY() - lightSource.sdl$getDynamicLightY() + 0.5;
			double dz = pos.getZ() - lightSource.sdl$getDynamicLightZ() + 0.5;

			double distanceSquared = dx * dx + dy * dy + dz * dz;
			// 7.75 because else we would have to update more chunks and that's not a good idea.
			// 15 (max range for blocks) would be too much and a bit cheaty.
			if (distanceSquared <= MAX_RADIUS_SQUARED) {
				double multiplier = 1.0 - Math.sqrt(distanceSquared) / MAX_RADIUS;
				double lightLevel = multiplier * (double) luminance;
				if (lightLevel > currentLightLevel) {
					return lightLevel;
				}
			}
		}
		return currentLightLevel;
	}

	/**
	 * Adds the light source to the tracked light sources.
	 *
	 * @param lightSource the light source to add
	 */
	public void addLightSource(@NotNull DynamicLightSource lightSource) {
		if (!lightSource.sdl$getDynamicLightLevel().isClientSide())
			return;
		if (!this.config.getDynamicLightsMode().isEnabled())
			return;
		if (this.containsLightSource(lightSource))
			return;
		this.lightSourcesLock.writeLock().lock();
		this.dynamicLightSources.add(lightSource);
		this.lightSourcesLock.writeLock().unlock();
	}

	/**
	 * Returns whether the light source is tracked or not.
	 *
	 * @param lightSource the light source to check
	 * @return {@code true} if the light source is tracked, else {@code false}
	 */
	public boolean containsLightSource(@NotNull DynamicLightSource lightSource) {
		if (!lightSource.sdl$getDynamicLightLevel().isClientSide())
			return false;

		boolean result;
		this.lightSourcesLock.readLock().lock();
		result = this.dynamicLightSources.contains(lightSource);
		this.lightSourcesLock.readLock().unlock();
		return result;
	}

	/**
	 * Returns the number of dynamic light sources that currently emit lights.
	 *
	 * @return the number of dynamic light sources emitting light
	 */
	public int getLightSourcesCount() {
		int result;

		this.lightSourcesLock.readLock().lock();
		result = this.dynamicLightSources.size();
		this.lightSourcesLock.readLock().unlock();

		return result;
	}

	/**
	 * Removes the light source from the tracked light sources.
	 *
	 * @param lightSource the light source to remove
	 */
	public void removeLightSource(@NotNull DynamicLightSource lightSource) {
		this.lightSourcesLock.writeLock().lock();

		var dynamicLightSources = this.dynamicLightSources.iterator();
		DynamicLightSource it;
		while (dynamicLightSources.hasNext()) {
			it = dynamicLightSources.next();
			if (it.equals(lightSource)) {
				dynamicLightSources.remove();
				lightSource.sodiumdynamiclights$scheduleTrackedChunksRebuild(Minecraft.getInstance().levelRenderer);
				break;
			}
		}

		this.lightSourcesLock.writeLock().unlock();
	}

	/**
	 * Clears light sources.
	 */
	public void clearLightSources() {
		this.lightSourcesLock.writeLock().lock();

		var dynamicLightSources = this.dynamicLightSources.iterator();
		DynamicLightSource it;
		while (dynamicLightSources.hasNext()) {
			it = dynamicLightSources.next();
			dynamicLightSources.remove();
			if (it.sdl$getLuminance() > 0)
				it.sdl$resetDynamicLight();
			it.sodiumdynamiclights$scheduleTrackedChunksRebuild(Minecraft.getInstance().levelRenderer);
		}

		this.lightSourcesLock.writeLock().unlock();
	}

	/**
	 * Removes light sources if the filter matches.
	 *
	 * @param filter the removal filter
	 */
	public void removeLightSources(@NotNull Predicate<DynamicLightSource> filter) {
		this.lightSourcesLock.writeLock().lock();

		var dynamicLightSources = this.dynamicLightSources.iterator();
		DynamicLightSource it;
		while (dynamicLightSources.hasNext()) {
			it = dynamicLightSources.next();
			if (filter.test(it)) {
				dynamicLightSources.remove();
				if (it.sdl$getLuminance() > 0)
					it.sdl$resetDynamicLight();
				it.sodiumdynamiclights$scheduleTrackedChunksRebuild(Minecraft.getInstance().levelRenderer);
				break;
			}
		}

		this.lightSourcesLock.writeLock().unlock();
	}

	/**
	 * Removes entities light source from tracked light sources.
	 */
	public void removeEntitiesLightSource() {
		this.removeLightSources(lightSource -> (lightSource instanceof Entity && !(lightSource instanceof Player)));
	}

	/**
	 * Removes Creeper light sources from tracked light sources.
	 */
	public void removeCreeperLightSources() {
		this.removeLightSources(entity -> entity instanceof Creeper);
	}

	/**
	 * Removes TNT light sources from tracked light sources.
	 */
	public void removeTntLightSources() {
		this.removeLightSources(entity -> entity instanceof PrimedTnt);
	}

	/**
	 * Removes block entities light source from tracked light sources.
	 */
	public void removeBlockEntitiesLightSource() {
		this.removeLightSources(lightSource -> lightSource instanceof BlockEntity);
	}

	/**
	 * Prints a message to the terminal.
	 *
	 * @param info the message to print
	 */
	public void log(String info) {
		this.logger.info("[LambDynLights] " + info);
	}

	/**
	 * Prints a warning message to the terminal.
	 *
	 * @param info the message to print
	 */
	public void warn(String info) {
		this.logger.warn("[LambDynLights] " + info);
	}

	/**
	 * Schedules a chunk rebuild at the specified chunk position.
	 *
	 * @param renderer the renderer
	 * @param chunkPos the chunk position
	 */
	public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, @NotNull BlockPos chunkPos) {
		scheduleChunkRebuild(renderer, chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
	}

	/**
	 * Schedules a chunk rebuild at the specified chunk position.
	 *
	 * @param renderer the renderer
	 * @param chunkPos the packed chunk position
	 */
	public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, long chunkPos) {
		scheduleChunkRebuild(renderer, BlockPos.getX(chunkPos), BlockPos.getY(chunkPos), BlockPos.getZ(chunkPos));
	}

	public static void scheduleChunkRebuild(@NotNull LevelRenderer renderer, int x, int y, int z) {
		if (Minecraft.getInstance().level != null)
			((WorldRendererAccessor) renderer).sodiumdynamiclights$scheduleChunkRebuild(x, y, z, false);
	}

	/**
	 * Updates the tracked chunk sets.
	 *
	 * @param chunkPos the packed chunk position
	 * @param old the set of old chunk coordinates to remove this chunk from it
	 * @param newPos the set of new chunk coordinates to add this chunk to it
	 */
	public static void updateTrackedChunks(@NotNull BlockPos chunkPos, @Nullable LongOpenHashSet old, @Nullable LongOpenHashSet newPos) {
		if (old != null || newPos != null) {
			long pos = chunkPos.asLong();
			if (old != null)
				old.remove(pos);
			if (newPos != null)
				newPos.add(pos);
		}
	}

	/**
	 * Updates the dynamic lights tracking.
	 *
	 * @param lightSource the light source
	 */
	public static void updateTracking(@NotNull DynamicLightSource lightSource) {
		boolean enabled = lightSource.sdl$isDynamicLightEnabled();
		int luminance = lightSource.sdl$getLuminance();

		if (!enabled && luminance > 0) {
			lightSource.sdl$setDynamicLightEnabled(true);
		} else if (enabled && luminance < 1) {
			lightSource.sdl$setDynamicLightEnabled(false);
		}
	}

	private static boolean isEyeSubmergedInFluid(LivingEntity entity) {
		if (!SodiumDynamicLights.get().config.getWaterSensitiveCheck().get()) {
			return false;
		}

		var eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
		return !entity.level().getFluidState(eyePos).isEmpty();
	}

	public static int getLivingEntityLuminanceFromItems(LivingEntity entity) {
		boolean submergedInFluid = isEyeSubmergedInFluid(entity);
		int luminance = 0;

		for (var equipped : entity.getAllSlots()) {
			if (!equipped.isEmpty())
				luminance = Math.max(luminance, SodiumDynamicLights.getLuminanceFromItemStack(equipped, submergedInFluid));
		}

		return luminance;
	}

	/**
	 * Returns the luminance from an item stack.
	 *
	 * @param stack the item stack
	 * @param submergedInWater {@code true} if the stack is submerged in water, else {@code false}
	 * @return the luminance of the item
	 */
	public static int getLuminanceFromItemStack(@NotNull ItemStack stack, boolean submergedInWater) {
		return ItemLightSources.getLuminance(stack, submergedInWater);
	}

	/**
	 * Returns the SodiumDynamicLights mod instance.
	 *
	 * @return the mod instance
	 */
	public static SodiumDynamicLights get() {
		return INSTANCE;
	}
}
