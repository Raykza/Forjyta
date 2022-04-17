package me.tyza.mod.event;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;
import net.minecraftforge.event.server.ServerStoppingEvent;

@Mod.EventBusSubscriber(modid="forjyta")
public class EventSubscriber {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        // IMPORTANT
        // Add commands here
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onServerStop(ServerStoppingEvent event){

    }
}
