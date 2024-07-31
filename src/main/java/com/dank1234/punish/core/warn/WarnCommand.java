package com.dank1234.punish.core.warn;

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
public class WarnCommand extends ICommand {
    private final Database database = Main.getDatabase();
    private Punishment punishment;

    @Override
    public void init() {
        super.setNames("warn");
        super.setArgs(sUtils.getPlayerNames(),
                new String[]{
                        "&7&o<Reason>"
                }, new String[]{
                        "&7&o<Modifier>"
                }
        );
        super.setPermissions("punish.type.warn");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setPlayer((Player) sender);

        if (args.length == 0) {
            super.sendMessage(MessageType.ERROR, "Invalid Arguments! /kick <Player> <Reason> <Modifier>");
            return;
        }

        UUID player = database.getUUIDByName(args[0]);
        String reason = "No reason provided!";
        boolean modifier = false;

        if (!(args.length == 1)){
            String lastArg = args[args.length - 1];
            if (lastArg.equals("-s")) {
                modifier = true;
                reason = reason(args, 1,args.length - 1);
            } else {
                reason = reason(args, 1, args.length);
            }
        }

        this.warnPlayer(player, reason, modifier);
        this.punishment.punish();
    }

    private void warnPlayer(UUID player, String reason, boolean modifier) {
        this.punishment = new Punishment(Type.WARN, player, super.getPlayer().getUniqueId(), reason, 6048^11, modifier, true);
        sendMessage((Bukkit.getPlayer(player) != null ? Bukkit.getPlayer(player) : null), "&4&lWARN &8» &cYou have been warned by &4"+database.getNameByUUID(punishment.punisher())+"&c for &4"+reason+"&c!\nExpires in: ");
    }
}
