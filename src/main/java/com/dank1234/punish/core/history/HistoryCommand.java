package com.dank1234.punish.core.history;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.utils.annotations.Cmd;
import com.dank1234.punish.utils.command.ICommand;
import com.dank1234.punish.utils.data.database.Database;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

@Cmd(disabled = false)
public class HistoryCommand extends ICommand {
    @Override
    public void init() {
        setNames("hist", "history");
        setArgs(sUtils.getPlayerNames(), new String[]{"&7&o<Limit>"});
        setPermissions("punish.history");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Usage: /hist <player> <limit>");
            return;
        }
        super.setPlayer((Player) sender);
        UUID player = database().getUUIDByName(args[0]);

        Main.get().utils.update(player);
        sendMessage(Main.get().utils.getFormatted(player, 10));
    }
}
