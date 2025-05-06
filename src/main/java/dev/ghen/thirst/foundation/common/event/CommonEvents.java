package dev.ghen.thirst.foundation.common.event;

import dev.ghen.thirst.compat.create.SandFilterBlockEntity;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.fml.ModList;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonEvents {
    @net.neoforged.bus.api.SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        if(ModList.get().isLoaded("create")) {
            SandFilterBlockEntity.registerCapabilities(event);
        }
    }
}
