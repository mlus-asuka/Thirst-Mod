package dev.ghen.thirst.compat.create;

import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

public class SandFilterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation
{
    public static final int TANK_SIZE = 1000;
    SmartFluidTankBehaviour dirtyTank;
    SmartFluidTankBehaviour purifiedTank;

    public SandFilterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        dirtyTank = SmartFluidTankBehaviour.single(this, TANK_SIZE);
        behaviours.add(dirtyTank);
        purifiedTank = SmartFluidTankBehaviour.single(this, TANK_SIZE);
        behaviours.add(purifiedTank);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0, -2, 0);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                CreateRegistry.SAND_FILTER_BE.get(),
                (be, side) -> {
                    if (side != null && side.getAxis() == Direction.Axis.Y)
                    {
                        if(side == Direction.DOWN)
                            return be.purifiedTank.getCapability();
                        else
                            return be.dirtyTank.getCapability();
                    }
                    return null;
                }
        );
    }

    public void tick()
    {
        super.tick();

        if(!level.isClientSide() && dirtyTank.getPrimaryHandler().getFluidAmount() >= CommonConfig.SAND_FILTER_MB_PER_TICK.get().intValue() &&
                purifiedTank.getPrimaryHandler().getFluidAmount() < TANK_SIZE)
        {
            FluidStack water = dirtyTank.getPrimaryHandler().drain(CommonConfig.SAND_FILTER_MB_PER_TICK.get().intValue(), IFluidHandler.FluidAction.EXECUTE);

            if(water.getFluid().equals(Fluids.WATER))
                WaterPurity.addPurity(water, Math.min(WaterPurity.getPurity(water) + CommonConfig.SAND_FILTER_FILTRATION_AMOUNT.get().intValue(), WaterPurity.MAX_PURITY));

            purifiedTank.getPrimaryHandler().fill(water, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking)
    {
            LangBuilder mb = CreateLang.translate("generic.unit.millibuckets");
            CreateLang.translate("gui.goggles.fluid_container")
                    .forGoggles(tooltip);

            int dirtyWaterAmount = dirtyTank.getPrimaryHandler().getFluidAmount();
            int purifiedWaterAmount = purifiedTank.getPrimaryHandler().getFluidAmount();

        buildTooltip(tooltip, mb, dirtyWaterAmount, dirtyTank);

        buildTooltip(tooltip, mb, purifiedWaterAmount, purifiedTank);

        if(dirtyTank.isEmpty() && purifiedTank.isEmpty()){
            CreateLang.translate("gui.goggles.fluid_container.capacity")
                    .add(CreateLang.number(dirtyTank.getPrimaryHandler().getTankCapacity(0))
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip, 1);
        }

        return !dirtyTank.isEmpty() || !purifiedTank.isEmpty();
    }

    private void buildTooltip(List<Component> tooltip, LangBuilder mb, int purifiedWaterAmount, SmartFluidTankBehaviour purifiedTank) {
        if(!purifiedTank.isEmpty())
        {
            CreateLang.builder()
                    .text(WaterPurity.getPurityText(WaterPurity.getPurity(purifiedTank.getPrimaryHandler().getFluid())))
                    .add(CreateLang.text(" "))
                    .add(CreateLang.fluidName(purifiedTank.getPrimaryHandler().getFluid()))
                    .style(ChatFormatting.GRAY)
                    .forGoggles(tooltip);

            CreateLang.builder()
                    .add(CreateLang.number(purifiedWaterAmount)
                            .add(mb)
                            .style(ChatFormatting.GOLD))
                    .text(ChatFormatting.GRAY, " / ")
                    .add(CreateLang.number(purifiedTank.getPrimaryHandler().getCapacity())
                            .add(mb)
                            .style(ChatFormatting.DARK_GRAY))
                    .forGoggles(tooltip, 1);
        }
    }
}