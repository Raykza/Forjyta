package me.tyza.mod.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class HourArgument implements ArgumentType<Integer> {

    private static final SimpleCommandExceptionType ERROR_INVALID_UNIT =
            new SimpleCommandExceptionType(new TranslatableComponent("argument.time.invalid_unit"));
    private static final Collection<String> EXAMPLES = Arrays.asList("0h", "0m", "0s", "0");
    private static final Object2IntMap<String> UNITS = new Object2IntOpenHashMap<>();

    public static HourArgument hour() {
        return new HourArgument();
    }

    @Override
    public Integer parse(StringReader reader) throws CommandSyntaxException {
        int input = reader.readInt();
        String rawString = reader.readUnquotedString();

        int unit = UNITS.getOrDefault(rawString, 0);

        if (unit == 0) { throw ERROR_INVALID_UNIT.create(); }
        else
        {
            int totalInput = input * unit;
            if (totalInput < 0) { throw ERROR_INVALID_UNIT.create(); }
            else { return totalInput; }
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringreader = new StringReader(builder.getRemaining());

        try {
            stringreader.readInt();
        } catch (CommandSyntaxException commandsyntaxexception) {
            return builder.buildFuture();
        }

        return SharedSuggestionProvider.suggest(
                UNITS.keySet(),
                builder.createOffset(builder.getStart() + stringreader.getCursor()
                ));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    static {
        UNITS.put("h",3600);
        UNITS.put("m",60);
        UNITS.put("s", 1);
        UNITS.put("", 1);
    }
}
