package com.dank1234.punish.core.history;

import com.dank1234.punish.utils.annotations.Cmd;
import com.dank1234.punish.utils.command.ICommand;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Cmd(disabled = false)
public class PruneHistoryCommand extends ICommand {
    @Override
    public void init() {
        setNames("prune", "prunehist", "prunehistory");
        setArgs(sUtils.getPlayerNames(), new String[]{"<Time>"});
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        super.setPlayer((Player) sender);
        if (args.length != 2) {
            return;
        }

        Player player = Bukkit.getPlayer(database().getUUIDByName(args[0]));
        String time = args[0];
        long timeMillis = formatDuration(time);

        database().prunePunishments(player.getUniqueId(), timeMillis);
    }

    private long formatDuration(String s) {
        long multiplier = 1;
        int displacement = 1;
        switch (s.charAt(s.length()-1)) {
            case 's' -> multiplier = 1000L;
            case 'm' -> multiplier = 1000L * 60L;
            case 'h' -> multiplier = 1000L * 60L * 60L;
            case 'd' -> multiplier = 1000L * 60L * 60L * 24L;
            case 'w' -> multiplier = 1000L * 60L * 60L * 24L * 7L;
            case 'o' -> {
                if ((s.charAt(s.length() - 2) == 'm')) {
                    multiplier = 1000L * 60L * 60L * 24L * 30L;
                    displacement = 2;
                }
            }
            case 'y' -> multiplier = 1000L * 60L * 60L * 24L * 365L;
        }

        long providedNum = Long.parseLong(s.substring(0,s.length()-displacement));
        return providedNum*multiplier;
    }
}
