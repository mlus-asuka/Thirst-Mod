package dev.ghen.thirst.foundation.util;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

import java.util.function.Supplier;

public class LoadedValue<T>
{
    /**
     * This class was taken from <a href="https://github.com/Momo-Studios/Cold-Sweat/blob/1.18.x-FG/src/main/java/dev/momostudios/coldsweat/util/config/ConfigValue.java">Cold Sweat</a>
     */

    T value;
    Supplier<T> valueCreator;

    public LoadedValue(Supplier<T> valueCreator)
    {
        this.valueCreator = valueCreator;
        this.value = valueCreator.get();
        NeoForge.EVENT_BUS.register(this);
    }

    public static <V> LoadedValue<V> of(Supplier<V> valueCreator)
    {
        return new LoadedValue<>(valueCreator);
    }

    @SubscribeEvent
    public void onLoaded(ServerStartedEvent event)
    {
        this.value = valueCreator.get();
    }

    public T get()
    {
        return value;
    }
}
