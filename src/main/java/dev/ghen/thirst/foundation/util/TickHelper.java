package dev.ghen.thirst.foundation.util;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;

@EventBusSubscriber
public class TickHelper
{
    /**
     * Util for running actions on the server delayed by n ticks
     * may not be the best implementation, i'm a dumb idiot.
     * */
    private static final Map<Integer, List<Runnable>> tickTasks = new HashMap<>();
    private static int tickTimerFsr = 0;

    public static void addTask(int tick, Runnable task)
    {
        if(!tickTasks.containsKey(tick))
            tickTasks.put(tick, new ArrayList<>());

        tickTasks.get(tick).add(task);
    }

    public static void nextTick(Level level, Runnable task)
    {
        addTask(Objects.requireNonNull(level.getServer()).getTickCount() + 1, task);
    }

    public static void TickLater(Level level, int tickNumber,Runnable task)
    {
        addTask(Objects.requireNonNull(level.getServer()).getTickCount() + tickNumber, task);
    }

    @SubscribeEvent
    static void runTasks(LevelTickEvent.Pre event)
    {
        if(event.getLevel() instanceof ServerLevel && tickTimerFsr == 0 && tickTasks.containsKey(event.getLevel().getServer().getTickCount()))
        {
            tickTasks.get(event.getLevel().getServer().getTickCount()).forEach(Runnable::run);
            tickTasks.remove(event.getLevel().getServer().getTickCount());

            tickTimerFsr += 3;
        }
        else if(tickTimerFsr > 0)
            tickTimerFsr--;
    }
}
