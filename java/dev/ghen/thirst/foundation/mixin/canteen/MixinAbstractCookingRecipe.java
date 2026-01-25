package dev.ghen.thirst.foundation.mixin.canteen;

import dev.ghen.thirst.canteen.common.item.Canteen;
import dev.ghen.thirst.canteen.util.CanteenRecipeAssembler;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractCookingRecipe.class)
public abstract class MixinAbstractCookingRecipe {
    @Mutable
    @Shadow
    @Final
    protected ItemStack result;

    @Shadow
    @Final
    protected RecipeType<?> type;

    @Inject(method = "matches", at = @At("TAIL"), cancellable = true)
    public void matches(Container container, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && container.getItem(0).getItem() instanceof Canteen) {
            if (container.getItem(0).getOrCreateTag().getInt("Purity") == 3) {
                cir.setReturnValue(false);
            }
            result = CanteenRecipeAssembler.assemble(container.getItem(0), result, type);
        }
    }
}
