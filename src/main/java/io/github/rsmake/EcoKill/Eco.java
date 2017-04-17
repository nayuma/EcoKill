package io.github.rsmake.EcoKill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Eco extends JavaPlugin implements Listener {

    private double PvPOnKill;
    private double PvPOnDeath;
    private double PvEOnKill;
    private double PvEOnDeath;
    private double EnvOnDeath;
    private boolean pvp;
    private boolean pve;
    private boolean env;

    Setup s = new Setup(this);

    @Override
    public void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
        PvPOnKill = getConfig().getDouble("PvP.OnKill");
        PvPOnDeath = getConfig().getDouble("PvP.OnDeath");
        PvEOnKill = getConfig().getDouble("PvE.OnKill");
        PvEOnDeath = getConfig().getDouble("PvE.OnDeath");
        EnvOnDeath = getConfig().getDouble("Env.OnDeath");
        pvp = getConfig().getBoolean("PvP.Enabled");
        pve = getConfig().getBoolean("PvE.Enabled");
        env = getConfig().getBoolean("Env.Enabled");
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
        PvPOnKill = getConfig().getDouble("PvP.OnKill");
        PvPOnDeath = getConfig().getDouble("PvP.OnDeath");
        PvEOnKill = getConfig().getDouble("PvE.OnKill");
        PvEOnDeath = getConfig().getDouble("PvE.OnDeath");
        EnvOnDeath = getConfig().getDouble("EnvOnDeath");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Entity murdurer = e.getEntity().getKiller();
        Entity player = e.getEntity().getPlayer();

        if (murdurer instanceof Player && player instanceof Player) {
            Player killer = ((Player) murdurer).getPlayer();
            Player killed = ((Player) player).getPlayer();

            if (pvp = true) {
                killer.sendMessage(ChatColor.DARK_AQUA + "You killed " + killed.getDisplayName() + ChatColor.DARK_AQUA + " and earned " + ChatColor.GREEN + "$" + PvPOnKill);
                s.econ.depositPlayer(killer, PvPOnKill);
                player.sendMessage(ChatColor.DARK_AQUA + "You were killed by " + killer.getDisplayName() + ChatColor.DARK_AQUA + " and lost " + ChatColor.GREEN + "$" + PvPOnDeath);
                s.econ.withdrawPlayer(killed, PvPOnDeath);
            }

        } else if (murdurer instanceof Monster && player instanceof Player) {
            Player killed = ((Player) player).getPlayer();
            if (pve = true) {
                player.sendMessage(String.format(ChatColor.DARK_AQUA + "You were killed by " + murdurer.getType().toString().replace("_", " ").toLowerCase() + " and lost " + ChatColor.GREEN + "$%s", PvEOnDeath));
                s.econ.withdrawPlayer(killed, PvEOnDeath);
            }

        } else if (murdurer instanceof Player && player instanceof Monster) {
            Player killer = ((Player) murdurer).getPlayer();
            if (pve = true) {
                player.sendMessage(String.format(ChatColor.DARK_AQUA + "You killed " + player.getType().toString().replace("_", " ").toLowerCase() + " and won " + ChatColor.GREEN + "$%s", PvEOnKill));
                s.econ.withdrawPlayer(killer, PvEOnDeath);
            }

        } else {
            Player killed = ((Player) player).getPlayer();
            if (env = true) {
                player.sendMessage(String.format(ChatColor.DARK_AQUA + "You were killed by the environment and lost " + ChatColor.GREEN + "$%s", EnvOnDeath));
                s.econ.withdrawPlayer(killed, EnvOnDeath);
            }
        }
    }
}
