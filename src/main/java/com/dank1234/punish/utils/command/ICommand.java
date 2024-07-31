package com.dank1234.punish.utils.command;

import com.dank1234.punish.Main;
import com.dank1234.punish.utils.MessageType;
import com.dank1234.punish.utils.Utils;
import com.dank1234.punish.utils.data.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ICommand implements Utils, TabCompleter {
    public abstract void init();
    public abstract void execute(CommandSender sender, String[] args);

    private final Database database = Main.getDatabase();
    public Database database() {
        return database;
    }

    private Player player;

    private String[] names;
    private String[][] args;
    private String[] permissions;

    public String[] getNames() {
        return names;
    }
    public String getName(int i) {
        if (names.length >= i) {
            return names[i];
        }
        throw new ArrayIndexOutOfBoundsException("[ERR] Array 'names' is not large enough!");
    }
    public void setNames(String ... names) {
        this.names = names;
    }
    public void setName(int i, String name) {
        this.names[i] = name;
    }

    public String[][] getArgs() {
        return args;
    }
    public String[] getArgNum(int i) {
        return args[i];
    }
    public String getArg(int i, int j) {
        return args[i][j];
    }
    public void setArgs(String[] ... args) {
        this.args = args;
    }
    public void setArgNum(int i, String[] args) {
        this.args[i] = args;
    }
    public void setArg(int i, int j, String arg) {
        this.args[i][j] = arg;
    }

    public String[] getPermissions() {
        return permissions;
    }
    public String getPermission(int i) {
        if (permissions.length >= i) {
            return permissions[i];
        }
        throw new ArrayIndexOutOfBoundsException("[ERR] Array 'permissions' is not large enough!");
    }
    public void setPermissions(String ... permissions) {
        this.permissions = permissions;
    }
    public void setPermission(int i, String permission) {
        this.permissions[i] = permission;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    public Player getPlayer() {
        return player;
    }
    public Player getPlayer(String identifier) {
        Player player;
        if (identifier.length() == 36 && identifier.contains("-")) {
            try {
                UUID uuid = UUID.fromString(identifier);
                player = Bukkit.getPlayer(uuid);
            } catch (IllegalArgumentException e) {
                player = Bukkit.getPlayer(identifier);
            }
        } else {
            player = Bukkit.getPlayer(identifier);
        }
        return player;
    }

    public String getUsage() {
        StringBuilder builder = new StringBuilder("/<");

        for (int i = 0; i < names.length; i++) {
            builder.append(names[i]);
            builder.append((i < names.length - 1) ? " | " : "> ");
        }

        for (String[] arg : args) {
            builder.append("<");
            for (int j = 0; j < arg.length; j++) {
                builder.append(arg[j]);
                builder.append((j < arg.length - 1) ? " " : "");
            }
            builder.append("> ");
        }

        return builder.toString().trim();
    }

    public void sendMessage(String message) {
        this.sendMessage(MessageType.NORMAL, message);
    }
    public void sendMessage(CommandSender sender, String message) {
        this.sendMessage(MessageType.NORMAL, sender, message);
    }
    public void sendMessage(MessageType type, String message) {
        this.sendMessage(type, getPlayer(), message);
    }
    public void sendMessage(MessageType type, CommandSender sender, String message) {
        if (sender==null) {
            return;
        }
        sender.sendMessage(Colour(type + message));
    }

    public Player checkPlayer(Player player) {
        if (player == null) {
            sendMessage(MessageType.ERROR, "Player parsing error!");
            return null;
        }
        return player;
    }
    public boolean isPlayer() {
        return player != null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();
        int level = args.length - 1;

        if (args[level] != null && level < this.args.length) {
            String[] options = this.args[level];
            for (String option : options) {
                if (option != null && option.toLowerCase().startsWith(args[level].toLowerCase())) {
                    completions.add(option);
                }
            }
        }

        return completions;
    }
}