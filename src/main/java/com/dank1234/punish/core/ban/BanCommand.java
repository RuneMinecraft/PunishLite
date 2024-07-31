package com.dank1234.punish.core.ban;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import com.dank1234.punish.utils.MessageType;
import com.dank1234.punish.utils.annotations.Cmd;
import com.dank1234.punish.utils.command.ICommand;
import com.dank1234.punish.utils.data.database.Database;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Cmd(disabled = false)
public class BanCommand extends ICommand {
    private Punishment punishment;

    @Override
    public void init() {
        setNames("ban");
        setArgs(sUtils.getPlayerNames(),
                new String[]{"<Reason>"},
                new String[]{"<Length>"},
                new String[]{"<Modifier>"}
        );
        setPermissions("punish.type.ban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setPlayer((Player) sender);

        if (args.length == 0) {
            super.sendMessage(MessageType.ERROR, "Invalid Arguments! /ban <Player> <Reason> <Length> <Modifier>");
            return;
        }

        UUID player = database().getUUIDByName(args[0]);
        String reason = "No reason provided!";
        long length = -1L;
        boolean modifier = false;

        if (args.length >= 2) {
            int lastIndex = args.length - 1;

            if (args[lastIndex].equals("-s")) {
                modifier = true;
                lastIndex--;
            }

            if (lastIndex >= 1 && isValidTimeFormat(args[lastIndex])) {
                length = parseTime(args[lastIndex]);
                lastIndex--;
            }

            if (lastIndex > 0) {
                reason = reason(args, 1, lastIndex + 1);
            } else {
                if (isValidTimeFormat(args[1])) {
                    length = parseTime(args[1]);
                } else {
                    reason = reason(args, 1, args.length);
                }
            }
        }

        if (database().isPlayerPunished(Type.BAN, player)) {
            Punishment ban = database().getLatestPunishment(Type.BAN, player);
            if (ban.active()) {
                sendMessage("&aRemoved last ban for the player, "+database().getNameByUUID(player));
                database().updatePunishment(ban.banId(), false);
            }
            return;
        }

        this.kickPlayer(player, reason, length, modifier);
        this.punishment.punish();
    }

    private void kickPlayer(UUID player, String reason, long length, boolean modifier) {
        this.punishment = new Punishment(Type.BAN, player, super.getPlayer().getUniqueId(), reason, length, modifier, true);
        if (Bukkit.getPlayer(player) != null) {
            StringBuilder message = new StringBuilder();
            message.append("&cYou have been banned!\n&f")
                    .append(this.date(punishment.startTime()))
                    .append("\n\n")
                    .append("&cPunisher: &f").append(database().getNameByUUID(punishment.punisher())).append("\n")
                    .append("&cReason: &f").append(punishment.reason()).append("\n")
                    .append("&cLength: &f").append(punishment.length() == -1L ? "Permanent" : formatDuration(punishment.length())).append("\n")
                    .append("&cEnds on: &f").append(punishment.length() != -1L ? date(punishment.endTime()) : "Never").append("\n");
            if (punishment.length() != -1L) {
                message.append("&cEnds in: &f").append(punishment.formatDuration(punishment.endTime() - System.currentTimeMillis())).append("\n");
            }

            message.append("\n&f").append(punishment.banId());
            Bukkit.getPlayer(player).kickPlayer(Colour(message.toString()));
        }
    }
}