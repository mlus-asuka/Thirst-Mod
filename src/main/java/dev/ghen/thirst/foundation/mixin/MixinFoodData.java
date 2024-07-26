package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class MixinFoodData
{
    @Shadow
    public abstract void addExhaustion(float p_38704_);

    @Shadow private float exhaustionLevel;
    @Unique
    private int dehydratedHealTimer = 0;


    @Redirect(
            method = {"tick"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V", ordinal = 0)
    )
    private void healWithSaturation(Player player, float amount)
    {
        FoodData foodData = player.getFoodData();
        IThirst thirstData =  player.getData(ModAttachment.PLAYER_THIRST);

        float f = Math.min(foodData.getSaturationLevel(), 6.0F);

        boolean shouldHeal = !CommonConfig.DEHYDRATION_HALTS_HEALTH_REGEN.get() || thirstData.getThirst() >= 20;

        if(shouldHeal)
        {
            player.heal(f / 6.0F);
            thirstData.setJustHealed();
            return;
        }

        dehydratedHealTimer++;
        if(dehydratedHealTimer >= 8 && thirstData.getThirst() > 18)
        {
            player.heal(f / 6.0F);
            thirstData.setJustHealed();
            dehydratedHealTimer = 0;
            return;
        }

        this.addExhaustion(-f);
    }

    @Redirect(
            method = {"tick"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V", ordinal = 1)
    )
    private void healWithHunger(Player player, float amount)
    {
        IThirst thirstData =  player.getData(ModAttachment.PLAYER_THIRST);
        boolean shouldHeal = !CommonConfig.DEHYDRATION_HALTS_HEALTH_REGEN.get() || thirstData.getThirst() > 18;

        if(shouldHeal)
        {
            player.heal(1.0F);
            thirstData.setJustHealed();
        }
        else
            this.addExhaustion(-6.0F);
    }

    @Inject(method = "tick",at = @At(value = "HEAD"))
    private void DealWithExhaustionBySaturation(Player player, CallbackInfo ci){
        if(exhaustionLevel>4.0F){
           player.getData(ModAttachment.PLAYER_THIRST).ExhaustionRecalculate();
        }
    }
}
