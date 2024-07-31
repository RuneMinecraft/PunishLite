package com.dank1234.punish.core.history;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.utils.data.database.Database;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class HistoryUtils {
    private final Map<UUID, List<Punishment>> historyMap = new HashMap<>();
    private final Database database = Main.getDatabase();

    public void update(UUID player) {
        historyMap.computeIfAbsent(player, list -> database.getPunishments(player));
    }
    public List<Punishment> get(UUID player, int limit) {
        List<Punishment> punishList = historyMap.get(player);
        List<Punishment> tempList = new ArrayList<>();
        for (int i = 0; i < limit-1; i++) {
            tempList.add(punishList.get(i));
        }
        return tempList;
    }
    public List<Punishment> getAll(UUID player) {
        return historyMap.get(player);
    }

    public String getFormatted(UUID player, int limit) {
        StringBuilder builder = new StringBuilder("&aHistory for &f"+database.getNameByUUID(player)+"&a:\n");
        int i = 0;
        for (Punishment punishment : getAll(player)) {
            if (i==limit) {
                break;
            }

            builder.append("&a<===> | &f").append(formatDuration(System.currentTimeMillis() - punishment.startTime())).append(" ago").append("&a | <===>").append('\n');
            builder.append(" &f| &aType: &c").append(punishment.type()).append('\n');
            builder.append(" &f| &aPunisher: &c").append(database.getNameByUUID(punishment.punisher())).append('\n');
            builder.append(" &f| &aReason: &c").append(punishment.reason()).append('\n');
            builder.append(" &f| &aLength: &c").append(punishment.length() != -1L ? formatDuration(punishment.length()) : "Permanent").append('\n');
            builder.append(" &f| &aExpired: &c").append((!punishment.active() ? "True" : "False")).append("\n\n");

            i+=1;
        }
        return builder.toString();
    }
    public String formatDuration(long durationMillis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis) % 24;
        long days = TimeUnit.MILLISECONDS.toDays(durationMillis) % 7;
        long weeks = TimeUnit.MILLISECONDS.toDays(durationMillis) / 7 % 4;
        long months = TimeUnit.MILLISECONDS.toDays(durationMillis) / 30 % 12;
        long years = TimeUnit.MILLISECONDS.toDays(durationMillis) / 365;

        StringBuilder durationString = new StringBuilder();
        if (years > 0)
            durationString.append(years).append(years == 1 ? " year, " : " years, ");
        if (months > 0)
            durationString.append(months).append(months == 1 ? " month, " : " months, ");
        if (weeks > 0)
            durationString.append(weeks).append(weeks == 1 ? " week, " : " weeks, ");
        if (days > 0)
            durationString.append(days).append(days == 1 ? " day, " : " days, ");
        if (hours > 0)
            durationString.append(hours).append(hours == 1 ? " hour, " : " hours, ");
        if (minutes > 0)
            durationString.append(minutes).append(minutes == 1 ? " minute, " : " minutes, ");
        durationString.append(seconds).append(seconds == 1 ? " second" : " seconds");

        return durationString.toString();
    }
}