package dev.ghen.thirst.foundation.common.effect;

import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class QuenchnessEffect extends InstantenousMobEffect {
    public QuenchnessEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public boolean applyEffectTick(LivingEntity p_295892_, int p_296026_) {
        if (!p_295892_.level().isClientSide && p_295892_ instanceof Player player) {
            player.getData(ModAttachment.PLAYER_THIRST).drink(1,1);
        }

        return true;
    }
}
