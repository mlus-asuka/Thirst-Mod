package dev.ghen.thirst.foundation.config;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record LootConfigCondition() implements ICondition
{
    public static final LootConfigCondition INSTANCE = new LootConfigCondition();
    public static final MapCodec<LootConfigCondition> CODEC = MapCodec.unit(INSTANCE).stable();

    @Override
    public boolean test(ICondition.@NotNull IContext context) {
        return CommonConfig.ENABLE_LOOT.get();
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
