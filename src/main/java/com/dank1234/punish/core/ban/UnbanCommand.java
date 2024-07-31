package com.dank1234.punish.core.ban;

import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import com.dank1234.punish.utils.annotations.Cmd;
import com.dank1234.punish.utils.command.ICommand;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.command.CommandSender;

import java.util.UUID;

@Cmd(disabled = false)
public class UnbanCommand extends ICommand {
    @Override
    public void init() {
        setNames("unban");
        setArgs(
                sUtils.getPlayerNames(),
                new String[]{"<Reason>"},
                new String[]{"<Modifier>"}
        );
        setPermissions("punish.type.unban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Usage: /unban <player> <reason> <modifier>");
            return;
        }

        String playerName = args[0];
        UUID playerUUID = database().getUUIDByName(playerName);

        Punishment latestBan = database().getLatestPunishment(Type.BAN, playerUUID);

        if (latestBan == null) {
            sender.sendMessage("&a"+playerName+"&f is not banned.");
            return;
        }

        database().updatePunishment(latestBan.banId(), false);
        new Punishment(
                latestBan.banId(), Type.UNBAN,
                latestBan.player(), latestBan.punisher(),
            "No Reason Provided!", -1L,
                Long.MAX_VALUE, true,
                true
        ).punish();
    }
}