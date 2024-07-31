package com.dank1234.punish.utils.command;

import com.dank1234.punish.Main;
import com.dank1234.punish.utils.MessageType;
import com.dank1234.punish.utils.data.NamespaceKey;
import com.dank1234.punish.utils.annotations.Cmd;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Register {
    private final Map<String, ICommand> commandHandlers = new HashMap<>();
    public void registerCommands(ICommand ... commands) {
        List<ICommand> cmdList = new ArrayList<>();

        for (ICommand cmd : commands) {
            cmd.init();
            if (!cmd.getName(0).isEmpty()) {
                for (int i = 0; i < cmd.getNames().length; i++) {
                    commandHandlers.put(cmd.getName(i), cmd);
                    cmdList.add(cmd);
                    Main.get().getCommand(cmd.getName(i)).setExecutor(Main.get());
                    Main.get().getCommand(cmd.getName(i)).setTabCompleter(cmd);
                }
            }
        }
    }
    public List<Listener> registerListeners(Listener ... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, Main.get());
        }
        return new ArrayList<>(Arrays.asList(listeners));
    }

    public boolean register(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        ICommand handler = null;
        try {
            if (commandHandlers.containsKey(command.getName().toLowerCase())) {
                handler = commandHandlers.get(command.getName().toLowerCase());
                handler.setPlayer(sender instanceof Player ? (Player) sender : null);

                if (disabled(handler)) {
                    handler.sendMessage(MessageType.ERROR, "This command is disabled!");
                    return false;
                }

                List<Boolean> permissions = new ArrayList<>();
                for (String permission : handler.getPermissions()) {
                    permissions.add(sender.hasPermission(permission) ||
                            (sender instanceof Player && Objects.requireNonNull(((Player) sender).getPersistentDataContainer().get(
                                    NamespaceKey.get("permissions"), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING)
                            )).contains(permission) || Objects.requireNonNull(((Player) sender).getPersistentDataContainer().get(
                                    NamespaceKey.get("permissions"), PersistentDataType.LIST.listTypeFrom(PersistentDataType.STRING)
                            )).contains("*")));
                }

                if (permissions.contains(true)) {
                    handler.execute(sender, args);
                    return true;
                }else{
                    handler.sendMessage(MessageType.ERROR, "You do not have permission to use this command!");
                }
            }

        }catch(Exception e) {
            handler.sendMessage(MessageType.EXCEPTION, "An error occurred while executing this command!\n"+e.getMessage());
            e.printStackTrace();
        }
        return true;
    }
    static boolean disabled(ICommand command) {
        Cmd cmd = command.getClass().getAnnotation(Cmd.class);
        return cmd != null && cmd.disabled();
    }
}
