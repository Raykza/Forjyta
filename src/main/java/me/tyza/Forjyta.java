package me.tyza;

import com.mojang.logging.LogUtils;
import me.tyza.jda.Bot;
import me.tyza.jda.Embeds;
import me.tyza.mod.command.SchedulerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.command.ConfigCommand;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("forjyta")
public class Forjyta
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    private static Bot bot;

    private static ScheduledExecutorService scheduledShutdown;
    public static ScheduledExecutorService getScheduledShutdown() {
        return scheduledShutdown;
    }



    public Forjyta() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        scheduledShutdown = Executors.newScheduledThreadPool(1);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // Some example code to dispatch IMC to another mod
        //InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // Some example code to receive and process InterModComms from other mods
        /*LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));*/
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        bot = new Bot(LOGGER);
        bot.sendToNewsChannel("**SV ON**", true);
    }

     @SubscribeEvent
     public void onCommandsRegister(RegisterCommandsEvent event) {
        SchedulerCommand.register(event.getDispatcher());

         ConfigCommand.register(event.getDispatcher());
     }

    @SubscribeEvent
    public void onServerStop(ServerStoppingEvent event) {
        bot.sendToNewsChannel("**SV OFF**",true);
        bot.modifyEmbed(
                new EmbedBuilder()
                        .setTitle("SERVER OFF")
                        .setColor(0xDE2E43)
                        .build()
        );
        scheduledShutdown.shutdownNow();
        bot.disableBot();
        LOGGER.info("Bye bye!");
    }

    public static Bot getBot() {
        return bot;
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here
        }
    }


}
