package dev.ghen.thirst.foundation.mixin;

import dev.ghen.thirst.foundation.gui.RenderGuiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class MixinGui
{

    @Unique
    private DeltaTracker deltaTracker;
    @Shadow
    public int rightHeight;

    @Inject(method="render", at=@At(value="HEAD"))
    public void onRender(GuiGraphics p_282884_, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        this.deltaTracker = deltaTracker;
    }

    @Inject(method="renderCameraOverlays", at=@At(value="INVOKE", target="net/minecraft/client/player/LocalPlayer.getTicksFrozen()I"))
    private void onBeginRenderFrozenOverlay(GuiGraphics guiGraphics, DeltaTracker p_344236_, CallbackInfo ci)
    {
        NeoForge.EVENT_BUS.post(new RenderGuiEvent.Pre(RenderGuiEvent.Type.FROSTBITE, (Gui)(Object)this, guiGraphics, this.deltaTracker, guiGraphics.guiWidth(), guiGraphics.guiHeight()));
    }

    @Inject(method="renderFoodLevel", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/Gui;getPlayerVehicleWithHealth()Lnet/minecraft/world/entity/LivingEntity;"))
    private void onRenderPlayerHealth(GuiGraphics guiGraphics, CallbackInfo ci)
    {
        NeoForge.EVENT_BUS.post(new RenderGuiEvent.Pre(RenderGuiEvent.Type.FOOD, (Gui)(Object)this, guiGraphics, this.deltaTracker, guiGraphics.guiWidth(), guiGraphics.guiHeight()));
    }

    @Inject(method="renderAirLevel", at=@At(value="HEAD"))
    private void onBeginRenderAir(GuiGraphics guiGraphics, CallbackInfo ci)
    {
        NeoForge.EVENT_BUS.post(new RenderGuiEvent.Pre(RenderGuiEvent.Type.AIR, (Gui)(Object)this, guiGraphics, this.deltaTracker, guiGraphics.guiWidth(), guiGraphics.guiHeight(),rightHeight - 10));
    }
}
