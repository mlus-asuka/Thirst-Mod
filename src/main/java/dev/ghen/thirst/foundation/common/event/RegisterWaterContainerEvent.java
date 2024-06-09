package dev.ghen.thirst.foundation.common.event;


import dev.ghen.thirst.content.purity.ContainerWithPurity;
import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class RegisterWaterContainerEvent extends Event {

    public RegisterWaterContainerEvent() {
    }

    /**
     *Registers new custom water container
     *the container will be taken into consider of purity
     */
    @SuppressWarnings("unused")
    public void addContainer(ContainerWithPurity container){
        WaterPurity.addContainer(container);
    }

    /**
     * A simple version, If you don't need your item to harvest water like bucket.
     */
    @SuppressWarnings("unused")
    public void addContainer(Item item){
        WaterPurity.addContainer(new ContainerWithPurity(new ItemStack(item)));
    }

    @Override
    public boolean isCancelable() {return false;}
}
