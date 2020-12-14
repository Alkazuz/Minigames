package br.alkazuz.minigame.listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.alkazuz.minigame.game.MinigameConfig;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.game.RoundState;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.spigot.addons.main.SpigotAddons;

public class MinigameListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        
        if(SpigotAddons.vanish.containsKey(p.getName()) && SpigotAddons.vanish.get(p.getName())) {
        	
        	Player target = Bukkit.getPlayer(SpigotAddons.playerTP.get(p.getName()));
        	if(target != null) {
        		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                    	p.teleport(target);
                		p.sendMessage("§aVocê foi teleportado até "+target.getDisplayName()+"§a.");
                		for(Round r : Main.theInstance().rounds) {
                			if(r.hasPlayer(target)) {
                				for(Player all : r.players.keySet()) {
                					p.showPlayer(all);
                				}
                			}
                		}
                    }
                }, 10L);
        		
        	}
        	return;
        }
        
        Round round = Main.theInstance().getRound();
        if (round != null && round.state ==RoundState.AVAILABLE) {
            new BukkitRunnable() {
                public void run() {
                    round.joinPlayer(p);
                }
            }.runTask((Plugin)Main.theInstance());
        }
    }
    
    @EventHandler
    public void onMotd(ServerListPingEvent event) {
        if (System.currentTimeMillis() - MinigameConfig.STARTED > TimeUnit.HOURS.toMillis(1L)) {
            event.setMotd("indisponivel");
            return;
        }
        if (Bukkit.hasWhitelist()) {
            event.setMotd("§cManuten\u00e7\u00e3o");
            return;
        }
        if (Main.theInstance().getRound() != null&& Main.theInstance().getRound().state ==RoundState.AVAILABLE) {
            event.setMotd("§aSala disponivel");
            return;
        }
        event.setMotd("indisponivel");
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!MinigameConfig.DROP) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (!MinigameConfig.PICK_UP) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && !MinigameConfig.PVP) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onSpawn(CreatureSpawnEvent event) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                if(event.getEntity().hasMetadata("custom"))return;
                event.getEntity().remove();
            }
        }, 2L);
    }
    
    @EventHandler
    public void onBuild(BlockBreakEvent event) {
        if (!MinigameConfig.BUILD && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (!MinigameConfig.BUILD && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (!MinigameConfig.PLAYER_DAMAGE && event.getEntity() instanceof Player) {
            event.setCancelled(true);
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && !MinigameConfig.FALL_DAMAGE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onchat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player p = event.getPlayer();
        String msg = event.getMessage();
        String format = "§e[L] §f{0}§e: §e" + msg;
        String send = format.replace("{0}", p.getDisplayName()).replace("{1}", msg.trim());
        p.sendMessage(send);
        for (Entity e : p.getNearbyEntities(30.0, 30.0, 30.0)) {
            if (e instanceof Player) {
                Player d = (Player)e;
                d.sendMessage(send);
            }
        }
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage();
        if (cmd.contains(" ")) {
            cmd = cmd.split(" ")[0];
        }
        if (cmd.equalsIgnoreCase("/spawn") || cmd.equalsIgnoreCase("/home") || cmd.equalsIgnoreCase("/lobby")) {
            Player p = event.getPlayer();
            event.setCancelled(true);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF("lobby");
            }
            catch (IOException e2) {
                e2.printStackTrace();
            }
            p.sendPluginMessage((Plugin)Main.theInstance(), "BungeeCord", b.toByteArray());
        }
    }
    
    
    @EventHandler
    public void onJoin(PlayerLoginEvent event) {
        Player p = event.getPlayer();
        
        if(SpigotAddons.vanish.containsKey(p.getName()) && SpigotAddons.vanish.get(p.getName())) return;
        
        Round round = Main.theInstance().getRound();
        if (round == null && !p.hasPermission("minigame.admin")) {
            event.setKickMessage("§cNenhuma partida encontrada.");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        MinigameConfig.LAST_PLAYER_JOINED = System.currentTimeMillis();
    }
}
