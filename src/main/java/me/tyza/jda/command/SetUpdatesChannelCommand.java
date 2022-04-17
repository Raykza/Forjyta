package me.tyza.jda.command;

import me.tyza.jda.Bot;
import me.tyza.jda.Embeds;
import me.tyza.utils.PropertiesManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.util.List;

public class SetUpdatesChannelCommand implements IGuildMessageCommand{

    protected PropertiesManager propertiesManager = null;

    SetUpdatesChannelCommand(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

    @Override
    public void handle(GuildMessageReceivedEvent event, List<String> args) {
        if(!Bot.hasPrivileges(event.getMember())) {
            Embeds.insufficientPrivilegesReply(event.getMessage());
        } else {
            this.propertiesManager
                    .setProperty("status_channel",event.getChannel().getId())
                    .save(new File("forjyta.properties"));
            event.getMessage()
                    .replyEmbeds(
                    new EmbedBuilder()
                            .setTitle("Current channel "+event.getChannel().toString()+" set as default channel.")
                            .setColor(0x79B15A)
                            .build()).queue();
        }
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getInvoke() {
        return "here";
    }
}
