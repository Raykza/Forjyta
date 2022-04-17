package me.tyza.jda.command;

import me.tyza.Forjyta;
import me.tyza.jda.Bot;
import me.tyza.jda.Embeds;
import me.tyza.utils.PropertiesManager;
import me.tyza.utils.ServerStatus;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;
import java.util.List;

public class StatusEmbedManualUpdateCommand implements IGuildMessageCommand {

        protected PropertiesManager propertiesManager = null;

    StatusEmbedManualUpdateCommand(PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }

        @Override
        public void handle(GuildMessageReceivedEvent event, List<String> args) {
        if(!Bot.hasPrivileges(event.getMember())) {
            Embeds.insufficientPrivilegesReply(event.getMessage());
        } else {
            event.getMessage().delete().queue();
            Embeds.editDynamicEmbed(event.getGuild(),
                    propertiesManager.getProperty("embed_channel"),
                    propertiesManager.getProperty("embed"),
                    Embeds.statusEmbed(Forjyta.getBot().getServerStatus()));
        }
    }

        @Override
        public String getHelp() {
        return "";
    }

        @Override
        public String getInvoke() {
        return "status";
    }
    }
