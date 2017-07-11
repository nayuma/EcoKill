package io.github.rsmake.EcoKill;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Events implements Listener {

    Main s;

    public Events(Main main) {
        this.s = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player killed = e.getEntity().getPlayer();
        if (e.getEntity().getKiller() instanceof Creature) {
            if (s.pve == true) {
                String killer_ = e.getEntity().getKiller().getType().toString();
                killed.sendMessage(String.format(ChatColor.DARK_AQUA + "You were killed by a " + ChatColor.DARK_BLUE + "%s" + ChatColor.DARK_AQUA + "and lost $%s", killer_, s.PvPOnKill));
                s.econ.withdrawPlayer(killed, s.PvEOnDeath);
            }
        } else if (e.getEntity().getKiller() instanceof Player) {
            if (s.pvp == true) {
                Player killer = e.getEntity().getKiller().getPlayer();
                killer.sendMessage(ChatColor.DARK_AQUA + "You killed " + killed.getDisplayName() + ChatColor.DARK_AQUA + " and earned $" + s.PvPOnKill);
                killed.sendMessage(ChatColor.DARK_AQUA + "You died by " + killer.getDisplayName() + ChatColor.DARK_AQUA + " and lost $" + s.PvPOnDeath);
                s.econ.depositPlayer(killer, s.PvPOnKill);
                s.econ.withdrawPlayer(killed, s.PvEOnDeath);
            }
        } else {
            if (s.env == true) {
                killed.sendMessage(ChatColor.DARK_AQUA + "You died by the environment and lost $" + s.EnvOnDeath);
            }
        }
    }
}

