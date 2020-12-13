package br.alkazuz.minigame.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import br.alkazuz.minigame.main.Main;

public class InvencibleAPI implements Listener
{
    public static List<Player> invencible = new ArrayList<Player>();
    
    public static void addPlayer(final Player p, final long time) {
        InvencibleAPI.invencible.add(p);
        //final FileConfiguration config = Main.theInstance().config;
        //p.sendMessage(config.getString("messages.invincible.start").replace("&", "§"));
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                InvencibleAPI.invencible.remove(p);
                //p.sendMessage(config.getString("messages.invincible.end").replace("&", "§"));
            }
        }, 20L * time);
    }
    
    @EventHandler
    public void onDamage(final EntityDamageEvent event) {
        if ( event.getEntity() instanceof Player) {

            final Player player = (Player)event.getEntity();
            if (InvencibleAPI.invencible.contains(player)) {
                event.setCancelled(true);
            }
        }
    }
}
