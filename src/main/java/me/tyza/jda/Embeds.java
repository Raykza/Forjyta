package me.tyza.jda;

import me.tyza.Forjyta;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import me.tyza.utils.*;

// TODO: Make a method for loading custom Embeds from JSON file.
// TODO: Make a new class "DynamicEmbed" that manages the edits of an Embed.
public class Embeds {
    public static net.dv8tion.jda.api.entities.MessageEmbed statusEmbed(ServerStatus serverStatus) {
        String ip = serverStatus.getIp();
        String relative_latency = String.valueOf(serverStatus.getLatency());

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("⬛  Estado del servidor");
        builder.addField("", "", false);

        builder.addField(
                "⬛  IP: ",
                "\uD83D\uDFE2  "+"**"+ip+"**",
                false);

        builder.addField(
                "⬛  Latencia:",
                Utils.pingToEmoji(relative_latency,50,100) +"  "+relative_latency+" ms",
                false);

        /*builder.addField(
                "⬛ TPS:",
                Utils.tpsToEmoji(TPS) +" "+TPS,
                false);*/

        serverStatus.getDimensions().forEach(dimension -> {
            builder.addField(dimension.toEmbedField(false));
        });

        builder.addField(
                "⬛  Jugadores:",
                "⬛  "+serverStatus.getPlayerList().getPlayerCount(),
                false);

        builder.setColor(Utils.pingToColor(relative_latency,50,100));

        builder.addField("", "", false);
        String footer = new StringBuilder()
                .append(Utils.hourOfDayToEmoji(Utils.getLocalHour()))
                .append(" ")
                .append(Utils.getLocalFormattedTime())
                .append(" ")
                .append(Utils.getLocalHourOffset())
                .toString();
        builder.setFooter(footer);

        MessageEmbed messageEmbed = builder.build();
        return messageEmbed;
    }

    public static net.dv8tion.jda.api.entities.MessageEmbed insufficientPrivilegesEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Privilegios insuficientes para ejecutar este comando.")
                .setColor(0xDE2E43);
        MessageEmbed messageEmbed = builder.build();
        return messageEmbed;
    }

    public static void insufficientPrivilegesReply(Message message) {
        message.replyEmbeds(insufficientPrivilegesEmbed()).queue();
    }

    public static void editDynamicEmbed(Guild guild, String channelID, String embedID, MessageEmbed newEmbed) {
        guild.getTextChannelById(channelID).editMessageEmbedsById(embedID, newEmbed).queue();
    }

    public static void editDynamicEmbed(String guildID, String channelID, String embedID, MessageEmbed newEmbed) {
        editDynamicEmbed(Forjyta.getBot().getJda().getGuildById(guildID), channelID, embedID, newEmbed);
    }
}
