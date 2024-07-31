package com.dank1234.punish.core.mute;

import com.dank1234.punish.Main;
import com.dank1234.punish.core.Punishment;
import com.dank1234.punish.core.Type;
import com.dank1234.punish.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteEvent implements Listener, Utils {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        if (Main.getDatabase().isPlayerPunished(Type.BAN, e.getPlayer().getUniqueId())) {
            Punishment punishment = Main.getDatabase().getLatestPunishment(Type.MUTE, e.getPlayer().getUniqueId());
            e.setCancelled(true);
            e.getPlayer().sendMessage(Colour("&4&lMUTE &8» &cYou are currently muted for &4"+punishment.reason()));
        }
    }
}
