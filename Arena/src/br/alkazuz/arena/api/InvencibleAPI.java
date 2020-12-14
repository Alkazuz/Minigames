package br.alkazuz.arena.api;

import org.bukkit.entity.*;
import java.util.*;
import br.alkazuz.arena.main.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.*;

public class InvencibleAPI implements Listener
{
    public static List<Player> invencible;
    
    static {
        InvencibleAPI.invencible = new ArrayList<Player>();
    }
    
    public static void addPlayer(final Player p, final long time) {
        InvencibleAPI.invencible.add(p);
        final FileConfiguration config = Main.theInstance().config;
        p.sendMessage(config.getString("messages.invincible.start").replace("&", "§"));
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                InvencibleAPI.invencible.remove(p);
                p.sendMessage(config.getString("messages.invincible.end").replace("&", "§"));
            }
        }, 20L * time);
    }
    
    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            final Player damager = (Player)event.getDamager();
            final Player player = (Player)event.getEntity();
            if (InvencibleAPI.invencible.contains(damager) || InvencibleAPI.invencible.contains(player)) {
                event.setCancelled(true);
            }
        }
    }
}
