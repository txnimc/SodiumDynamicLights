package toni.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class VersionUtils
{
    public static ResourceLocation resource(String modid, String path) {
        #if AFTER_21_1
        return ResourceLocation.fromNamespaceAndPath(modid, path);
        #else
        return new ResourceLocation(modid, path);
        #endif
    }

    public static Component text(String str) {
        #if AFTER_21_1
        return Component.literal(str);
        #else
        return Component.literal(str);
        #endif
    }

    public static Level level(Entity entity)
    {
        #if AFTER_20_1
        return entity.level();
        #else
        return entity.level;
        #endif

    }
}
