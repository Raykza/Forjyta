package me.tyza.jda.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface IGuildMessageCommand {
    void handle(GuildMessageReceivedEvent event, List<String> args);
    String getHelp();
    String getInvoke();
}
