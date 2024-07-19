package dev.ghen.thirst.foundation.mixin.farmersrespite;

import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import umpaz.farmersrespite.common.block.entity.KettleBlockEntity;

@Mixin(value = KettleBlockEntity.class,remap = false)
public abstract class MixinKettleBlockEntity {
    @Redirect(method = "processBrewing",at= @At(value = "INVOKE", target = "Lnet/minecraftforge/fluids/capability/templates/FluidTank;setFluid(Lnet/minecraftforge/fluids/FluidStack;)V"))
    private void ProcessBrewing(FluidTank instance, FluidStack stack){
        int purity = Math.min(WaterPurity.getPurity(instance.getFluid()) + CommonConfig.KETTLE_PURIFICATION_LEVELS.get().intValue(), WaterPurity.MAX_PURITY);
        instance.setFluid(WaterPurity.addPurity(stack,purity));
    }
}
