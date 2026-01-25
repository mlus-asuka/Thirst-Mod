package dev.ghen.thirst.canteen;

import dev.ghen.thirst.canteen.common.item.Canteen;
import dev.ghen.thirst.canteen.compat.CanteenCreativeTabHandler;
import dev.ghen.thirst.canteen.compat.ThirstCompatEvent;
import dev.ghen.thirst.canteen.config.ThirstCanteenConfig;
import dev.ghen.thirst.canteen.registry.ThirstCanteenItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

public class ThirstCanteen {
    public static final String MODID = "thirstcanteen";

    public static void thirstCanteen(FMLJavaModLoadingContext context) {
        ThirstCanteenConfig.setup(context);

        IEventBus modBus = context.getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        ThirstCanteenItem.ITEMS.register(modBus);

        ThirstCompatEvent.register(forgeBus);

        CanteenCreativeTabHandler.register(modBus);
    }

    @Mod.EventBusSubscriber
    public static class ListeningEvents {
        @SubscribeEvent(priority = EventPriority.HIGH)
        public static void onRenderItemTooltips(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (stack.getItem() instanceof Canteen canteen) {
                List<Component> tooltip = event.getToolTip();
                tooltip.add(Component.translatable("tooltips.drinkable", canteen.getLeftUsableTimes(stack), canteen.getMaxUsableTimes()));
            }
            if (stack.is(ThirstCanteenItem.LEATHER_CANTEEN.get())) {
                event.getToolTip().add(Component.nullToEmpty("Thanks SquARzy for drawing this."));
            }
        }
    }
}
