package dev.ghen.thirst.foundation.common.event;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.Event;

import static dev.ghen.thirst.api.ThirstHelper.VALID_DRINKS;
import static dev.ghen.thirst.api.ThirstHelper.VALID_FOODS;

public class RegisterThirstValueEvent extends Event {
    public RegisterThirstValueEvent(){
    }

    /**
     * Adds a hydration and "quenchness" value to an item via code, and treats it as food.
     * Can be overwritten by the player in the config.
     * */
    @SuppressWarnings("unused")
    public void addFood(Item item, int thirst, int quenched)
    {
        VALID_FOODS.putIfAbsent(item, new Number[]{thirst, quenched});
    }

    /**
     * Adds a hydration and "quenchness" value to an item via code, and treats it as a drink.
     * Can be overwritten by the player in the config.
     * */
    @SuppressWarnings("unused")
    public void addDrink(Item item, int thirst, int quenched)
    {
        VALID_DRINKS.putIfAbsent(item, new Number[]{thirst, quenched});
    }


    @Override
    public boolean isCancelable() {return false;}
}
