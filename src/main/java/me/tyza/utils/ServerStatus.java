package me.tyza.utils;

import com.google.gson.Gson;
import me.tyza.mod.command.Actions;
import me.tyza.mod.command.SimpleDimension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.PlayerList;

import java.util.ArrayList;

public class ServerStatus {
    private String ip;
    private long latency;
    private ArrayList<SimpleDimension> dimensions;
    private PlayerList playerList;

    @Deprecated
    public ServerStatus() {
        this.ip = Utils.getIP();
        this.latency = Utils.getPing("1.1.1.1",1);
    }

    @Deprecated
    public ServerStatus(String ip, int ping) {
        this.ip = ip;
        this.latency = ping;
    }

    public ServerStatus(MinecraftServer server) {
        this.dimensions = Actions.getDimensions(server);
        this.playerList = Actions.getPlayerList(server);
    }

    public ArrayList<SimpleDimension> getDimensions() {
        return dimensions;
    }

    public ServerStatus setDimensions(ArrayList<SimpleDimension> dimensions) {
        this.dimensions = dimensions;
        return this;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getLatency() {
        return latency;
    }

    public void setLatency(long latency) {
        this.latency = latency;
    }

    public ServerStatus fetchInet() {
        this.ip = Utils.getIP();
        this.latency = Utils.getPing();
        return this;
    }

    public String asJSONstring() {
        return new Gson().toJson(this);
    }
    public PlayerList getPlayerList() {
        return playerList;
    }

    public ServerStatus setPlayerList(PlayerList playerList) {
        this.playerList = playerList;
        return this;
    }
}
