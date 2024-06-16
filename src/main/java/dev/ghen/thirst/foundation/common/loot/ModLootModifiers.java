package dev.ghen.thirst.foundation.common.loot;


import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;


public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS;
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>,MapCodec<AddLootTableModifier>> ADD_LOOT_TABLE;

    public ModLootModifiers() {
    }

    static {
        LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, "thirst");
        ADD_LOOT_TABLE = LOOT_MODIFIERS.register("add_loot_table", ()->AddLootTableModifier.CODEC);
    }
}
