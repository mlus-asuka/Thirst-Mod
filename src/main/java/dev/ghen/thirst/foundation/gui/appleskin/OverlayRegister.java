package dev.ghen.thirst.foundation.gui.appleskin;

import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

public class OverlayRegister {
    public static void onRenderGuiOverlayPost(RegisterGuiLayersEvent event) {
        event.registerBelow(VanillaGuiLayers.FOOD_LEVEL, HUDOverlayHandler.ExhaustionOverlay.ID,new HUDOverlayHandler.ExhaustionOverlay());
        event.registerAboveAll(HUDOverlayHandler.SaturationOverlay.ID,new HUDOverlayHandler.SaturationOverlay());
    }
}
