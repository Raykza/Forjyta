package me.tyza.mod.command;

import me.tyza.utils.Utils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class SimpleDimension {
    private final double TPS;
    private final String name;
    private final double tickTime;

    public SimpleDimension(String name, double tickTime, double TPS) {
        this.name = name;
        this.tickTime = tickTime;
        this.TPS = TPS;
    }

    public SimpleDimension(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        this.name = formatName(dimension.location().toString());

        long[] tickTimes = level.getServer().getTickTime(dimension);
        if(tickTimes == null) tickTimes = new long[] {0};

        this.tickTime = mean(tickTimes) * 1.0E-06;
        this.TPS = Math.min(1000.0 / this.tickTime, 20.0);
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for (long v : values)
            sum += v;
        return sum / values.length;
    }

    private static String formatName(String name) {
        name = name.split(":")[1];
        name = StringUtils.capitalize(name);
        name = name.replaceAll("_"," ");
        return name;
    }

    public String getName() {
        return name;
    }

    public double getTickTime() {
        return tickTime;
    }

    public double getTPS() {
        return TPS;
    }

    public MessageEmbed.Field toEmbedField(boolean inline) {
        String content;
        content = Utils.tpsToEmoji(this.TPS) + "  " + this.TPS + " TPS";
        return new MessageEmbed.Field("⬛ "+ this.name, content, inline);
    }
}
