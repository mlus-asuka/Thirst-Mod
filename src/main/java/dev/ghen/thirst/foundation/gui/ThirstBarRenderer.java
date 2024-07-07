package dev.ghen.thirst.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.gui.appleskin.HUDOverlayHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT)
public class ThirstBarRenderer
{
    public static IThirst PLAYER_THIRST = null;
    public static ResourceLocation THIRST_ICONS = Thirst.asResource("textures/gui/thirst_icons.png");
    public static Boolean cancelRender = false;
    public static Boolean checkIfPlayerIsVampire = false;
    static Minecraft minecraft = Minecraft.getInstance();
    protected final static RandomSource random = RandomSource.create();

    @SubscribeEvent
    public static void onBeginRenderAir(RenderGuiEvent.Pre event)
    {
        if (event.getType() != RenderGuiEvent.Type.AIR)
            return;

        Entity vehicle = minecraft.player.getVehicle();
        boolean isMounted = vehicle != null && vehicle.showVehicleHealth();
        cancelRender =false;
        if (!isMounted && !minecraft.options.hideGui && HUDOverlayHandler.shouldDrawSurvivalElements(minecraft))
        {
//            if(checkIfPlayerIsVampire)
//            {
//                if(Helper.isVampire(gui.getMinecraft().player))
//                {
//                    cancelRender =true;
//                    return;
//                }
//            }

            if(minecraft.player.isAlive() && !minecraft.player.getData(ModAttachment.PLAYER_THIRST).getShouldTickThirst()){
                cancelRender = true;
                return;
            }

            setupOverlayRenderState(true, false);

            render(event.getScreenWidth(),event.getScreenHeight(),event.getGuiGraphics());
        }
    }

    public static void setupOverlayRenderState(boolean blend, boolean depthTest)
    {
        if (blend)
        {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }
        else RenderSystem.disableBlend();

        if (depthTest)
            RenderSystem.enableDepthTest();
        else
            RenderSystem.disableDepthTest();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    public static void render(int width, int height, GuiGraphics guiGraphics)
    {
        minecraft.getProfiler().push("thirst");
        if (PLAYER_THIRST == null || minecraft.player.tickCount % 40 == 0)
        {
            PLAYER_THIRST = minecraft.player.getData(ModAttachment.PLAYER_THIRST);
        }

        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, THIRST_ICONS);
        int left = width / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET.get();
        int top = height - minecraft.gui.rightHeight + ClientConfig.THIRST_BAR_Y_OFFSET.get();
        minecraft.gui.rightHeight += 10;

        int level = PLAYER_THIRST.getThirst();

        for (int i = 0; i < 10; ++i)
        {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            if (PLAYER_THIRST.getQuenched() <= 0.0F && minecraft.gui.getGuiTicks() % (level * 3 + 1) == 0)
            {
                y = top + (random.nextInt(3) - 1);
            }

            guiGraphics.blit(THIRST_ICONS, x, y, 0, 0, 9, 9, 25, 9);

            if (idx < level)
                guiGraphics.blit(THIRST_ICONS, x, y, 16, 0, 9, 9, 25, 9);
            else if (idx == level)
                guiGraphics.blit(THIRST_ICONS, x, y, 8, 0, 9, 9, 25, 9);
        }
        RenderSystem.disableBlend();

        minecraft.getProfiler().pop();
    }
}