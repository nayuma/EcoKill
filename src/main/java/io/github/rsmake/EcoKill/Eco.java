package io.github.rsmake.EcoKill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public class Eco extends JavaPlugin implements Listener{

    public final static Logger logger = Logger.getLogger("minecraft");
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
    public void onEnable(){
        if (!s.setupEconomy()) {
            logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        s.setupPermissions();
        s.setupChat();
        logger.info(String.format("[%s] - Plugin enabled. Vault dependency found.", getDescription().getName()));
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("ecoreload").setExecutor(new Commands(this));
    }

    @Override
    public void onDisable(){
        this.saveConfig();
        getLogger().info(String.format("[%s] - Plugin disabled.", getDescription().getName()));
    }

    public void reloadPlugin(){
        this.reloadConfig();
        logger.info(String.format("[%s] - Configuration reloaded.", getDescription().getName()));
        moneyOnKill = getConfig().getDouble("Money.OnKill");
        moneyOnDeath = getConfig().getDouble("Money.OnDeath");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        Player killer = e.getEntity().getKiller();
        Player player = e.getEntity().getPlayer();

        if (killer == null){
            player.sendMessage(ChatColor.DARK_AQUA + "You were killed by the environment and lost " + ChatColor.GREEN + "$" + moneyOnDeath);
            s.econ.withdrawPlayer(player, moneyOnDeath);
        }else{
            killer.sendMessage(ChatColor.DARK_AQUA + "You killed " + player.getDisplayName() + " and earned " + ChatColor.GREEN + "$" + moneyOnKill);
            player.sendMessage(ChatColor.DARK_AQUA + "You were killed by " + killer.getDisplayName() + " and lost " + ChatColor.GREEN + "$" + moneyOnDeath);
            s.econ.depositPlayer(killer, moneyOnKill);
            s.econ.withdrawPlayer(player, moneyOnDeath);
        }
    }
}
