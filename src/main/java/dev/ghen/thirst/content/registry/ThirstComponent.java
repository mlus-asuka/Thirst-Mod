package dev.ghen.thirst.content.registry;

import com.mojang.serialization.Codec;
import dev.ghen.thirst.Thirst;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public class ThirstComponent {
    public static final DeferredRegister<DataComponentType<?>> DR = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, Thirst.ID);

    private ThirstComponent() {
    }

    public static final DataComponentType<Integer> PURITY = register("purity",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));


    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        DR.register(name, () -> componentType);
        return componentType;
    }
}
