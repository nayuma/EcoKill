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
    public void onEnable() {
        saveDefaultConfig();
        if (!setupEconomy()) {
            getLogger().severe("Please install Vault to use EcoKill, it has temporarily been disabled.");
            getServer().getPluginManager().disablePlugin(this);
        }
        setupPermissions();
        setupChat();
        getLogger().info(String.format("[%s] - Plugin enabled. Vault dependency found.", getDescription().getName()));
        getServer().getPluginManager().registerEvents(new Events(this), this);

        PvPOnKill = getConfig().getDouble("pvp.onKill");
        PvPOnDeath = getConfig().getDouble("pvp.onDeath");
        PvEOnDeath = getConfig().getDouble("pve.onDeath");
        EnvOnDeath = getConfig().getDouble("env.onDeath");
        pvp = getConfig().getBoolean("pvp.enabled");
        pve = getConfig().getBoolean("pve.enabled");
        env = getConfig().getBoolean("env.enabled");
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
