package dev.ghen.thirst.foundation.gui.appleskin;

import dev.ghen.thirst.Thirst;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Thirst.ID,bus = EventBusSubscriber.Bus.MOD)
public class OverlayRegister {
    @SubscribeEvent
    public static void onRenderGuiOverlayPost(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.FOOD_LEVEL, HUDOverlayHandler.ExhaustionOverlay.ID,new HUDOverlayHandler.ExhaustionOverlay());
        event.registerAboveAll(HUDOverlayHandler.SaturationOverlay.ID,new HUDOverlayHandler.SaturationOverlay());
    }
}
