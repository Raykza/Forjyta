package me.tyza.jda.command;

import me.tyza.jda.Bot;
import me.tyza.jda.Embeds;
import me.tyza.utils.PropertiesManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.util.List;

public class GenerateStatusEmbedCommand implements IGuildMessageCommand {

    protected PropertiesManager propertiesManager = null;

    GenerateStatusEmbedCommand(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

    @Override
    public void handle(GuildMessageReceivedEvent event, List<String> args) {
        if(!Bot.hasPrivileges(event.getMember())) {
            Embeds.insufficientPrivilegesReply(event.getMessage());
        } else {
            event.getMessage().delete().queue();
            event.getChannel().sendMessageEmbeds(
                    new EmbedBuilder().setTitle("Placeholder embed").build()
            ).queue(message ->
                this.propertiesManager
                        .setProperty("embed", ""+message.getId())
                        .setProperty("embed_channel",""+message.getChannel().getId())
                        .setProperty("guild", ""+message.getGuild().getId())
                        .save(new File("forjyta.properties")));
        }
    }

    @Override
    public String getHelp() {
        return "";
    }

    @Override
    public String getInvoke() {
        return "embed";
    }
}
