package br.alkazuz.minigame.listener;

import br.alkazuz.minigame.main.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.event.server.*;
import br.alkazuz.minigame.game.*;
import java.util.concurrent.*;
import org.bukkit.event.*;
import org.bukkit.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import java.io.*;
import org.bukkit.event.player.*;

public class MinigameListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Round round = Main.theInstance().getRound();
        if (round != null) {
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
        if (Main.theInstance().getRound() != null) {
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
        event.setCancelled(true);
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
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (!MinigameConfig.SPAWN_MOBS) {
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
        Round round = Main.theInstance().getRound();
        if (round == null && !p.hasPermission("megasw.admin")) {
            event.setKickMessage("§cNenhuma partida encontrada.");
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            return;
        }
        MinigameConfig.LAST_PLAYER_JOINED = System.currentTimeMillis();
    }
}
