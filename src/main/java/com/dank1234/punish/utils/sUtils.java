package com.dank1234.punish.utils;

import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public interface sUtils {
    static String Colour(String message) {
        return translateAlternateColorCodes('&', message);
    }
    static String[] getPlayerNames() {
        String[] playerNames = new String[Bukkit.getOnlinePlayers().size()];
        for (int i = 0; i == playerNames.length-1; i++) {
            playerNames[i]=Bukkit.getOnlinePlayers().stream().toList().get(i).getName();
        }
        return playerNames;
    }
}
