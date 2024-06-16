package dev.ghen.thirst.content.thirst;

import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.network.message.DrinkByHandMessage;
import dev.ghen.thirst.foundation.util.MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class DrinkByHandClient
{
    public static void drinkByHand()
    {
        Minecraft mc = Minecraft.getInstance();

        Player player = mc.player;
        Level level = mc.level;
        BlockPos blockPos = MathHelper.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY).getBlockPos();
        boolean HandAvailable;

        if (level.getFluidState(blockPos).is(FluidTags.WATER) && player.isCrouching() && !player.isInvulnerable()) {

            if(!ClientConfig.DRINK_BOTH_HAND_NEEDED.get()){
                HandAvailable = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
            }else {
                HandAvailable = player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && player.getItemInHand(InteractionHand.OFF_HAND).isEmpty();
            }
            if(HandAvailable){
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);
                PacketDistributor.sendToServer(new DrinkByHandMessage(new Vector3f(blockPos.getX(),blockPos.getY(),blockPos.getZ())));
            }
        }
    }
}
