package com.dank1234.punish.core.kick;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import com.dank1234.punish.utils.command.ICommand;
import com.dank1234.punish.utils.MessageType;
import com.dank1234.punish.utils.annotations.Cmd;
import com.dank1234.punish.utils.data.database.Database;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@Cmd(disabled = false)
public class KickCommand extends ICommand {
    private final Database database = Main.getDatabase();
    private Punishment punishment;

    @Override
    public void init() {
        super.setNames("kick");
        super.setArgs(sUtils.getPlayerNames(),
                new String[]{
                        "&7&o<Reason>"
                }, new String[]{
                        "&7&o<Modifier>"
                }
        );
        super.setPermissions("punish.type.kick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setPlayer((Player) sender);

        if (args.length == 0) {
            super.sendMessage(MessageType.ERROR, "Invalid Arguments! /kick <Player> <Reason> <Modifier>");
            return;
        }
        Player player = super.getPlayer(args[0]);
        String reason = "No reason provided!";
        boolean modifier = false;

        if (!(args.length == 1)){
            String lastArg = args[args.length - 1];
            if (lastArg.equals("-s")) {
                modifier = true;
                reason = reason(args, args.length - 1);
            } else {
                reason = reason(args, args.length);
            }
        }

        if (player == null) {
            sendMessage(MessageType.ERROR, "Cannot kick a player that is not online!");
            return;
        }

        this.kickPlayer(player, reason, modifier);
        this.punishment.punish();
    }

    private void kickPlayer(Player player, String reason, boolean modifier) {
        this.punishment = new Punishment(Type.KICK, player.getUniqueId(), super.getPlayer().getUniqueId(), reason, 0L, modifier, true);
        player.kickPlayer(Colour(
                "&cYou have been kicked!" + "\n&f" +
                this.date(punishment.startTime()) + "\n\n" +
                "&cPunisher: &f"+database.getNameByUUID(punishment.punisher())+"\n"+
                "&cReason: &f"+punishment.reason()+"\n"+
                "\n"+
                "&f"+punishment.banId()
        ));
    }

    private String reason(String[] args, int end) {
        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < end; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        return reasonBuilder.toString().trim();
    }
}
