package me.tyza.jda.command;

import me.tyza.Forjyta;
import me.tyza.jda.Bot;
import me.tyza.jda.Embeds;
import me.tyza.mod.command.Actions;
import me.tyza.utils.ServerStatus;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class TestCommand implements IGuildMessageCommand{
    @Override
    public void handle(GuildMessageReceivedEvent event, List<String> args) {
        ServerStatus ss = Actions.getMinecraftServerStatus();
        event.getMessage().replyEmbeds(Embeds.statusEmbed(ss)).queue();
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getInvoke() {
        return "test";
    }
}
