package dev.ghen.thirst.content.registry;

import dev.ghen.thirst.foundation.common.item.DrinkableItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;


public class ItemInit {
    public static final DeferredRegister.Items ITEMS;
    public static final DeferredItem<Item> CLAY_BOWL;
    public static final DeferredItem<Item>  TERRACOTTA_BOWL;
    public static final DeferredItem<Item>  TERRACOTTA_WATER_BOWL;

    public ItemInit() {
    }

    static {
        ITEMS = DeferredRegister.createItems("thirst");
        CLAY_BOWL = ITEMS.register("clay_bowl", () -> new Item((new Item.Properties())
                .stacksTo(64)
        ));
        TERRACOTTA_BOWL = ITEMS.register("terracotta_bowl", () -> new Item((new Item.Properties())
                .stacksTo(64)
        ));
        TERRACOTTA_WATER_BOWL = ITEMS.register("terracotta_water_bowl", () -> (new DrinkableItem())
                .setContainer(TERRACOTTA_BOWL.get())
        );
    }
}