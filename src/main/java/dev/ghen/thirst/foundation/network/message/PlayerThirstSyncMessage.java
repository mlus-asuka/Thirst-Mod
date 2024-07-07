package dev.ghen.thirst.foundation.network.message;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record PlayerThirstSyncMessage(int thirst,int quenched,float exhaustion,boolean enable) implements CustomPacketPayload
{

    public static final CustomPacketPayload.Type<PlayerThirstSyncMessage> TYPE = new Type<>(Thirst.asResource("thirstsync"));

    public static final StreamCodec<ByteBuf, PlayerThirstSyncMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PlayerThirstSyncMessage::thirst,
            ByteBufCodecs.INT,
            PlayerThirstSyncMessage::quenched,
            ByteBufCodecs.FLOAT,
            PlayerThirstSyncMessage::exhaustion,
            ByteBufCodecs.BOOL,
            PlayerThirstSyncMessage::enable,
            PlayerThirstSyncMessage::new
    );


    public static void serverHandle(final PlayerThirstSyncMessage message,final IPayloadContext context)
    {

    }

    public static void clientHandle(final PlayerThirstSyncMessage message,final IPayloadContext context)
    {
        context.enqueueWork(() -> {
            Player player = context.player();
            IThirst cap = player.getData(ModAttachment.PLAYER_THIRST);
            cap.setThirst(message.thirst);
            cap.setQuenched(message.quenched);
            cap.setExhaustion(message.exhaustion);
            cap.setShouldTickThirst(message.enable);
        });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}