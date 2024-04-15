package dev.ghen.thirst.content.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Thirst.ID)
public class CommandInit {

    @SubscribeEvent
    public static void RegisterCommand(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher=event.getDispatcher();
        dispatcher.register(Commands.literal("thirst")
                .requires(cs->cs.hasPermission(2))
                .then(Commands.literal("query").then(Commands.argument("Player", EntityArgument.player())
                        .executes(context -> {
                                    Player player = EntityArgument.getPlayer(context,"Player");
                                    IThirst iThirst = player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
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
                                            Player player = EntityArgument.getPlayer(context,"Player");
                                            IThirst iThirst = player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
                                            Object[] arg =new Object[2];
                                            arg[0]= IntegerArgumentType.getInteger(context,"thirst");
                                            arg[1]= IntegerArgumentType.getInteger(context,"quenched");

                                            iThirst.setThirst((Integer) arg[0]);
                                            iThirst.setQuenched((Integer) arg[1]);
                                            context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.set","command.thirst.set",arg)),false);
                                            return 0;
                                        })))
                ))
                .then(Commands.literal("enable").then(Commands.argument("Player",EntityArgument.player())
                        .then(Commands.argument("bool", BoolArgumentType.bool())
                                .executes(context ->{
                                    Player player = EntityArgument.getPlayer(context,"Player");
                                    boolean shouldTick = BoolArgumentType.getBool(context,"bool");
                                    IThirst thirstData =  player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
                                    thirstData.setShouldTickThirst(shouldTick);
                                    if(shouldTick){
                                        context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.enable","command.thirst.enable",new Object[]{player.getName()})),false);
                                    }else {
                                        context.getSource().sendSuccess(()->MutableComponent.create(new TranslatableContents("command.thirst.disable","command.thirst.disable",new Object[]{player.getName()})),false);
                                    }
                                    return 0;
                                }))))
        );
    }
}
