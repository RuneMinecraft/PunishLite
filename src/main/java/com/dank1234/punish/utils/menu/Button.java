package com.dank1234.punish.utils.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

public record Button(
        Material material,
        String name,
        String[] lore,
        BiConsumer<Player, InventoryClickEvent> action
){}