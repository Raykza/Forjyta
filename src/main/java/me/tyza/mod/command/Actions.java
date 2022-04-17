package me.tyza.mod.command;

import me.tyza.utils.ServerStatus;
import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;

public class Actions {
    public static MinecraftServer getServerInstance() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static void haltServer(MinecraftServer server) {
        server.halt(false);
    }

    public static void closeServer(MinecraftServer server) {
        server.close();
    }

    public static void messagePlayer(Player player, String message) {
        player.sendMessage(new TextComponent(message), Util.NIL_UUID);
    }

    public static void messagePlayers(PlayerList playerList, String message) {
        playerList.getPlayers().forEach((player -> {
            messagePlayer(player, message);
        }));
    }

    public static ArrayList<SimpleDimension> getDimensions(MinecraftServer server) {
        ArrayList<SimpleDimension> simpleDimensions = new ArrayList<>();
        server.getAllLevels().forEach(dimension -> {
            simpleDimensions.add(new SimpleDimension(dimension));
        });

        return simpleDimensions;
    }

    public static PlayerList getPlayerList(MinecraftServer server) {
        return server.getPlayerList();
    }

    public static ServerStatus getMinecraftServerStatus() {
        return new ServerStatus(Actions.getServerInstance());
    }
}
