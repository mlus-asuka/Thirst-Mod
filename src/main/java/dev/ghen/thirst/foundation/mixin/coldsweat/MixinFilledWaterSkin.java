package dev.ghen.thirst.foundation.mixin.coldsweat;

import com.momosoftworks.coldsweat.common.item.FilledWaterskinItem;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FilledWaterskinItem.class)
public class MixinFilledWaterSkin {
    @Inject(method = "consumeWaterskin",at = @At("HEAD"))
    private static void consumeWaterskin(ItemStack stack, Player player, InteractionHand usedHand, CallbackInfo ci) {
        IThirst iThirst = player.getData(ModAttachment.PLAYER_THIRST);
        iThirst.drink(6,3);
    }
}
