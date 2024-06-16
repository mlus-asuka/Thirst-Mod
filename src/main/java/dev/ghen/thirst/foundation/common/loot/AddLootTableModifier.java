package dev.ghen.thirst.foundation.common.loot;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AddLootTableModifier extends LootModifier {
    public static final MapCodec<AddLootTableModifier> CODEC = RecordCodecBuilder.mapCodec(
                    (inst) -> codecStart(inst).and(ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("lootTable").forGetter(
                            (m) -> m.lootTable)).apply(inst, AddLootTableModifier::new));
    private final ResourceKey<LootTable> lootTable;

    protected AddLootTableModifier(LootItemCondition[] conditionsIn,ResourceKey<LootTable> lootTable) {
        super(conditionsIn);
        this.lootTable=lootTable;
    }

    @Nonnull
    protected ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context)
    {
        LootTable extraTable = context.getResolver().get(Registries.LOOT_TABLE, this.lootTable)
            .map(Holder::value)
            .orElse(LootTable.EMPTY);
        extraTable.getRandomItems(context, generatedLoot::add);
        return generatedLoot;
    }

    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
