package dev.ghen.thirst.compat.create;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.content.registry.ItemInit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public class CreateRegistry
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Thirst.ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Thirst.ID);
    public static void register(){}

    public static final DeferredHolder<Block, Block> SAND_FILTER_BLOCK = BLOCKS.register("sand_filter",
                    () -> new SandFilterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

    public static final DeferredHolder<Item, Item> SAND_FILTER_ITEM = ItemInit.ITEMS.register("sand_filter",
            () -> new BlockItem(SAND_FILTER_BLOCK.get(), new Item.Properties()));

    public static final Supplier<BlockEntityType<SandFilterBlockEntity>> SAND_FILTER_BE = BLOCK_ENTITY_TYPES.register("sand_filter",
            () -> BlockEntityType.Builder.of(SandFilterBlockEntity::new, SAND_FILTER_BLOCK.get()).build(null));
}
