package me.tyza.mod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.logging.LogUtils;
import me.tyza.Forjyta;
import me.tyza.utils.Utils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

import java.sql.Time;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SchedulerCommand {
    private final static Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bsch")
                        .requires((sender) -> {
                            return sender.hasPermission(2);}) // Permission
                        .then(Commands.literal("stop")
                                .then(Commands.argument("seconds", HourArgument.hour())
                                        .executes((sender) -> {
                                            MinecraftServer server = sender.getSource().getServer();

                                            int seconds = (IntegerArgumentType.getInteger(sender, "seconds"));
                                            Calendar now = Utils.getLocalNow();
                                            now.add(Calendar.SECOND, seconds);
                                            LOGGER.info(Utils.getFormattedCalendar(now));
                                            String fullMessage = "El servidor se va a cerrar a las "+Utils.getFormattedCalendar(now);

                                            // TODOn't: Send warnings to Minecraft's chat, implementing TellrawCommand.


                                            // TODO: The scheduler on Forjyta.java shouldn't be public and global.
                                            Forjyta.getScheduledShutdown().schedule(() -> {
                                                sender.getSource().sendSuccess(new TranslatableComponent("commands.stop.stopping"), true);
                                                server.halt(false); // TODO: Replace with Actions.haltServer()
                                            }, seconds, TimeUnit.SECONDS);

                                            Forjyta.getScheduledShutdown().schedule(() -> {
                                                Forjyta.getBot().sendToNewsChannel("El servidor se va a cerrar en **5 minutos**", true);
                                            }, seconds - (5*60), TimeUnit.SECONDS);

                                            Forjyta.getBot().sendToNewsChannel(
                                                    fullMessage,
                                                    true);
                                            Actions.messagePlayers(server.getPlayerList(), fullMessage);


                                            return seconds;
                                        }
                                )
                        )


                        //.then(Commands.literal("a1"))

                        //.then(Commands.literal("b2"))
        ));
    }
}
