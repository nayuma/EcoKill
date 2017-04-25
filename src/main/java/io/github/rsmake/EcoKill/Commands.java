package io.github.rsmake.EcoKill;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {

    Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ecoreload")) {
            if (sender.hasPermission("ecokill.reload")) {
                sender.sendMessage(ChatColor.DARK_AQUA + "Configuration reloaded.");
                plugin.reloadPlugin();
                return true;
            } else {
                sender.sendMessage(ChatColor.DARK_RED + "Insufficient permissions to execute command.");
            }
        }
        return false;
    }
}
