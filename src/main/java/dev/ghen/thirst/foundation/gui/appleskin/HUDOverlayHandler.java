package dev.ghen.thirst.foundation.gui.appleskin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.config.ClientConfig;
import dev.ghen.thirst.foundation.gui.ThirstBarRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.opengl.GL11;
import squeek.appleskin.ModConfig;
import squeek.appleskin.util.IntPoint;

import java.util.Random;
import java.util.Vector;

@OnlyIn(Dist.CLIENT)
public class HUDOverlayHandler {
    private static float unclampedFlashAlpha = 0.0F;
    private static float flashAlpha = 0.0F;
    private static byte alphaDir = 1;
    protected static int foodIconsOffset;
    public static final Vector<squeek.appleskin.util.IntPoint> foodBarOffsets = new Vector<>();
    private static final Random random = new Random();
    private static final ResourceLocation modIcons = Thirst.asResource("textures/gui/appleskin_icons.png");

    public HUDOverlayHandler() {
    }

    public static void init() {
        NeoForge.EVENT_BUS.register(new HUDOverlayHandler());
    }

    public static boolean shouldDrawSurvivalElements(Minecraft minecraft) {
        return minecraft.gameMode.canHurtPlayer() && minecraft.getCameraEntity() instanceof Player;
    }

    public static class ExhaustionOverlay extends squeek.appleskin.client.HUDOverlayHandler.Overlay{
        public static final ResourceLocation ID = Thirst.asResource("exhaustion_overlay");
        public ExhaustionOverlay() {}
        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics, int left, int right, int top, int guiTicks) {
            Minecraft mc = Minecraft.getInstance();
            boolean isMounted = mc.player.getVehicle() instanceof LivingEntity;
            boolean isAlive = mc.player.isAlive();

            if (isAlive && !isMounted && !mc.options.hideGui && shouldDrawSurvivalElements(mc) && !ThirstBarRenderer.cancelRender) {
                if(ModConfig.SHOW_FOOD_EXHAUSTION_UNDERLAY.get()){
                    renderExhaustion(mc.gui,guiGraphics);
                }
            }
        }
    }

    public static class SaturationOverlay extends squeek.appleskin.client.HUDOverlayHandler.Overlay{
        public static final ResourceLocation ID = Thirst.asResource("saturation_overlay");
        public SaturationOverlay(){}
        @Override
        public void render(Minecraft minecraft, Player player, GuiGraphics guiGraphics, int left, int right, int top, int guiTicks) {
            Minecraft mc = Minecraft.getInstance();
            boolean isMounted = mc.player.getVehicle() instanceof LivingEntity;
            boolean isAlive = mc.player.isAlive();

            if (isAlive && !isMounted && !mc.options.hideGui && shouldDrawSurvivalElements(mc) && !ThirstBarRenderer.cancelRender) {
                if(ModConfig.SHOW_SATURATION_OVERLAY.get()){
                    renderThirstOverlay(guiGraphics);
                }
            }
        }
    }

    public static void renderExhaustion(Gui gui, GuiGraphics mStack)
    {
        foodIconsOffset = gui.rightHeight +10;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;

        int right = mc.getWindow().getGuiScaledWidth() / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET.get();
        int top = mc.getWindow().getGuiScaledHeight() - foodIconsOffset + ClientConfig.THIRST_BAR_Y_OFFSET.get();
        float exhaustion = player.getData(ModAttachment.PLAYER_THIRST).getExhaustion();

        drawExhaustionOverlay(exhaustion, mStack, right, top);
    }

    public static void renderThirstOverlay(GuiGraphics guiGraphics)
    {
        if (!shouldRenderAnyOverlays())
            return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;
        IThirst thirstData = player.getData(ModAttachment.PLAYER_THIRST);

        int right = mc.getWindow().getGuiScaledWidth() / 2 + 91 + ClientConfig.THIRST_BAR_X_OFFSET.get();
        int top = mc.getWindow().getGuiScaledHeight() - foodIconsOffset + ClientConfig.THIRST_BAR_Y_OFFSET.get();

        generateHungerBarOffsets(top, right, mc.gui.getGuiTicks(), player);

        drawSaturationOverlay(0, thirstData.getQuenched(), guiGraphics , right, top, 1f);

        // try to get the item stack in the player hand
        ItemStack heldItem = player.getMainHandItem();
        if (ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get() && !ThirstHelper.itemRestoresThirst(heldItem))
            heldItem = player.getOffhandItem();

        boolean shouldRenderHeldItemValues = !heldItem.isEmpty() && ThirstHelper.itemRestoresThirst(heldItem);
        if (!shouldRenderHeldItemValues)
        {
            resetFlash();
            return;
        }

        ThirstValues thirstValues = new ThirstValues(ThirstHelper.getThirst(heldItem), ThirstHelper.getQuenched(heldItem));

        // calculate the final hunger and saturation
        int drinkThirst = thirstValues.thirst;

        // restored hunger/saturation overlay while holding food
        if(thirstData.getThirst() < 20)
            drawHungerOverlay(drinkThirst, thirstData.getThirst(), guiGraphics, right, top, flashAlpha);
        // Redraw saturation overlay for gained
        if(!ThirstHelper.isFood(heldItem) || player.getFoodData().getFoodLevel() < 20)
            drawSaturationOverlay(thirstValues.quenchedModifier, thirstData.getQuenched(),guiGraphics, right, top, flashAlpha);
    }

    public static void drawSaturationOverlay(float saturationGained, float saturationLevel, GuiGraphics guiGraphics, int right, int top, float alpha)
    {
        if (saturationLevel + saturationGained < 0)
            return;

        enableAlpha(alpha);
        RenderSystem.setShaderTexture(0, modIcons);

        float modifiedSaturation = Math.max(0, Math.min(saturationLevel + saturationGained, 20));

        int startSaturationBar = 0;
        int endSaturationBar = (int) Math.ceil(modifiedSaturation / 2.0F);

        // when require rendering the gained saturation, start should relocation to current saturation tail.
        if (saturationGained != 0)
            startSaturationBar = (int) Math.max(saturationLevel / 2.0F, 0);

        int iconSize = 9;

        for (int i = startSaturationBar; i < endSaturationBar; ++i)
        {
            // gets the offset that needs to be rendered of icon
            IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            int v = 0;
            int u = 0;

            float effectiveSaturationOfBar = (modifiedSaturation / 2.0F) - i;

            if (effectiveSaturationOfBar >= 1)
                u = 3 * iconSize;
            else if (effectiveSaturationOfBar > .5)
                u = 2 * iconSize;
            else if (effectiveSaturationOfBar > .25)
                u = iconSize;

            guiGraphics.blit(modIcons, x, y, u, v, iconSize, iconSize);
        }

        disableAlpha();
    }

    public static void drawHungerOverlay(int hungerRestored, int foodLevel, GuiGraphics guiGraphics, int right, int top, float alpha)
    {
        if (hungerRestored <= 0)
            return;

        enableAlpha(alpha);
        RenderSystem.setShaderTexture(0, ThirstBarRenderer.THIRST_ICONS);

        int modifiedFood = Math.max(0, Math.min(20, foodLevel + hungerRestored));

        int startFoodBars = Math.max(0, foodLevel / 2);
        int endFoodBars = (int) Math.ceil(modifiedFood / 2.0F);

        int iconStartOffset = 8 -3;
        int iconSize = 9;

        for (int i = startFoodBars; i < endFoodBars; ++i)
        {
            // gets the offset that needs to be rendered of icon
            IntPoint offset = foodBarOffsets.get(i);
            if (offset == null)
                continue;

            int x = right + offset.x;
            int y = top + offset.y;

            // location to normal food by default
            int v = 3 * iconSize;
            int u = iconStartOffset + 4 * iconSize;

            // relocation to half food
            if (i * 2 + 1 == modifiedFood)
                u -= iconSize -1;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

            guiGraphics.blit(ThirstBarRenderer.THIRST_ICONS, x, y, u, v, iconSize, iconSize, 25, 9);
        }

        disableAlpha();
    }

    public static void drawExhaustionOverlay(float exhaustion, GuiGraphics guiGraphics, int right, int top)
    {
        RenderSystem.setShaderTexture(0, modIcons);

        float maxExhaustion = 4.0f;
        // clamp between 0 and 1
        float ratio = Math.min(1, Math.max(0, exhaustion / maxExhaustion));
        int width = (int) (ratio * 81);
        int height = 9;

        enableAlpha(.75f);
        guiGraphics.blit(modIcons, right - width, top, 81 - width, 18, width, height);
        disableAlpha();
    }

    public static void enableAlpha(float alpha)
    {
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void disableAlpha()
    {
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event)
    {

        unclampedFlashAlpha += alphaDir * 0.125f;
        if (unclampedFlashAlpha >= 1.5f)
        {
            alphaDir = -1;
        }
        else if (unclampedFlashAlpha <= -0.5f)
        {
            alphaDir = 1;
        }
        flashAlpha = Math.max(0F, Math.min(1F, unclampedFlashAlpha)) * 0.65f;
    }

    public static void resetFlash()
    {
        unclampedFlashAlpha = flashAlpha = 0f;
        alphaDir = 1;
    }

    private static boolean shouldRenderAnyOverlays()
    {
        return true;
    }

    private static void generateHungerBarOffsets(int top, int right, int ticks, Player player)
    {
        final int preferFoodBars = 10;

        boolean shouldAnimatedFood;

        IThirst thirstData = player.getData(ModAttachment.PLAYER_THIRST);

        // in vanilla saturation level is zero will show hunger animation
        float quenched = thirstData.getQuenched();
        int thirst = thirstData.getThirst();
        shouldAnimatedFood = quenched <= 0.0F && ticks % (thirst * 3 + 1) == 0;

        if (foodBarOffsets.size() != preferFoodBars)
            foodBarOffsets.setSize(preferFoodBars);

        // right alignment, single row
        for (int i = 0; i < preferFoodBars; ++i)
        {
            int x = right - i * 8 - 9;
            int y = top;

            // apply the animated offset
            if (shouldAnimatedFood)
                y += random.nextInt(3) - 1;

            // reuse the point object to reduce memory usage
            IntPoint point = foodBarOffsets.get(i);
            if (point == null)
            {
                point = new IntPoint();
                foodBarOffsets.set(i, point);
            }

            point.x = x - right;
            point.y = y - top;
        }
    }
}
