/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of SodiumDynamicLights.
 *
 * Licensed under the MIT License. For more information,
 * see the LICENSE file.
 */

package toni.sodiumdynamiclights.api.item;

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import toni.sodiumdynamiclights.SodiumDynamicLights;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Represents an item light sources manager.
 *
 * @author LambdAurora
 * @version 2.3.2
 * @since 1.3.0
 */
public final class ItemLightSources {
	private static final Map<Item, ItemLightSource> ITEM_LIGHT_SOURCES = new Reference2ObjectOpenHashMap<>();
	private static final Map<Item, ItemLightSource> STATIC_ITEM_LIGHT_SOURCES = new Reference2ObjectOpenHashMap<>();

	private ItemLightSources() {
		throw new UnsupportedOperationException("ItemLightSources only contains static definitions.");
	}

	/**
	 * Loads the item light source data from resource pack.
	 *
	 * @param resourceManager The resource manager.
	 */
	public static void load(ResourceManager resourceManager) {
		ITEM_LIGHT_SOURCES.clear();

		resourceManager.listResources("dynamiclights/item", path -> path.getPath().endsWith(".json"))
				.forEach(ItemLightSources::load);

		ITEM_LIGHT_SOURCES.putAll(STATIC_ITEM_LIGHT_SOURCES);
	}

	private static void load(ResourceLocation resourceId, Resource resource) {
		var id = ResourceLocation.fromNamespaceAndPath(resourceId.getNamespace(), resourceId.getPath().replace(".json", ""));
		try (var reader = new InputStreamReader(resource.open())) {
			var json = JsonParser.parseReader(reader).getAsJsonObject();

			ItemLightSource.fromJson(id, json).ifPresent(data -> {
				if (!STATIC_ITEM_LIGHT_SOURCES.containsKey(data.item()))
					register(data);
			});
		} catch (IOException | IllegalStateException e) {
			SodiumDynamicLights.get().warn("Failed to load item light source \"" + id + "\".");
		}
	}

	/**
	 * Registers an item light source data.
	 *
	 * @param data The item light source data.
	 */
	private static void register(ItemLightSource data) {
		var other = ITEM_LIGHT_SOURCES.get(data.item());

		if (other != null) {
			SodiumDynamicLights.get().warn("Failed to register item light source \"" + data.id() + "\", duplicates item \""
					+ BuiltInRegistries.ITEM.getId(data.item()) + "\" found in \"" + other.id() + "\".");
			return;
		}

		ITEM_LIGHT_SOURCES.put(data.item(), data);
	}

	/**
	 * Registers an item light source data.
	 *
	 * @param data the item light source data
	 */
	public static void registerItemLightSource(ItemLightSource data) {
		var other = STATIC_ITEM_LIGHT_SOURCES.get(data.item());

		if (other != null) {
			SodiumDynamicLights.get().warn("Failed to register item light source \"" + data.id() + "\", duplicates item \""
					+ BuiltInRegistries.ITEM.getId(data.item()) + "\" found in \"" + other.id() + "\".");
			return;
		}

		STATIC_ITEM_LIGHT_SOURCES.put(data.item(), data);
	}

	/**
	 * Returns the luminance of the item in the stack.
	 *
	 * @param stack the item stack
	 * @param submergedInWater {@code true} if the stack is submerged in water, else {@code false}
	 * @return a luminance value
	 */
	public static int getLuminance(ItemStack stack, boolean submergedInWater) {
		var data = ITEM_LIGHT_SOURCES.get(stack.getItem());

		if (data != null) {
			return data.getLuminance(stack, submergedInWater);
		} else if (stack.getItem() instanceof BlockItem blockItem)
			return ItemLightSource.BlockItemLightSource.getLuminance(stack, blockItem.getBlock().defaultBlockState());
		else return 0;
	}
}
