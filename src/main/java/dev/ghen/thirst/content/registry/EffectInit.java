package dev.ghen.thirst.content.registry;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.effect.QuenchnessEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectInit {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, Thirst.ID);
    public static final DeferredHolder<MobEffect, MobEffect> QUENCHNESS = MOB_EFFECTS.register("quenchness", () -> new QuenchnessEffect(MobEffectCategory.BENEFICIAL, 0xDC143C));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
