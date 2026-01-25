package dev.ghen.thirst.canteen.common.item;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EmptyCanteen extends Item {
    Supplier<ItemStack> container;

    public EmptyCanteen(Properties properties, Supplier<ItemStack> container) {
        super(properties.stacksTo(1));
        this.container = container;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack result = container.get().copy();
        ItemStack stack = player.getItemInHand(interactionHand);

        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        BlockState blockState = level.getBlockState(blockPos);
        int needed = ((Canteen) result.getItem()).getMaxUsableTimes();
        int defaultPurity = ((Canteen) result.getItem()).getDefaultPurity();
        boolean handled = false;

        if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
            result.getOrCreateTag().putInt("Contain", needed);
            handled = true;
        } else if (blockEntity != null) {
            //Handle with Fluid Capability
            LazyOptional<IFluidHandler> capability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER);
            if (capability.isPresent()) {
                IFluidHandler iFluidHandler = capability.orElse(null);
                int totalAmount = 0;
                int purity = 0;
                for (int i = 0; i < iFluidHandler.getTanks(); i++) {
                    if (iFluidHandler.getFluidInTank(i).getFluid() != Fluids.WATER)
                        break;
                    else {
                        totalAmount += iFluidHandler.getFluidInTank(i).getAmount();
                        //
                        purity = WaterPurity.getPurity(iFluidHandler.getFluidInTank(i));
                    }
                }
                totalAmount = totalAmount / 250;
                int actual = Math.min(needed, totalAmount);
                if (actual <= 0)
                    return InteractionResultHolder.success(stack);
                iFluidHandler.drain(actual * 250, IFluidHandler.FluidAction.EXECUTE);
                result.getOrCreateTag().putInt("Contain", Math.min(needed, actual));
                stack.shrink(1);
                //
                WaterPurity.addPurity(result, Math.max(defaultPurity, purity));
                player.getInventory().add(result);
                level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        } else if (blockState.getBlock() instanceof LayeredCauldronBlock) {
            int waterLevel = blockState.getValue(LayeredCauldronBlock.LEVEL);
            int actual = Math.min(needed, waterLevel);
            if (actual <= 0)
                return InteractionResultHolder.pass(stack);
            if (waterLevel - actual > 0) {
                blockState.setValue(LayeredCauldronBlock.LEVEL, waterLevel - actual);
            } else {
                blockState = Blocks.CAULDRON.defaultBlockState();
            }
            level.setBlockAndUpdate(blockPos, blockState);


            result.getOrCreateTag().putInt("Contain", Math.min(needed, actual));
            handled = true;
        }

        if (handled) {
            stack.shrink(1);
            //
            WaterPurity.addPurity(result, Math.max(defaultPurity, WaterPurity.getBlockPurity(level, blockPos)));
            player.setItemInHand(interactionHand, result);
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            return InteractionResultHolder.success(result);
        }

        return InteractionResultHolder.pass(stack);
    }
}

