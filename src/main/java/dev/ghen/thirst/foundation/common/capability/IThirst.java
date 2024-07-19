package dev.ghen.thirst.foundation.common.capability;

import net.minecraft.world.entity.player.Player;

public interface IThirst
{
    int getThirst();
    void setThirst(int value);
    int getQuenched();
    void setQuenched(int value);
    float getExhaustion();
    void setExhaustion(float value);
    void addExhaustion(Player player, float amount);
    void tick(Player player);
    void drink(int thirst, int quenched);
    void updateThirstData(Player player);
    void setJustHealed();
    void ExhaustionRecalculate();
    void setShouldTickThirst(boolean value);
    boolean getShouldTickThirst();
    void copy(IThirst cap);
}
