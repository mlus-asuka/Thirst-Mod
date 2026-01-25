package dev.ghen.thirst.canteen.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class CanteenRecipeAssembler {
    public static ItemStack assemble(ItemStack input, ItemStack output, RecipeType<?> type) {
        int purity;
        int damage;
        int added = type == RecipeType.CAMPFIRE_COOKING ? 1 : 2;

        CompoundTag compoundTag = input.getOrCreateTag();
        purity = Math.min(compoundTag.getInt("Purity") + added, 3);
        damage = compoundTag.getInt("Contain");

        ItemStack result0 = output.copy();
        CompoundTag tag = result0.getOrCreateTag();

        tag.putInt("Purity", purity);
        tag.putInt("Contain", damage);
        return result0;
    }
}