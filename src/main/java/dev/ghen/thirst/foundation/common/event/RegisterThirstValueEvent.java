package dev.ghen.thirst.foundation.common.event;

import dev.ghen.thirst.content.purity.ContainerWithPurity;
import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    /**
     *Registers new custom water container
     *the container will be taken into consider of purity
     */
    @SuppressWarnings({"unused", "deprecation"})
    public void addContainer(ContainerWithPurity container){
        WaterPurity.addContainer(container);
    }

    /**
     * A simple version, If you don't need your item to harvest water like bucket.
     */
    @SuppressWarnings({"unused", "deprecation"})
    public void addContainer(Item item){
        WaterPurity.addContainer(new ContainerWithPurity(new ItemStack(item)));
    }


    @Override
    public boolean isCancelable() {return false;}
}
