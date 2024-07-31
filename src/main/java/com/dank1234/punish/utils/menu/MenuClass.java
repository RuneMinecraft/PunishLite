package com.dank1234.punish.utils.menu;

import org.jetbrains.annotations.Nullable;

public abstract class MenuClass extends Menu {
    public MenuClass(@Nullable MenuHolder holder, String name, int rows) {
        super(holder, name, rows);
    }

    public abstract void init();
}
