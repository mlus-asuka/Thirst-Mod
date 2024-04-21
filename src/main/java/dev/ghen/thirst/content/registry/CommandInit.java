package dev.ghen.thirst.content.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import dev.ghen.thirst.foundation.network.ThirstModPacketHandler;
import dev.ghen.thirst.foundation.network.message.PlayerThirstSyncMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class CommandInit {

    @SubscribeEvent
    public static void RegisterCommand(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher=event.getDispatcher();
        dispatcher.register(Commands.literal("thirst")
                .requires(cs->cs.hasPermission(2))
                .then(Commands.literal("query").then(Commands.argument("Player", EntityArgument.player())
                        .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context,"Player");
                                    IThirst iThirst = player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
                                    int thirst = iThirst.getThirst();
                                    int quenched = iThirst.getQuenched();
                                    context.getSource().sendSuccess(new TranslatableComponent("command.thirst.query",thirst,quenched),false);
                                    return 0;
                                }
                        )))
                .then(Commands.literal("set").then(Commands.argument("Player", EntityArgument.player())
                        .then(Commands.argument("thirst", IntegerArgumentType.integer(0,20))
                                .then(Commands.argument("quenched", IntegerArgumentType.integer(0,20))
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context,"Player");
                                            IThirst iThirst = player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
                                            int thirst= IntegerArgumentType.getInteger(context,"thirst");
                                            int quenched= IntegerArgumentType.getInteger(context,"quenched");
                                            iThirst.setThirst(thirst);
                                            iThirst.setQuenched(quenched);
                                            context.getSource().sendSuccess(new TranslatableComponent("command.thirst.set",thirst,quenched),false);
                                            return 0;
                                        })))
                ))
                .then(Commands.literal("enable").then(Commands.argument("Player",EntityArgument.player())
                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                .executes(context ->{
                                    ServerPlayer player = EntityArgument.getPlayer(context,"Player");
                                    boolean shouldTick = BoolArgumentType.getBool(context,"bool");
                                    IThirst thirstData =  player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
                                    thirstData.setShouldTickThirst(shouldTick);
                                    ThirstModPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                            new PlayerThirstSyncMessage(shouldTick));
                                    if(shouldTick){
                                        context.getSource().sendSuccess(new TranslatableComponent("command.thirst.enable",player.getName()),false);
                                    }else {
                                        context.getSource().sendSuccess(new TranslatableComponent("command.thirst.disable",player.getName()),false);
                                    }
                                    return 0;
                                }))))
        );
    }
}
