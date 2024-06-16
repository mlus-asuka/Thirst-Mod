package dev.ghen.thirst.content.thirst;

import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.content.purity.WaterPurity;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.config.CommonConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class PlayerThirstManager {

//    @SubscribeEvent
//    public static void attachCapabilityToEntityHandler(AttachCapabilitiesEvent<Entity> event)
//    {
//        if (event.getObject() instanceof Player)
//        {
//            IThirst playerThirstCap = new PlayerThirst();
//            LazyOptional<IThirst> capOptional = LazyOptional.of(() -> playerThirstCap);
//            Capability<IThirst> capability = ModCapabilities.PLAYER_THIRST;
//
//            ICapabilityProvider provider = new ICapabilitySerializable<CompoundTag>()
//            {
//                @Nonnull
//                @Override
//                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction)
//                {
//                    if (cap == capability)
//                    {
//                        return capOptional.cast();
//                    }
//                    return LazyOptional.empty();
//                }
//
//                @Override
//                public CompoundTag serializeNBT()
//                {
//                    return playerThirstCap.serializeNBT();
//                }
//
//                @Override
//                public void deserializeNBT(CompoundTag nbt)
//                {
//                    playerThirstCap.deserializeNBT(nbt);
//                }
//            };
//
//            event.addCapability(Thirst.asResource("thirst"), provider);
//        }
//    }

    @SubscribeEvent
    public static void drinkByHand(PlayerInteractEvent.RightClickBlock event) {
        if (CommonConfig.CAN_DRINK_BY_HAND.get() && event.getEntity().level().isClientSide)
            DrinkByHandClient.drinkByHand();
    }

    @SubscribeEvent
    public static void drinkByHand(PlayerInteractEvent.RightClickEmpty event) {
        if (CommonConfig.CAN_DRINK_BY_HAND.get() && event.getEntity().level().isClientSide)
            DrinkByHandClient.drinkByHand();
    }

    @SubscribeEvent
    public static void drink(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player && ThirstHelper.itemRestoresThirst(event.getItem())) {
            ItemStack item = event.getItem();
            if (WaterPurity.givePurityEffects((Player) event.getEntity(), item))
                event.getEntity().getData(ModAttachment.PLAYER_THIRST).drink(ThirstHelper.getThirst(item), ThirstHelper.getQuenched(item));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            serverPlayer.getData(ModAttachment.PLAYER_THIRST).tick(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            player.getData(ModAttachment.PLAYER_THIRST).setThirst(20);
            player.getData(ModAttachment.PLAYER_THIRST).setQuenched(5);
        }
    }

    @SubscribeEvent
    public static void EndFix(PlayerEvent.Clone event) {
        if (event.isWasDeath() && event.getOriginal().hasData(ModAttachment.PLAYER_THIRST)) {
            event.getEntity().getData(ModAttachment.PLAYER_THIRST).copy(event.getOriginal().getData(ModAttachment.PLAYER_THIRST));
        }
    }
}


