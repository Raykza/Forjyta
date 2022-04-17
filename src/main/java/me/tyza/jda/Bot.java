package me.tyza.jda;

import me.tyza.jda.command.BotListener;
import me.tyza.jda.command.CommandManager;
import me.tyza.mod.command.Actions;
import me.tyza.utils.PropertiesManager;
import me.tyza.utils.ServerStatus;
import me.tyza.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot {
    // TODO
    private static String prefix;
    private static String modrole_id;
    private final Logger LOGGER;
    private JDA jda;
    private BotListener listener;
    private final PropertiesManager propertiesManager;
    private CommandManager commandManager;
    private TextChannel textChannel;
    private static MessageEmbed statusEmbed;
    private static ServerStatus serverStatus;
    private static ScheduledExecutorService scheduler;

    public Bot(Logger logger) {
        LOGGER = logger;
        this.propertiesManager = new PropertiesManager(LOGGER);
        int loaded = this.propertiesManager.load(new File("forjyta.properties"));

        if(loaded == 0) {
            Bot.prefix = this.propertiesManager.getProperty("prefix");
            Bot.prefix = Utils.toUTF(Bot.prefix);
            Bot.modrole_id = this.propertiesManager.getProperty("mod_role");
            Bot.modrole_id = Utils.toUTF(Bot.modrole_id);

            this.commandManager = new CommandManager(LOGGER, this.propertiesManager);
            this.listener = new BotListener(this.commandManager, LOGGER);
            this.startBot(propertiesManager.getProperty("api"));
        }
    }

    private void startBot(String key) {
        JDABuilder builder;
        builder = JDABuilder.createDefault(key);
        builder.setActivity(Activity.listening(Bot.getPrefix()));
        builder.addEventListeners(this.listener);

        try {
            jda = builder.build();
            //
            jda.awaitReady();

            LOGGER.info("Successfully logged on Discord!");
            LOGGER.info(jda.getSelfUser().getAsTag() + " here! uwu");


        } catch(LoginException | InterruptedException ex) {
            LOGGER.error("There was an error while logging onto Discord. Is the key up to date?");
            if(propertiesManager.getProperty("api").equals("key")) {
                LOGGER.warn("The api key on the properties file has default value 'key', please update it.");
            }
        }

        // TODO: There should be Bot methods for starting or stopping this scheduler.
        // TODO: There should be a parameter somewhere to dynamically change the period in real time
        // TODO: There should be multiple timers to poll IP, TPS and latency at different rates.
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            LOGGER.info("Updating server status.");
            // TODO: @editDynamicEmbed
            Embeds.editDynamicEmbed(
                    propertiesManager.getProperty("guild"),
                    propertiesManager.getProperty("embed_channel"),
                    propertiesManager.getProperty("embed"),
                    Embeds.statusEmbed(Actions.getMinecraftServerStatus()));
        }, 0,15, TimeUnit.MINUTES);
    }

    public static String getPrefix() {
        return prefix;
    }

    public static void setPrefix(String prefix) {
        Bot.prefix = prefix;
    }

    public void disableBot() {
        scheduler.shutdownNow();
        this.jda.shutdown();
    }

    public static boolean hasPrivileges(Member user) {
        Role r = user.getRoles()
                        .stream()
                        .filter(role -> role.getId()
                                        .equals(Bot.modrole_id))
                        .findFirst().orElse(null);

        return r != null;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(ServerStatus serverStatus) {
        Bot.serverStatus = serverStatus;
    }

    public JDA getJda() {
        return jda;
    }

    // TODO: Make a method for sending messages given the channels ID, then overload it with the news' channel one.
    public void sendToNewsChannel(String message, boolean mention) {
        String mentionstr = "@here";
        String msg = mention? mentionstr+" "+message : message;
        TextChannel channel = this.getJda().getGuildById(propertiesManager.getProperty("guild")).getTextChannelById(
                        propertiesManager.getProperty("status_channel"));
        channel.sendMessage(msg).queue();

        try {
            jda.awaitReady();
        } catch (Exception ex) {}
    }

    public void modifyEmbed(MessageEmbed embed) {
        Embeds.editDynamicEmbed(
                propertiesManager.getProperty("guild"),
                propertiesManager.getProperty("embed_channel"),
                propertiesManager.getProperty("embed"),
                embed);

        try {this.getJda().awaitReady();} catch (Exception ignored) {}
    }
}
