package io.github.rsmake.EcoKill;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Eco extends JavaPlugin implements Listener{

    public final static Logger logger = Logger.getLogger("minecraft");
    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;

    @Override
    public void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
    }

    @Override
    public void onEnable(){
        if (!setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        logger.info(String.format("[%s] - Plugin enabled. Vault dependency found.", getDescription().getName()));
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable(){
        this.saveConfig();
        getLogger().info(String.format("[%s] -Plugin disabled.", getDescription().getName()));
    }

    @Override
    public void reloadConfig(){
        this.reloadConfig();
        logger.info(String.format("[%s] - Configuration reloaded.", getDescription().getName()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player killer = e.getEntity().getKiller();
        Player player = e.getEntity().getPlayer();

        if (killer == null){
            player.sendMessage(ChatColor.DARK_AQUA + "You were killed by the environment and lost $" + ChatColor.GOLD + getConfig().getDouble("OnDeathMoneyLost"));
            econ.withdrawPlayer(player, getConfig().getDouble("OnDeathMoneyLost"));
        }else{
            killer.sendMessage(ChatColor.DARK_AQUA + "You killed " + player.getDisplayName() + " and earned $" + ChatColor.GOLD + getConfig().getDouble("OnKillMoneyEarned"));
            econ.depositPlayer(killer, getConfig().getDouble("OnKillMoneyEarned"));
        }
    }
    public void onCommand(CommandSender sender, Command cmd, String[]args){
        if (cmd.getName().equalsIgnoreCase("ecoreload")){
            if (sender.hasPermission("ecokill.reload")){
                sender.sendMessage(ChatColor.DARK_AQUA + "Configuration reloaded.");
                reloadConfig();
            }else{
                sender.sendMessage(ChatColor.RED + "Insufficient permissions to execute command.");
            }
        }
    }
}
