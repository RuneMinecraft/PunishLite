package com.dank1234.punish.utils.data;

import com.dank1234.punish.Main;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

public class NamespaceKey {
    static Map<String, NamespacedKey> keys = new HashMap<>();
    public static NamespacedKey get(String key) {
        return keys.computeIfAbsent(key, a -> new NamespacedKey(Main.get(), key));
    }
}
