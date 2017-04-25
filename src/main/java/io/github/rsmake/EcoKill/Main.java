package io.github.rsmake.EcoKill;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static Economy econ = null;
    public static Permission perms = null;
    public static Chat chat = null;
    public double PvPOnKill;
    public double PvPOnDeath;
    public double PvEOnDeath;
    public double EnvOnDeath;
    public boolean pvp;
    public boolean pve;
    public boolean env;

    @Override
    public void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
    }

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        getLogger().info(String.format("[%s] - Plugin enabled. Vault dependency found.", getDescription().getName()));
        getServer().getPluginManager().registerEvents(new Events(this), this);
        this.getCommand("ecoreload").setExecutor(new Commands(this));

        PvPOnKill = getConfig().getDouble("PvP.OnKill");
        PvPOnDeath = getConfig().getDouble("PvP.OnDeath");
        PvEOnDeath = getConfig().getDouble("PvE.OnDeath");
        EnvOnDeath = getConfig().getDouble("Env.OnDeath");
        pvp = getConfig().getBoolean("PvP.Enabled");
        pve = getConfig().getBoolean("PvE.Enabled");
        env = getConfig().getBoolean("Env.Enabled");
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
        PvEOnDeath = getConfig().getDouble("PvE.OnDeath");
        EnvOnDeath = getConfig().getDouble("Env.OnDeath");
        pvp = getConfig().getBoolean("PvP.Enabled");
        pve = getConfig().getBoolean("PvE.Enabled");
        env = getConfig().getBoolean("Env.Enabled");
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
}