package com.dank1234.punish.utils.menu;

import com.dank1234.punish.Main;
import com.dank1234.punish.utils.Utils;
import com.dank1234.punish.utils.sUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Menu implements Utils, Listener {
    private final Main plugin = Main.get();

    @NotNull private final Inventory inv;
    @Nullable private final Button[] items;
    @Nullable private final MenuHolder holder;
    @Nullable private final ItemStack filler;

    @NotNull private final String name;
    private final int rows;
    private final int size;

    public Main plugin() {
        return plugin;
    }
    public int size() {
        return size;
    }
    public int rows() {
        return rows;
    }
    public @NotNull String name() {
        return name;
    }
    @Nullable public ItemStack filler() {
        return filler;
    }
    @Nullable public MenuHolder holder() {
        return holder;
    }
    public @NotNull Inventory inv() {
        return inv;
    }
    @Nullable public Button[] items() {
        return items;
    }

    public Menu(@Nullable MenuHolder holder, String name, int rows) {
        this.holder = holder;
        this.name = Colour(name);
        this.rows = rows;

        this.size = this.rows * 9;
        this.items = new Button[this.size];
        this.filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);

        this.inv = Bukkit.createInventory(this.holder, this.size, this.name);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void add(int place, Button button) {
        assert items != null;
        items[place] = button;
    }

    private void initInventory() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                Button btn = items[i];
                ItemStack stack = new ItemStack(btn.material());
                ItemMeta meta = stack.getItemMeta();

                meta.setDisplayName(sUtils.Colour(btn.name()));
                List<String> lore = Arrays.stream(btn.lore())
                        .map(sUtils::Colour)
                        .toList();
                meta.setLore(lore);
                stack.setItemMeta(meta);
                inv.setItem(i, stack);
            } else {
                inv.setItem(i, filler);
            }
        }
    }

    public void build() {
        if (holder == null) {
            throw new NullPointerException("Attempted to open an inventory for an empty holder!");
        }
        this.build(this.holder);
    }
    public void build(Player player) {
        this.initInventory();
        player.openInventory(this.inv);
    }
    public void build(MenuHolder holder) {
        this.build(holder.getPlayer());
    }

    @EventHandler private void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(inv)) {
            return;
        }
        event.setCancelled(true);

        int slot = event.getSlot();
        if (slot < 0 || slot >= Objects.requireNonNull(items).length) {
            return;
        }

        Button button = items[slot];
        if (button != null) {
            Player player = (Player) event.getWhoClicked();
            button.action().accept(player, event);
        }
    }
}