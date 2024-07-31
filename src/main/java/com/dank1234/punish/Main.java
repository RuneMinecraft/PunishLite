package com.dank1234.punish;

import com.dank1234.punish.core.EntryListener;
import com.dank1234.punish.core.ban.BanCommand;
import com.dank1234.punish.core.ban.JoinEvent;
import com.dank1234.punish.core.ban.UnbanCommand;
import com.dank1234.punish.core.history.HistoryCommand;
import com.dank1234.punish.core.history.HistoryUtils;
import com.dank1234.punish.core.history.PruneHistoryCommand;
import com.dank1234.punish.core.kick.KickCommand;
import com.dank1234.punish.core.mute.MuteCommand;
import com.dank1234.punish.core.mute.MuteEvent;
import com.dank1234.punish.core.warn.WarnCommand;
import com.dank1234.punish.utils.command.Register;
import com.dank1234.punish.utils.data.database.Database;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Main extends JavaPlugin {
    private final Register register = new Register();

    private static Main instance;
    private static Database database;

    public static Database getDatabase() {
        return database;
    }
    public HistoryUtils utils;

    public static Main get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance=this;
        database = new Database();
        utils = new HistoryUtils();

        register.registerCommands(
                new KickCommand(), new HistoryCommand(),
                new BanCommand(), new MuteCommand(),
                new WarnCommand(), new PruneHistoryCommand(),
                new UnbanCommand()
        );
        register.registerListeners(new JoinEvent(), new EntryListener(), new MuteEvent());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return register.register(sender, command, label, args);
    }
}