package me.tyza.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final DateTimeFormatter FORMATTER_HOUR_MINUTES =
                        DateTimeFormatter.ofPattern("HH:mm");

    public static Calendar ldtToCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(
                localDateTime.getYear(),
                localDateTime.getMonthValue()-1,
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond());
        return calendar;
    }

    public static Calendar getLocalNow() {
        return ldtToCalendar(LocalDateTime.now());
    }

    public static Iterable<MatchResult> allMatches(final Pattern p, final CharSequence input) {
        return new Iterable<MatchResult>() {
            public Iterator<MatchResult> iterator() {
                return new Iterator<MatchResult>() {
                    // Use a matcher internally.
                    final Matcher matcher = p.matcher(input);
                    // Keep a match around that supports any interleaving of hasNext/next calls.
                    MatchResult pending;

                    public boolean hasNext() {
                        // Lazily fill pending, and avoid calling find() multiple times if the
                        // clients call hasNext() repeatedly before sampling via next().
                        if (pending == null && matcher.find()) {
                            pending = matcher.toMatchResult();
                        }
                        return pending != null;
                    }

                    public MatchResult next() {
                        // Fill pending if necessary (as when clients call next() without
                        // checking hasNext()), throw if not possible.
                        if (!hasNext()) { throw new NoSuchElementException(); }
                        // Consume pending so next call to hasNext() does a find().
                        MatchResult next = pending;
                        pending = null;
                        return next;
                    }

                    /** Required to satisfy the interface, but unsupported. */
                    public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    public static String GET(String targetURL) {
        String result = null;
        try {
            URL url = new URL(targetURL);
            URLConnection connection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            result = org.apache.commons.io.IOUtils.toString(bufferedReader);
            bufferedReader.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static String getIP() {
        String request = GET("https://www.httpbin.org/ip");
        JsonObject jsonObject = (JsonObject) JsonParser.parseString(request);
        String ip = String.valueOf(jsonObject.get("origin"));
        ip = ip.replaceAll("\"","");
        return ip;
    }

    public static int getPing(String hostname, int iterations) {
        AtomicInteger ping = new AtomicInteger(-1);
        Pattern pattern =  Pattern.compile("(?:M\u00E9dia|Media|Average|\u5E73\u5747) = ([0-9]*)ms");
        StringBuilder pingResult = new StringBuilder();

        String pingCmd = "ping " + hostname + " -n " + iterations;
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(pingCmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                //System.out.println(inputLine);
                pingResult.append(inputLine);
            }
            in.close();
            allMatches(pattern, pingResult).forEach(matchResult ->
                    ping.set(Integer.parseInt(matchResult.group(1)))
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ping.get();
    }

    public static int getPing() {
        return getPing("1.1.1.1", 1);
    }

    // TODO: Test this!

    public static JsonObject loadsJSON(String json) {
        return (JsonObject) JsonParser.parseString(json);
    }

    public static Object loadJSON(File json) {
        try {
            return new Gson().fromJson(Files.newBufferedReader(json.toPath()), Object.class);
        } catch(IOException exception) { return null; }
    }

    public static String toUTF(String string) {
        return new String(string.getBytes(), StandardCharsets.UTF_8);
    }

    public static String fromByteArray(byte[] byte_array) {
        return new String(byte_array, StandardCharsets.UTF_8);
    }

    public static String encodeBase64(String message) {
        return Base64.getEncoder().encodeToString(message.getBytes(StandardCharsets.UTF_8));
    }

    public static String decodeBase64(String encoded) {
        return Utils.fromByteArray(Base64.getDecoder().decode(encoded));
    }

    public static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).anyMatch(inputStr::contains);
    }

    public static long stringToLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ex) {
            System.err.println("Long not found in parsed string.");
            return -1;
        }
    }

    public static int getLocalHour() {
        return getLocalNow().get(Calendar.HOUR_OF_DAY);
    }

    public static String getLocalFormattedTime() {
        return FORMATTER_HOUR_MINUTES.format(LocalTime.now());
    }

    public static String getLocalHourOffset() {
        long offset = getLocalNow().get(Calendar.ZONE_OFFSET);
        offset = (long) (offset / 3.6E6);
        char sign = (int) Math.signum(offset) == -1? '-' : '+';

        return "GMT"+sign+Math.abs(offset);
    }

    public static Calendar setCalendarTime(Calendar cal, int hour, int minute, int second) {
        cal.set(Calendar.HOUR_OF_DAY  , hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        return cal;
    }

    public static Calendar parseHourMinutes(String string) {
        LocalTime dateTime = LocalTime.parse(string,FORMATTER_HOUR_MINUTES);
        return setCalendarTime(getLocalNow(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond());
    }

    public static String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    public static String padLeftZeros(int inputInt, int length) {
        return padLeftZeros(String.valueOf(inputInt), length);
    }

    public static String getFormattedCalendar(Calendar now) {
        return  padLeftZeros(now.get(Calendar.HOUR_OF_DAY),2) + ":" +
                padLeftZeros(now.get(Calendar.MINUTE),2) + ":" +
                padLeftZeros(now.get(Calendar.SECOND),2 );
    }

    /* ------------------------------------------------------------------------------------ */

    public static String pingToEmoji(long ping, int min, int max) {
        String emoji;

        if (ping >= max) { emoji = "\uD83D\uDD34"; }
        else if(ping <= min) { emoji = "\uD83D\uDFE2"; }
        else { emoji = "\uD83D\uDFE1"; }

        return emoji;
    }

    public static String pingToEmoji(long ping) {
        return pingToEmoji(ping, 400, 700);
    }

    public static String pingToEmoji(String formattedPing, int min, int max) {
        if(formattedPing.contains("*")) formattedPing = formattedPing.replace("*","");
        return pingToEmoji(Long.parseLong(formattedPing), min, max);
    }

    public static String pingToEmoji(String formattedPing) {
        if(formattedPing.contains("*")) formattedPing = formattedPing.replace("*","");
        return pingToEmoji(Long.parseLong(formattedPing));
    }

    /* ------------------------------------------------------------------------------------ */

    public static int pingToColor(long ping, int min, int max) {
        int color;

        /* #79b15a #f5900c #de2e43 */

        if (ping >= max) { color = 0xDE2E43; }
        else if(ping <= min) { color = 0x79B15A; }
        else { color = 0xF5900C; }

        return color;
    }

    public static int pingToColor(long ping) {
        return pingToColor(ping, 400, 700);
    }

    public static int pingToColor(String formattedPing, int min, int max) {
        if(formattedPing.contains("*")) formattedPing = formattedPing.replace("*","");
        return pingToColor(Long.parseLong(formattedPing), min, max);
    }

    public static int pingToColor(String formattedPing) {
        if(formattedPing.contains("*")) formattedPing = formattedPing.replace("*","");
        return pingToColor(Long.parseLong(formattedPing));
    }

    /* ------------------------------------------------------------------------------------ */

    public static String tpsToEmoji(double tps) {
        String emoji;

        if (tps <= 7) { emoji = "\uD83D\uDD34"; }
        else if(tps >= 15) { emoji = "\uD83D\uDFE2"; }
        else { emoji = "\uD83D\uDFE1"; }

        return emoji;
    }

    public static String tpsToEmoji(double tps, int min, int max) {
        String emoji;

        if (tps <= min) { emoji = "\uD83D\uDD34"; }
        else if(tps >= max) { emoji = "\uD83D\uDFE2"; }
        else { emoji = "\uD83D\uDFE1"; }

        return emoji;
    }

    public static String tpsToEmoji(String formattedTPS) {
        if(formattedTPS.contains("*")) formattedTPS = formattedTPS.replace("*","");
        return tpsToEmoji(Double.parseDouble(formattedTPS));
    }

    public static String tpsToEmoji(String formattedTPS, int min, int max) {
        if(formattedTPS.contains("*")) formattedTPS = formattedTPS.replace("*","");
        return tpsToEmoji(Double.parseDouble(formattedTPS), min, max);

    }

    /* ---------------------------------------------------------------------------------------- */

    public static int tpsToColor(double tps) {
        int color;

        /* #79b15a #f5900c #de2e43 */

        if (tps <= 7) { color = 0xDE2E43; }
        else if(tps >= 15) { color = 0x79B15A; }
        else { color = 0xF5900C; }

        return color;
    }

    public static int tpsToColor(double tps, int min, int max) {
        int color;

        /* #79b15a #f5900c #de2e43 */

        if (tps <= min) { color = 0xDE2E43; }
        else if(tps >= max) { color = 0x79B15A; }
        else { color = 0xF5900C; }

        return color;
    }

    public static int tpsToColor(String formattedTPS) {
        if(formattedTPS.contains("*")) formattedTPS = formattedTPS.replace("*","");
        return pingToColor(Long.parseLong(formattedTPS));
    }

    public static int tpsToColor(String formattedTPS, int min, int max) {
        if(formattedTPS.contains("*")) formattedTPS = formattedTPS.replace("*","");
        return pingToColor(Long.parseLong(formattedTPS), min, max);
    }

    /* ------------------------------------------------------------------------------------ */

    public static String hourOfDayToEmoji(int hour_of_day) {
        return switch (hour_of_day%12) {
            case 11 -> "\uD83D\uDD5A";
            case 10 -> "\uD83D\uDD59";
            case 9  -> "\uD83D\uDD58";
            case 8  -> "\uD83D\uDD57";
            case 7  -> "\uD83D\uDD56";
            case 6  -> "\uD83D\uDD55";
            case 5  -> "\uD83D\uDD54";
            case 4  -> "\uD83D\uDD53";
            case 3  -> "\uD83D\uDD52";
            case 2  -> "\uD83D\uDD51";
            case 1  -> "\uD83D\uDD50";
            case 0  -> "\uD83D\uDD5B";
            default -> "Error";
        };
    }

    public static String emojiToHex(char c) {
        return "U+"+Integer.toHexString(c).toUpperCase();
    }
}
