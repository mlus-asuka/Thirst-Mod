package dev.ghen.thirst.canteen.compat;

import dev.ghen.thirst.canteen.common.item.Canteen;
import dev.ghen.thirst.canteen.config.ThirstCanteenConfig;
import dev.ghen.thirst.canteen.registry.ThirstCanteenItem;
import dev.ghen.thirst.foundation.common.event.RegisterThirstValueEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ThirstCompatEvent {
    @SubscribeEvent
    public static void registerDrinks(RegisterThirstValueEvent event){
        ThirstCanteenItem.ITEMS.getEntries().forEach(itemRegistryObject ->{
            if(itemRegistryObject.get() instanceof Canteen canteen){
                event.addDrink(canteen, ThirstCanteenConfig.THIRST_RESTORE_EACH_SIP.get().intValue(),ThirstCanteenConfig.QUENCHED_RESTORE_EACH_SIP.get().intValue());
                event.addContainer(canteen);
            }
        } );
    }

    public static void register(IEventBus modBus) {
        modBus.register(ThirstCompatEvent.class);
    }
}
