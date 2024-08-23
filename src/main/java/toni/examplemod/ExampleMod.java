package toni.examplemod;

import net.minecraft.client.gui.Gui;
import toni.examplemod.foundation.config.AllConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


#if FABRIC
    import net.fabricmc.api.ClientModInitializer;
    import net.fabricmc.api.ModInitializer;
    import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
    import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
    import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
    import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
    #if AFTER_20_1
    import net.neoforged.fml.config.ModConfig;
    import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
    import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
    import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
    #else
    import net.minecraftforge.fml.config.ModConfig;
    import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
    import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry
    #endif
#endif


#if FORGE
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
#endif


#if NEO
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
#endif


#if FORGELIKE
@Mod("example_mod")
#endif
public class ExampleMod #if FABRIC implements ModInitializer, ClientModInitializer #endif
{
    public static final String MODNAME = "Example Mod";
    public static final String MODID = "example_mod";
    public static final Logger LOGGER = LogManager.getLogger(MODNAME);

    public ExampleMod(#if NEO IEventBus modEventBus, ModContainer modContainer #endif) {
        #if FORGE
        var context = FMLJavaModLoadingContext.get();
        var modEventBus = context.getModEventBus();
        #endif

        #if FORGELIKE
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        #endif

        AllConfigs.register((type, spec) -> {
            #if FORGE
            ModLoadingContext.get().registerConfig(type, spec);
            #elif NEO
            modContainer.registerConfig(type, spec);
            #elif FABRIC
                #if AFTER_21_1
                ForgeConfigRegistry.INSTANCE.register(ExampleMod.MODID, type, spec);
                #else
                ConfigRegistry.registerConfig(ExampleMod.MODID, type, spec);
                #endif
            #endif
        });
    }


    #if FABRIC @Override #endif
    public void onInitialize() {

    }

    #if FABRIC @Override #endif
    public void onInitializeClient() {

    }

    // Forg event stubs to call the Fabric initialize methods, and set up cloth config screen
    #if FORGELIKE
    public void commonSetup(FMLCommonSetupEvent event) { onInitialize(); }
    public void clientSetup(FMLClientSetupEvent event) { onInitializeClient(); }
    #endif
}
