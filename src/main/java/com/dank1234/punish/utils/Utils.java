package com.dank1234.punish.utils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public interface Utils {
    default String Colour(String message) {
        return translateAlternateColorCodes('&', message);
    }
    default String generateBanId() {
        int length = 10;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new SecureRandom();
        StringBuilder banId = new StringBuilder("#");
        for (int i = 0; i < length; i++) {
            banId.append(characters.charAt(random.nextInt(characters.length())));
        }
        return banId.toString();
    }
    default String reason(String[] args, int start, int end) {
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = start; i < end; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        return reasonBuilder.toString().trim();
    }
    default String date(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(millis));
    }
    default String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30;
        long years = days / 365;

        if (years > 0) return years + " year" + (years > 1 ? "s" : "");
        if (months > 0) return months + " month" + (months > 1 ? "s" : "");
        if (weeks > 0) return weeks + " week" + (weeks > 1 ? "s" : "");
        if (days > 0) return days + " day" + (days > 1 ? "s" : "");
        if (hours > 0) return hours + " hour" + (hours > 1 ? "s" : "");
        if (minutes > 0) return minutes + " minute" + (minutes > 1 ? "s" : "");
        return seconds + " second" + (seconds > 1 ? "s" : "");
    }
    default boolean isValidTimeFormat(String input) {
        return input.matches("\\d+(?:[smhdwy]|mo)");
    }
    default long parseTime(String input) {
        long time = Long.parseLong(input.substring(0, input.length() - 1));
        return switch (input.charAt(input.length() - 1)) {
            case 's' -> time * 1000;
            case 'm' -> time * 1000 * 60;
            case 'h' -> time * 1000 * 60 * 60;
            case 'd' -> time * 1000 * 60 * 60 * 24;
            case 'w' -> time * 1000 * 60 * 60 * 24 * 7;
            case 'o' -> input.charAt(input.length()-2)=='m' ? time * 100 * 60 * 60 * 24 * 30 : null;
            case 'y' -> time * 1000 * 60 * 60 * 24 * 365;
            default -> throw new IllegalArgumentException("Invalid time format: " + input);
        };
    }
}
