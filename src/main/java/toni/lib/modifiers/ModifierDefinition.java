package toni.lib.modifiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import toni.lib.VersionUtils;

import java.util.UUID;

public class ModifierDefinition
{
    private static String ID;
    #if AFTER_21_1
    private static ResourceLocation Resource;
    #else
    private static UUID Resource;
    #endif

    public ModifierDefinition(String modid, String path)
    {
        ID = path;

        #if AFTER_21_1
        Resource = VersionUtils.resource(modid, path);
        #else
        Resource = Mth.createInsecureUUID(RandomSource.create(path.hashCode()));
        #endif
    }

    public void removeModifier(AttributeInstance attribute) {
        attribute.removeModifier(Resource);
    }

    public void addPermanentModifier(AttributeInstance attribute, double value) {
        attribute.addPermanentModifier(new AttributeModifier(
            Resource,
            #if BEFORE_21_1 ID, #endif
            value,
            #if BEFORE_21_1 AttributeModifier.Operation.ADDITION #else AttributeModifier.Operation.ADD_VALUE #endif));
    }


}
