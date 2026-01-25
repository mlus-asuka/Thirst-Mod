package dev.ghen.thirst.canteen.common.item;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Canteen extends Item implements Drinkable {

    Supplier<Integer> usableTime;
    Supplier<ItemStack> container;
    Supplier<Integer> defaultPurity;

    public Canteen(Properties properties, Supplier<Integer> usableTime, Supplier<ItemStack> container) {
        this(properties, usableTime, container, () -> 0);
    }

    public Canteen(Item.Properties properties, Supplier<Integer> usableTime, Supplier<ItemStack> container, Supplier<Integer> defaultPurity) {
        super(properties.stacksTo(1));
        this.usableTime = usableTime;
        this.defaultPurity = defaultPurity;
        this.container = container;
    }

    public int getUseDuration(@NotNull ItemStack p_43001_) {
        return 32;
    }

    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack p_42997_) {
        return UseAnim.DRINK;
    }

    @Override
    public int getMaxUsableTimes() {
        return usableTime.get();
    }

    @Override
    public int getLeftUsableTimes(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getInt("Contain");
    }

    public int getDefaultPurity() {
        return Math.min(defaultPurity.get(), 3);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity) {
        Player player = entity instanceof Player ? (Player) entity : null;
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
        }

        itemStack.getOrCreateTag().putInt("Contain", Math.max(0, getLeftUsableTimes(itemStack) - 1));
        int times = getLeftUsableTimes(itemStack);

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (times == 0 && !player.getAbilities().instabuild) {
                ItemStack containerStack = new ItemStack(container.get().getItem());

                if (containerStack.isEmpty() || !player.getInventory().add(containerStack)) {
                    player.drop(containerStack, false);
                }
                itemStack.shrink(1);
                return itemStack;
            }
        }

        player.gameEvent(GameEvent.DRINK);
        return itemStack;
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);

        if (this.getLeftUsableTimes(stack) == getMaxUsableTimes()) {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.success(stack);
        }

        boolean handled = false;
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        BlockState blockState = level.getBlockState(blockPos);

        int needed = stack.getDamageValue();

        if (level.getFluidState(blockPos).is(FluidTags.WATER)) {
            stack.getOrCreateTag().putInt("Contain", getMaxUsableTimes());
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
                    return InteractionResultHolder.pass(stack);
                iFluidHandler.drain(actual * 250, IFluidHandler.FluidAction.EXECUTE);
                stack.getOrCreateTag().putInt("Contain", Math.min(getMaxUsableTimes(), needed + actual));
                //
                WaterPurity.addPurity(stack, Math.min(Math.max(getDefaultPurity(), purity), WaterPurity.getPurity(stack)));
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

            stack.getOrCreateTag().putInt("Contain", Math.min(getMaxUsableTimes(), needed + waterLevel));
            handled = true;
        } else {
            player.startUsingItem(interactionHand);
        }

        if (handled) {
            //
            WaterPurity.addPurity(stack, Math.min(Math.max(getDefaultPurity(), WaterPurity.getBlockPurity(level, blockPos)), WaterPurity.getPurity(stack)));
            level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }

        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }
}

