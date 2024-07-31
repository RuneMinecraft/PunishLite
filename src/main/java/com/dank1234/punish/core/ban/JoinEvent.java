package com.dank1234.punish.core.ban;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import com.dank1234.punish.utils.Utils;
import com.dank1234.punish.utils.data.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinEvent implements Listener, Utils {
    private final Database database = Main.getDatabase();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Main.getDatabase().isPlayerPunished(Type.BAN, e.getPlayer().getUniqueId())) {
            Punishment punishment = Main.getDatabase().getLatestPunishment(Type.BAN, e.getPlayer().getUniqueId());
            kickPlayer(e.getPlayer().getUniqueId(), punishment);
        }
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (Main.getDatabase().isPlayerPunished(Type.BAN, e.getPlayer().getUniqueId())) {
            e.setQuitMessage("");
        }
    }

    private void kickPlayer(UUID player, Punishment punishment) {
        if (Bukkit.getPlayer(player) != null) {
            StringBuilder message = new StringBuilder();
            message.append(Colour("&cYou have been banned!\n&f"))
                    .append(this.date(punishment.startTime()))
                    .append("\n\n")
                    .append("&cPunisher: &f").append(database.getNameByUUID(punishment.punisher())).append("\n")
                    .append("&cReason: &f").append(punishment.reason()).append("\n")
                    .append("&cLength: &f").append(punishment.length() == -1L ? "Permanent" : formatDuration(punishment.length())).append("\n")
                    .append("&cEnds on: &f").append(date(punishment.endTime())).append("\n");
            if (punishment.length() != -1L) {
                message.append("&cEnds in: &f").append(punishment.formatDuration(punishment.endTime() - System.currentTimeMillis())).append("\n");
            }

            message.append("\n&f").append(punishment.banId());
            Bukkit.getPlayer(player).kickPlayer(message.toString());
        }
    }
}
