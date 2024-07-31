package com.dank1234.punish.utils.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuHolder implements InventoryHolder {
    private Player player;
    private Inventory inv;

    private MenuHolder(Player player) {
        this.player = player;
    }

    public static MenuHolder of(Player player) {
        return new MenuHolder(player);
    }

    public MenuHolder set(Inventory inv) {
        this.inv = inv;
        return this;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inv;
    }

    public Player getPlayer() {
        return this.player;
    }
}