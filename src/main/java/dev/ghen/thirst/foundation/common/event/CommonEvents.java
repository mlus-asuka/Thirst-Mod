package dev.ghen.thirst.foundation.common.event;

import dev.ghen.thirst.compat.create.SandFilterBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber
public class CommonEvents {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if(ModList.get().isLoaded("create")) {
            SandFilterBlockEntity.registerCapabilities(event);
        }
    }
}
