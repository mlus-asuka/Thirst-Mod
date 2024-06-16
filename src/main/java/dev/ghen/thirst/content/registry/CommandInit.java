package dev.ghen.thirst.content.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModAttachment;
import dev.ghen.thirst.foundation.network.message.PlayerThirstSyncMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;


import java.util.ArrayList;
import java.util.Collection;

@EventBusSubscriber(modid = Thirst.ID)
public class CommandInit {

    @SubscribeEvent
    public static void RegisterCommand(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher=event.getDispatcher();
        dispatcher.register(Commands.literal("thirst")
                .requires(cs->cs.hasPermission(2))
                .then(Commands.literal("query").then(Commands.argument("Player", EntityArgument.player())
                        .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context,"Player");
                                    IThirst iThirst = player.getData(ModAttachment.PLAYER_THIRST);
                                    Object[] arg =new Object[2];
                                    arg[0]=iThirst.getThirst();
                                    arg[1]=iThirst.getQuenched();
                                    context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.query","command.thirst.query",arg)),false);
                                    return 0;
                                }
                        )))
                .then(Commands.literal("set").then(Commands.argument("Player", EntityArgument.player())
                        .then(Commands.argument("thirst", IntegerArgumentType.integer(0,20))
                                .then(Commands.argument("quenched", IntegerArgumentType.integer(0,20))
                                        .executes(context -> {
                                            ServerPlayer player = EntityArgument.getPlayer(context,"Player");
                                            IThirst iThirst = player.getData(ModAttachment.PLAYER_THIRST);
                                            Object[] arg =new Object[2];
                                            arg[0]= IntegerArgumentType.getInteger(context,"thirst");
                                            arg[1]= IntegerArgumentType.getInteger(context,"quenched");

                                            iThirst.setThirst((Integer) arg[0]);
                                            iThirst.setQuenched((Integer) arg[1]);
                                            context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.set","command.thirst.set",arg)),false);
                                            return 0;
                                        })))
                ))
                .then(Commands.literal("enable").then(Commands.argument("Player",EntityArgument.players())
                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                .executes(context ->{
                                    Collection<ServerPlayer> players = EntityArgument.getPlayers(context,"Player");
                                    boolean shouldTick = BoolArgumentType.getBool(context,"bool");
                                    Collection<Component> playersName = new ArrayList<>();
                                    for(ServerPlayer player:players){
                                        IThirst thirstData = player.getData(ModAttachment.PLAYER_THIRST);
                                        thirstData.setShouldTickThirst(shouldTick);
                                        PacketDistributor.sendToPlayer(player,new PlayerThirstSyncMessage(thirstData.getThirst(),thirstData.getQuenched(),thirstData.getExhaustion(),shouldTick));
                                        playersName.add(player.getName());
                                    }

                                    if(shouldTick){
                                        context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.enable","command.thirst.enable",playersName.toArray())),false);
                                    }else {
                                        context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.disable","command.thirst.disable",playersName.toArray())),false);
                                    }

                                    return 0;
                                }))))
        );
    }
}
