package dev.ghen.thirst.foundation.network;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.network.message.DrinkByHandMessage;
import dev.ghen.thirst.foundation.network.message.PlayerThirstSyncMessage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Thirst.ID,bus = EventBusSubscriber.Bus.MOD)
public class ThirstModPacketHandler
{
    private static final String PROTOCOL_VERSION = "0.1.3";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playBidirectional(
                DrinkByHandMessage.TYPE,
                DrinkByHandMessage.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        DrinkByHandMessage::clientHandle,
                        DrinkByHandMessage::serverHandle
                )
        );
        registrar.playBidirectional(
                PlayerThirstSyncMessage.TYPE,
                PlayerThirstSyncMessage.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        PlayerThirstSyncMessage::clientHandle,
                        PlayerThirstSyncMessage::serverHandle
                )
        );
    }
}
