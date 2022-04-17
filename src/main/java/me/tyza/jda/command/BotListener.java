package me.tyza.jda.command;

import me.tyza.jda.Bot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.jetbrains.annotations.NotNull;

public class BotListener extends ListenerAdapter implements EventListener {
    private final CommandManager commandManager;
    private final Logger LOGGER;

    public BotListener(CommandManager commandManager, Logger logger) {
        this.commandManager = commandManager;
        this.LOGGER = logger;
    }

    @Override
    public void onReady(ReadyEvent event) {
        LOGGER.info(String.format("Logged as %#s", event.getJDA().getSelfUser()));
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        User author = event.getAuthor();
        Message message = event.getMessage();

        if(author.isBot() || event.isWebhookMessage()) return;

        String content = message.getContentDisplay();
        String messageRaw = message.getContentRaw();

        if(messageRaw.startsWith(Bot.getPrefix())) {
            LOGGER.info(author+" issued server command "+messageRaw+" on Discord server "+event.getGuild());
            commandManager.handleMessageCommand(event);
        }
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        commandManager.handleSlashCommand(event);
    }
}
