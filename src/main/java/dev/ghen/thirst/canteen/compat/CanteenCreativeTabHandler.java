package dev.ghen.thirst.canteen.compat;

import dev.ghen.thirst.canteen.common.item.Canteen;
import dev.ghen.thirst.canteen.registry.ThirstCanteenItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CanteenCreativeTabHandler {
    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("thirst", "thirst"))) {
            addCanteens(event);
        }
    }

    private static void addCanteens(BuildCreativeModeTabContentsEvent event) {
        ThirstCanteenItem.ITEMS.getEntries().forEach(itemRegistryObject -> {
            ItemStack itemStack = itemRegistryObject.get().getDefaultInstance();
            if (itemStack.getItem() instanceof Canteen canteen) {
                itemStack.getOrCreateTag().putInt("Contain", canteen.getMaxUsableTimes());
                itemStack.getOrCreateTag().putInt("Purity", canteen.getDefaultPurity());
            }
            event.accept(itemStack);
        });
    }

    public static void register(IEventBus modBus) {
        modBus.register(CanteenCreativeTabHandler.class);
    }
}
