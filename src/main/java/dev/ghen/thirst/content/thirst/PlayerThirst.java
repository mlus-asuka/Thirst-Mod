package dev.ghen.thirst.content.thirst;

import de.teamlapen.vampirism.util.Helper;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.damagesource.ModDamageSource;
import dev.ghen.thirst.foundation.config.CommonConfig;
import dev.ghen.thirst.foundation.network.ThirstModPacketHandler;
import dev.ghen.thirst.foundation.network.message.PlayerThirstSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import vectorwing.farmersdelight.common.registry.ModEffects;

public class PlayerThirst implements IThirst
{
    public static boolean checkTombstoneEffects = false;
    public static boolean checkFDEffects = false;
    public static boolean checkVampirismEffects = false;
    public static boolean checkLetsDoBakeryEffects = false;

    int thirst = 20;
    int quenched = 5;
    float exhaustion = 0;
    int damageTimer = 0;
    int syncTimer = 0;
    float prevTickExhaustion = 0.0F;
    boolean justHealed = false;
    boolean shouldTickThirst = true;
    boolean exhaustionRecalculate = false;
    boolean init = true;

    public int getThirst()
    {
        return thirst;
    }

    public void setThirst(int value)
    {
        thirst = value;
    }

    public int getQuenched()
    {
        return quenched;
    }

    public void setQuenched(int value)
    {
        quenched = value;
    }

    public float getExhaustion()
    {
        return exhaustion;
    }

    public void setExhaustion(float value)
    {
        exhaustion = value;
    }

    @Override
    public void setShouldTickThirst(boolean value){shouldTickThirst = value;}
    @Override
    public boolean getShouldTickThirst(){return shouldTickThirst;}

    public void drink(Player player, int thirst, int quenched)
    {
        this.thirst = Math.min(this.thirst + thirst, 20);
        this.quenched = Math.min(this.quenched + quenched, this.thirst);
    }

    /**
    * Method adapted from minecraft's Food Data class equivalent for hunger.
    */
    public void tick(Player player)
    {
        Difficulty difficulty = player.level.getDifficulty();

        if(player.getAbilities().invulnerable)
            return;

        if(!shouldTickThirst) {
            if (init) {
                init = false;
                updateThirstData(player);
            }
            return;
        }

        if(checkTombstoneEffects && player.getActiveEffects().stream().anyMatch(e -> e.getDescriptionId().contains("ghostly_shape")))
            return;

        if(checkVampirismEffects && Helper.isVampire(player))
            return;

        boolean isNourished = checkFDEffects && player.hasEffect(ModEffects.NOURISHMENT.get());

        boolean isSitting = player.isPassenger();

        if(CommonConfig.DEPLETES_WHEN_NAUSEA.get() && player.getActiveEffects().stream().anyMatch(e->e.getEffect().equals(MobEffects.CONFUSION))){
            addExhaustion(player,0.06F);
        }

        if (!isSitting && !isNourished)
        {
            updateExhaustion(player);
        }

        if (exhaustion > 4)
        {
            exhaustion -= 4;
            if (quenched > 0)
            {
                quenched--;
            }
            else if (difficulty != Difficulty.PEACEFUL || CommonConfig.THIRST_DEPLETION_IN_PEACEFUL.get())
            {
                thirst = Math.max(thirst - 1, 0);
            }
        }

        ++syncTimer;
        if(syncTimer > 10 && !player.getLevel().isClientSide())
        {
            updateThirstData(player);
            syncTimer = 0;
        }
        if (thirst <= 0)
        {
            ++damageTimer;
            if (damageTimer >= 40)
            {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD ||
                        player.getHealth() > 0 && difficulty == Difficulty.NORMAL)
                {
                    player.hurt(ModDamageSource.DEHYDRATE, 1.0F);
                }

                damageTimer = 0;
            }
        }
    }

    void updateExhaustion(Player player)
    {
        float hungerExhaustion = player.getFoodData().getExhaustionLevel();
        float normalizedHungerExhaustion = hungerExhaustion < this.prevTickExhaustion ? (exhaustionRecalculate ? hungerExhaustion + 4.0F : hungerExhaustion) : hungerExhaustion;
        if(exhaustionRecalculate){
            exhaustionRecalculate = false;
        }
        float deltaExhaustion = normalizedHungerExhaustion - this.prevTickExhaustion;
        this.addExhaustion(player, deltaExhaustion);
        this.prevTickExhaustion = hungerExhaustion;
    }

    public void updateThirstData(Player player)
    {
        ThirstModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                new PlayerThirstSyncMessage(thirst, quenched, exhaustion,shouldTickThirst));
    }

    @Override
    public void setJustHealed()
    {
        justHealed = true;
    }

    @Override
    public void ExhaustionRecalculate(){exhaustionRecalculate = true;}

    @Override
    public void copy(IThirst cap)
    {
        thirst = cap.getThirst();
        quenched = cap.getQuenched();
        exhaustion = cap.getExhaustion();
        shouldTickThirst = cap.getShouldTickThirst();
    }

    public void addExhaustion(Player player, float amount)
    {
        if(!CommonConfig.HEALTH_REGEN_DEPLETES_HYDRATION.get() && justHealed)
            amount = 0;

        if(!CommonConfig.HEALTH_REGEN_DEHYDRATION_IS_BIOME_DEPENDENT.get() && justHealed)
            exhaustion += amount;
        else
            exhaustion += (amount *
                    ThirstHelper.getExhaustionBiomeModifier(player) *
                    ThirstHelper.getExhaustionFireProtModifier(player)*
                    ThirstHelper.getExhaustionFireResistanceModifier(player)
            );

        if(justHealed)
            justHealed = false;

        updateThirstData(player);
    }

    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("thirst", thirst);
        nbt.putInt("quenched", quenched);
        nbt.putFloat("exhaustion", exhaustion);
        nbt.putBoolean("enable",shouldTickThirst);

        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt)
    {
        thirst = nbt.getInt("thirst");
        quenched = nbt.getInt("quenched");
        exhaustion = nbt.getFloat("exhaustion");
        shouldTickThirst = !nbt.contains("enable") || nbt.getBoolean("enable");
    }
}
