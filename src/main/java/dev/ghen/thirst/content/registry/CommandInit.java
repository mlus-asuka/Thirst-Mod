package dev.ghen.thirst.content.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.ghen.thirst.foundation.common.capability.IThirst;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandInit {

    @SubscribeEvent
    public static void RegisterCommand(RegisterCommandsEvent event){
        CommandDispatcher<CommandSourceStack> dispatcher=event.getDispatcher();
        dispatcher.register(Commands.literal("thirst")
                .then(Commands.literal("query").then(Commands.argument("Player", EntityArgument.player())
                        .executes(context -> {
                                    Player player = EntityArgument.getPlayer(context,"Player");
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
                                            Player player = EntityArgument.getPlayer(context,"Player");
                                            IThirst iThirst = player.getCapability(ModCapabilities.PLAYER_THIRST).orElse(null);
                                            int thirst= IntegerArgumentType.getInteger(context,"thirst");
                                            int quenched= IntegerArgumentType.getInteger(context,"quenched");
                                            iThirst.setThirst(thirst);
                                            iThirst.setQuenched(quenched);
                                            context.getSource().sendSuccess(new TranslatableComponent("command.thirst.set",thirst,quenched),false);
                                            return 0;
                                        })))
                )));
    }
}
