package dev.ghen.thirst.canteen.common.item;

import net.minecraft.world.item.ItemStack;

public interface Drinkable {
    int getMaxUsableTimes();

    int getLeftUsableTimes(ItemStack stack);
}
