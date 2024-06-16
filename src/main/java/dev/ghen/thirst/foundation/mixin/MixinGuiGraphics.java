package dev.ghen.thirst.foundation.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics
{
    @Shadow
    private ItemStack tooltipStack;

    @Shadow public abstract int guiWidth();

    @Shadow public abstract int guiHeight();

    @ModifyVariable(method = "renderTooltipInternal", at  = @At(value = "LOAD", ordinal = 0), ordinal = 0, argsOnly = true)
    private List<ClientTooltipComponent> modifyRenderTooltipComponents(List<ClientTooltipComponent> components, Font fallbackFont, List<ClientTooltipComponent> components2, int x, int y, ClientTooltipPositioner positioner)
    {
        // Make components modifiable
        components = new ArrayList<>(components);

        // Fire tooltip render event
        NeoForge.EVENT_BUS.post(new RenderTooltipEvent.Pre(this.tooltipStack, (GuiGraphics)(Object)this, x, y, this.guiWidth(), this.guiHeight(), fallbackFont,components, positioner));
        return components;
    }
}
