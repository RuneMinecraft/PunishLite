package com.dank1234.punish.core.mute;

import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import com.dank1234.punish.utils.MessageType;
import com.dank1234.punish.utils.annotations.Cmd;
import com.dank1234.punish.utils.command.ICommand;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@Cmd(disabled = false)
public class MuteCommand extends ICommand {
    private Punishment punishment;
    @Override
    public void init() {
        setNames("mute");
        setArgs(sUtils.getPlayerNames(),
                new String[]{"<Reason>"},
                new String[]{"<Length>"},
                new String[]{"<Modifier>"}
        );
        setPermissions("punish.type.mute");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setPlayer((Player) sender);

        if (args.length == 0) {
            super.sendMessage(MessageType.ERROR, "Invalid Arguments! /mute <Player> <Reason> <Length> <Modifier>");
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

        this.mutePlayer(player, reason, length, modifier);
        this.punishment.punish();
    }

    private void mutePlayer(UUID player, String reason, long length, boolean modifier) {
        this.punishment = new Punishment(Type.MUTE, player, super.getPlayer().getUniqueId(), reason, length, modifier, true);
        sendMessage(Bukkit.getPlayer(player),
                "&4&lMUTE &8» &cYou have been muted by &4"+super.getPlayer().getName()+"&c for &4"+reason+"\n"+
                        (
                                length != -1L
                                ? "&cThis mute will expire in "+formatDuration(length)+"."
                                : "&cThis mute will never expire."
                        )
        );
    }
}
