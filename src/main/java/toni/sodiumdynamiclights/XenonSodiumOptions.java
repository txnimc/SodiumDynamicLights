package toni.sodiumdynamiclights;

#if FORGE
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.embeddedt.embeddium.api.OptionGUIConstructionEvent;
import toni.sodiumdynamiclights.util.DynamicLightingPage;

@Mod.EventBusSubscriber(modid = SodiumDynamicLights.NAMESPACE, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class XenonSodiumOptions {
    @SubscribeEvent
    public static void onEmbeddiumPagesRegister(OptionGUIConstructionEvent e) {
        var pages = e.getPages();

        pages.add(new DynamicLightingPage());
    }
}
#endif