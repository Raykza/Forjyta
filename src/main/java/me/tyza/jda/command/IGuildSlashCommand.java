package me.tyza.jda.command;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface IGuildSlashCommand {
    void handle(SlashCommandEvent event);
    String getHelp();
    String getInvoke();
}
