package io.github.rsmake.EcoKill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Eco extends JavaPlugin implements Listener {

    private double moneyOnKill;
    private double moneyOnDeath;
    Setup s = new Setup(this);

    @Override
    public void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
                this.saveDefaultConfig();
        }
        moneyOnKill = getConfig().getDouble("Money.OnKill");
        moneyOnDeath = getConfig().getDouble("Money.OnDeath");
    }

    @Override
    public void onEnable() {
        if (!s.setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        s.setupPermissions();
        s.setupChat();
        getLogger().info(String.format("[%s] - Plugin enabled. Vault dependency found.", getDescription().getName()));
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("ecoreload").setExecutor(new Commands(this));
    }

    @Override
    public void onDisable() {
        this.saveConfig();
        getLogger().info(String.format("[%s] - Plugin disabled.", getDescription().getName()));
    }

    public void reloadPlugin() {
        this.reloadConfig();
        getLogger().info(String.format("[%s] - Configuration reloaded.", getDescription().getName()));
        moneyOnKill = getConfig().getDouble("Money.OnKill");
        moneyOnDeath = getConfig().getDouble("Money.OnDeath");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        Player player = e.getEntity().getPlayer();

        if (killer == null) {
            if (moneyOnDeath != 0) {
                player.sendMessage(ChatColor.DARK_AQUA + "You were killed by the environment and lost " + ChatColor.GREEN + "$" + moneyOnDeath);
                s.econ.withdrawPlayer(player, moneyOnDeath);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "You were killed by the environment.");
            }
        } else {
            if (moneyOnKill != 0) {
                killer.sendMessage(ChatColor.DARK_AQUA + "You killed " + player.getDisplayName() + " and earned " + ChatColor.GREEN + "$" + moneyOnKill);
                s.econ.depositPlayer(killer, moneyOnKill);
            } else {
                killer.sendMessage(ChatColor.DARK_AQUA + "You killed " + player.getDisplayName() + ".");
            }
            if (moneyOnDeath != 0) {
                player.sendMessage(ChatColor.DARK_AQUA + "You were killed by " + killer.getDisplayName() + " and lost " + ChatColor.GREEN + "$" + moneyOnDeath);
                s.econ.withdrawPlayer(player, moneyOnDeath);
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "You were killed by " + killer.getDisplayName() + ".");
            }
        }
    }
}
