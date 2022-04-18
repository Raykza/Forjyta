package me.tyza.jda;

import me.tyza.Forjyta;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import me.tyza.utils.*;

import java.awt.*;
import java.util.Objects;

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
        String footer = Utils.hourOfDayToEmoji(Utils.getLocalHour()) + " " +
                        Utils.getLocalFormattedTime() + " " +
                        Utils.getLocalHourOffset();
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

    // TODO: TEST THIS!

    public static MessageEmbed embedFromJSON(JsonObject json) {
        EmbedBuilder builder = new EmbedBuilder();

        JsonPrimitive json_title = json.getAsJsonPrimitive("title");
        if (json_title != null) builder.setTitle(json_title.getAsString());

        JsonPrimitive json_description = json.getAsJsonPrimitive("description");
        if (json_description != null) builder.setDescription(json_description.getAsString());

        JsonObject json_author = json.getAsJsonObject("author");
        if (json_author != null) {
            String authorName = json_author.get("name").getAsString();
            String authorIconURL = json_author.get("icon_url").getAsString();
            if (authorIconURL != null) builder.setAuthor(authorName, authorIconURL);
            else builder.setAuthor(authorName);
        }

        JsonPrimitive json_color = json.getAsJsonPrimitive("color");
        if (json_color != null) {
            Color color = new Color(json_color.getAsInt());
            builder.setColor(color);
        }

        JsonArray json_fields = json.getAsJsonArray("fields");
        if (json_fields != null) json_fields.forEach(json_field -> {
            String name    = json_field.getAsJsonObject().get("name").getAsString();
            String content = json_field.getAsJsonObject().get("value").getAsString();
            boolean inline = json_field.getAsJsonObject().get("inline").getAsBoolean();
            builder.addField(name, content, inline);
            });

        JsonPrimitive thumbnailObj = json.getAsJsonPrimitive("thumbnail");
        if (thumbnailObj != null) builder.setThumbnail(thumbnailObj.getAsString());

        JsonObject footerObj = json.getAsJsonObject("footer");
        if (footerObj != null) {
            String content = footerObj.get("text").getAsString();
            String footerIconUrl = footerObj.get("icon_url").getAsString();

            if (footerIconUrl != null) builder.setFooter(content, footerIconUrl);
            else builder.setFooter(content);
        }

        return builder.build();
    }

    public static JsonObject embedToJSON(MessageEmbed messageEmbed) {
        JsonObject object = new JsonObject();

        JsonPrimitive title = new JsonPrimitive(Objects.requireNonNull(messageEmbed.getTitle(), "Embed title was null."));

        JsonPrimitive description = new JsonPrimitive(Objects.requireNonNull(messageEmbed.getDescription(),"Embed description was null."));

        JsonObject author = new JsonObject();
        author.add("name", new JsonPrimitive(Objects.requireNonNull(Objects.requireNonNull(messageEmbed.getAuthor()).getName(),"Embed author name was null.")));
        author.add("icon_url", new JsonPrimitive(Objects.requireNonNull(Objects.requireNonNull(messageEmbed.getAuthor().getIconUrl(), "Embed author icon was null."))));

        JsonPrimitive color = new JsonPrimitive(Objects.requireNonNull(messageEmbed.getColor(),"Embed color was null.").toString());

        JsonArray fields = new JsonArray();
        messageEmbed.getFields().forEach(field -> {
            JsonObject json_field = new JsonObject();
            json_field.add("name", new JsonPrimitive(Objects.requireNonNull(field.getName(),"Embed field's name was null.")));
            json_field.add("value", new JsonPrimitive(Objects.requireNonNull(field.getValue(),"Embed field's value was null.")));
            json_field.add("inline", new JsonPrimitive(field.isInline()));
        });

        JsonPrimitive thumbnail = new JsonPrimitive(Objects.requireNonNull(Objects.requireNonNull(messageEmbed.getThumbnail()).getUrl(),"Embed thumbnail's URL was null."));

        JsonObject footer = new JsonObject();
        footer.add("text", new JsonPrimitive(Objects.requireNonNull(Objects.requireNonNull(messageEmbed.getFooter(), "Embed footer was null.").getText(),"Embed footer's text was null.")));
        footer.add("icon_url", new JsonPrimitive(Objects.requireNonNull(Objects.requireNonNull(messageEmbed.getFooter(),"Embed footer was null.").getIconUrl(),"Embed footer's icon was null.")));

        object.add("title", title);
        object.add("description", description);
        object.add("author", author);
        object.add("color", color);
        object.add("fields", fields);
        object.add("thumbnail", thumbnail);

        return object;
    }
}
