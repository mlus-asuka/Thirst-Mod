package dev.ghen.thirst.foundation.common.event;

import dev.ghen.thirst.compat.create.SandFilterBlockEntity;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonEvents {
    @net.neoforged.bus.api.SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        SandFilterBlockEntity.registerCapabilities(event);
    }
}
