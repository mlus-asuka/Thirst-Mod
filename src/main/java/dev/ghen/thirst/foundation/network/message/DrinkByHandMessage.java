package dev.ghen.thirst.foundation.network.message;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.content.purity.WaterPurity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record DrinkByHandMessage(Vector3f pos) implements CustomPacketPayload
{

    public static final CustomPacketPayload.Type<DrinkByHandMessage> TYPE = new Type<>(Thirst.asResource("drinkbyhand"));

    public static final StreamCodec<ByteBuf, DrinkByHandMessage> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            DrinkByHandMessage::pos,
            DrinkByHandMessage::new
    );

    public static void clientHandle(final DrinkByHandMessage data, final IPayloadContext context){

    }


    public static void serverHandle(final DrinkByHandMessage data, final IPayloadContext context) {
            context.enqueueWork(() ->
            {
                Player player = context.player();
                Level level = player.level();

                int purity = WaterPurity.getBlockPurity(level, new BlockPos((int) data.pos.x, (int) data.pos.y, (int) data.pos.z));
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                if(WaterPurity.givePurityEffects(player, purity))
                        player.getData(ModAttachment.PLAYER_THIRST).drink(CommonConfig.HAND_DRINKING_HYDRATION.get().intValue(), CommonConfig.HAND_DRINKING_QUENCHED.get().intValue());
            });
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
