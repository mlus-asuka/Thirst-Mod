package dev.ghen.thirst.foundation.mixin.canteen;

import dev.ghen.thirst.canteen.common.item.Canteen;
import dev.ghen.thirst.canteen.util.CanteenRecipeAssembler;
import mekanism.api.recipes.ItemStackToItemStackRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemStackToItemStackRecipe.class, remap = false)
public abstract class MixinItemStackToItemStackRecipe {
    @Final
    @Shadow
    private ItemStack output;

    /**
     * @author mlus
     * @reason Mek compat
     */
    @Overwrite
    @Contract(
            value = "_ -> new",
            pure = true
    )
    public ItemStack getOutput(ItemStack input) {
        if (input.getItem() instanceof Canteen) {
            return CanteenRecipeAssembler.assemble(input, output, RecipeType.SMELTING);
        }
        return output.copy();
    }
}
