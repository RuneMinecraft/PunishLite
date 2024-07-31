package com.dank1234.punish.core;

import com.dank1234.punish.Main;
import com.dank1234.punish.utils.data.database.Database;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EntryListener implements Listener {
    private final Database database = Main.getDatabase();
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (database.userExists(e.getPlayer().getUniqueId())) {
            return;
        }

        database.insertUser(e.getPlayer());
    }
}
