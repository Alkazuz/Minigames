package br.alkazuz.lobby.listener;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import br.alkazuz.lobby.api.AutoRestart;
import br.alkazuz.lobby.api.ItemBuilder;
import br.alkazuz.lobby.api.QueueAPI;
import br.alkazuz.lobby.api.TagAPI;
import br.alkazuz.lobby.main.Main;
import br.alkazuz.lobby.menu.Menus;
import br.alkazuz.lobby.object.Servidor;
import br.alkazuz.lobby.object.Servidores;
import br.alkazuz.lobby.scoreboard.ScoreBoard;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class PlayerListener implements Listener
{
    public ItemStack bussola;
    public ItemStack vip;
    
    public PlayerListener() {
        this.bussola = new ItemBuilder(Material.COMPASS).name("§bMenu de Jogos").build();
        this.vip = new ItemBuilder(Material.DIAMOND).name("§6[§6§lVIP§6]").build();
    }
    
    @EventHandler
    public void onPing(ServerListPingEvent e) {
    	if(!AutoRestart.started) {
    		e.setMotd("offline");
    	}else {
    		e.setMotd("§aOnline");
    	}
    	
    }
    
    @EventHandler
    public void npc(NPCRightClickEvent event) {
    	for(Servidor servidor : Servidores.servidores) {
    		if(servidor.npc != null && servidor.npc.getNpc() == event.getNPC()) {
    			event.getClicker().chat("/"+servidor.name);
    			event.setCancelled(true);
    			return;
    		}
    	}
    }
    
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onchat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player p = event.getPlayer();
        String msg = event.getMessage();
        String format = "§e[L] §f{0}§e: §e" + msg;
        if (p.hasPermission("minigames.staff")) {
            format = "§e[L] §4[STAFF] §f{0}§e: §e" + msg;
        }
        else if (p.hasPermission("minigames.vip")) {
            format = "§e[L] §6[VIP] §f{0}§e: §e" + msg;
        }
        String send = format.replace("{0}", p.getName()).replace("{1}", msg.trim());
        p.sendMessage(send);
        for (Entity e : p.getNearbyEntities(30.0, 30.0, 30.0)) {
            if (e instanceof Player) {
                Player d = (Player)e;
                d.sendMessage(send);
            }
        }
    }
    
    public HashMap<Player, Block> last = new HashMap<Player, Block>();
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
    	if(event.getPlayer().getLocation().getBlock() != null && event.getPlayer().getLocation().getBlock().getType() == Material.PORTAL) {
    		if(last.containsKey(event.getPlayer()) && last.get(event.getPlayer()).getType() == Material.PORTAL) return;
    		for (Servidor servidor : Servidores.servidores) {
                if (servidor.lobby != null) {
                    if (servidor.lobby.getWorld() != event.getPlayer().getWorld()) {
                        continue;
                    }
                    if (event.getPlayer().getLocation().distance(servidor.lobby) > 50.0) {
                        continue;
                    }
                    if (QueueAPI.queue.containsKey(event.getPlayer())) {
                        event.getPlayer().sendMessage(" ");
                        event.getPlayer().sendMessage("§cVoc\u00ea j\u00e1 est\u00e1 na fila para jogar " + QueueAPI.queue.get(event.getPlayer()).flatName + ". Aguarde uma partida ser encontrada para jogar!");
                        event.getPlayer().sendMessage(" ");
                        return;
                    }
                    QueueAPI.addToQueue(event.getPlayer(), servidor);
                    
                    break;
                }
            }
		}
    	last.put(event.getPlayer(), event.getPlayer().getLocation().getBlock());
    }
    
//    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
//    public static void aoTeleportaPorPortal(PlayerPortalEvent event) {
//        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
//            event.setCancelled(true);
//            for (Servidor servidor : Servidores.servidores) {
//                if (servidor.lobby != null) {
//                    if (servidor.lobby.getWorld() != event.getPlayer().getWorld()) {
//                        continue;
//                    }
//                    if (event.getPlayer().getLocation().distance(servidor.lobby) > 50.0) {
//                        continue;
//                    }
//                    if (QueueAPI.queue.containsKey(event.getPlayer())) {
//                        event.getPlayer().sendMessage(" ");
//                        event.getPlayer().sendMessage("§cVoc\u00ea j\u00e1 est\u00e1 na fila para jogar " + QueueAPI.queue.get(event.getPlayer()).flatName + ". Aguarde uma partida ser encontrada para jogar!");
//                        event.getPlayer().sendMessage(" ");
//                        return;
//                    }
//                    QueueAPI.addToQueue(event.getPlayer(), servidor);
//                    break;
//                }
//            }
//        }
//    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player p = (Player)event.getWhoClicked();
        if (event.getInventory().getTitle().equals("Lista de Minigames")) {
            event.setCancelled(true);
            ItemStack item = event.getCurrentItem();
            if (item != null) {
                for (Servidor servidor : Servidores.servidores) {
                    if (servidor.getIcon().isSimilar(item) && servidor.lobby != null) {
                        p.teleport(servidor.lobby);
                        if (QueueAPI.queue.containsKey(p)) {
                            QueueAPI.queue.remove(p);
                        }
                        for(Player all : Bukkit.getOnlinePlayers()) {
                        	if(QueueAPI.lobby.containsKey(all.getName()) && QueueAPI.lobby.get(all.getName()) == servidor) {
                        		p.showPlayer(all);
                        		all.showPlayer(p);
                        	}else {
                        		p.hidePlayer(all);
                        		all.hidePlayer(p);
                        	}
                        }
                        QueueAPI.lobby.put(p.getName(), servidor);
                        p.sendMessage("§3» §bTeleportando para o lobby do §6" + servidor.flatName + "§b.");
                        ScoreBoard.createScoreBoardMinigames(p, servidor);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item != null && item.isSimilar(this.bussola)) {
            event.setCancelled(true);
            Menus.openGames(event.getPlayer());
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.getInventory().setBoots((ItemStack)null);
        p.getInventory().setChestplate((ItemStack)null);
        p.getInventory().setLeggings((ItemStack)null);
        p.getInventory().setHelmet((ItemStack)null);
        p.getInventory().clear();
        p.getInventory().setItem(7, this.bussola);
        p.getInventory().setItem(8, this.vip);
        for (PotionEffect pe : p.getActivePotionEffects()) {
            p.removePotionEffect(pe.getType());
        }
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
        
        if (p.hasPermission("minigames.admin")) {
            return;
        }
    }
    
    @EventHandler
    public void onJoin2(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        TagAPI.apply("", p);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                if (QueueAPI.lobby.containsKey(p.getName())) {
                    Servidor servidor = QueueAPI.lobby.get(p.getName());
                    if (servidor.lobby != null) {
                        p.teleport(servidor.lobby);
                    }
                    ScoreBoard.createScoreBoardMinigames(p, servidor);
                    ScoreBoard.updateScoreboardMinigame(p, servidor);
                    for(Player all : Bukkit.getOnlinePlayers()) {
           			 if(QueueAPI.lobby.containsKey(all.getName()) && QueueAPI.lobby.get(all.getName()) == servidor) {
           				 all.showPlayer(p);
           				 p.showPlayer(all);
           			 }else {
           				 all.hidePlayer(p);
           				 p.hidePlayer(all);
           			 }
           		 }
                }else {
                	ScoreBoard.createScoreBoard(p);
                	for(Player all : Bukkit.getOnlinePlayers()) {
           			 if(!QueueAPI.lobby.containsKey(all.getName())) {
           				 all.showPlayer(p);
           				 p.showPlayer(all);
           			 }else {
           				 all.hidePlayer(p);
           				 p.hidePlayer(all);
           			 }
           		 }
                }
            }
        }, 2L);
    }
    
    @EventHandler
    public void onDrop(PlayerPickupItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }
}
