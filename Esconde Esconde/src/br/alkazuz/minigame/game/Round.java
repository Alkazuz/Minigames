package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import br.alkazuz.minigame.api.PlayerAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TagAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;
import br.alkazuz.minigame.utils.ItemBuilder;
import br.alkazuz.minigame.utils.Utils;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityPainting;

public class Round implements Listener
{
	public static HashMap<String, Long> delay = new HashMap<String, Long>();
    public int id;
    public HashMap<Player, EscPlayer> players;
    public RoundState state;
    public long timeStarted;
    public long timeLoaded;
    public RoundLevel level;
    public RoundCounter counter;
    private boolean valid = false;
    
    @Override
    public String toString() {
        return String.format("Round@[ID=%s,State=%s,World=%s,PlayersSize=%s]", String.valueOf(this.id), this.state.toString(), this.level.world.getName(), String.valueOf(this.players.size()));
    }
    
    public Round(RoundLevel level) {
        this.players = new HashMap<Player, EscPlayer>();
        this.state = RoundState.AVAILABLE;
        this.level = level;
        if (level == null) {
            this.state = RoundState.LOADING;
        }
        else {
        	for(int x = -10; x <= 10; x++) {
        		for(int z = -10; z <= 10; z++) {
            		Chunk c = level.startSpawn.getWorld().getChunkAt(x, z);
            		if(c != null) {
            			c.load(true);
            		}
            	}
        	}
            for (Entity e : level.startSpawn.getWorld().getEntities()) {
                if (e instanceof Player) {
                    continue;
                }
                if (e instanceof EntityPainting) {
                    continue;
                }
                if (e instanceof EntityItemFrame) {
                    continue;
                }
                e.remove();
            }
            this.validateWorld();
        }
        this.id = RoundLevel.worldCounter.get();
        this.timeLoaded = System.currentTimeMillis();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Main.theInstance());
    }
    
    public boolean isValidWorld() {
    	List<Material> list = Utils.getAmountMaterials(level.startSpawn);
    	if(list.size() < 5 && !list.contains(Material.GLASS)) {
    		System.out.println("Mundo inválido");
    		return false;
    	}
    	return true;
    }
    
    public void validateWorld() {
    	if(isValidWorld()) return;
    	this.state = RoundState.FINISHED;
		Main.theInstance().rounds.remove(this);
		Iterator<Player> iterator = players.keySet().iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
        	removePlayerMD(p);
        }
    }
    
    public void update() {
    	
    	if(getHiddenCount() <= 0 && (this.state == RoundState.IN_PROGRESS || this.state == RoundState.HIDDING)) {
    		broadcast("\n§a§lOS SEKEERS VENCERAM!!\n");
    		
    		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                	Iterator<Player> iterator = new ArrayList<Player>(players.keySet()).iterator();
                    while(iterator.hasNext()) {
                    	Player p = iterator.next();
        				EscPlayer info = players.get(p);
        				if(info.seek) {
        					PlayerManager.fromNick(p.getName()).winTotal++;
        				}
        				removePlayer(p);
            		}
            		state = RoundState.FINISHED;
                }
            }, 80L);
    		
    	}
    	
    	
    	if(getSeekCount() <= 0 && (this.state == RoundState.IN_PROGRESS || this.state == RoundState.HIDDING)) {
    		List<Player> list = new ArrayList<Player>(this.players.keySet());
            Player random = list.get(new Random().nextInt(list.size()));
            EscPlayer dp = this.players.get(random);
            dp.seek = true;
            
            broadcast("§3"+random.getName()+" §eé o novo Seeker.");
            
            for(Player p : players.keySet()) {
    			ScoreBoard.update(p);
    		}
            dp.seek = true;
            TagAPI.apply("§3", "", random, true);
            random.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS)
        			.setLeatherArmorColor(Color.NAVY).build());
            random.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS)
        			.setLeatherArmorColor(Color.NAVY).build());
            random.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
        			.setLeatherArmorColor(Color.NAVY).build());
            random.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
        			.setLeatherArmorColor(Color.NAVY).build());
            random.getInventory().setItem(0, new ItemBuilder(Material.STONE_SWORD)
        			.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
        			.addUnsafeEnchantment(Enchantment.DURABILITY, 5).build());
            random.getInventory().remove(Material.STICK);
            
            if(this.state == RoundState.IN_PROGRESS) {
            	random.teleport(level.startSpawn);
            }else {
            	random.teleport(level.seekSpawn);
            }
    	}
    	
    	if (this.state == RoundState.IN_PROGRESS) {
    		if(counter.timer <= 0 && getHiddenCount() > 0) {
    			broadcast("\n§a§lOS ESCONDIDOS VENCERAM!!\n");
    			for(Player p : players.keySet()) {
    				EscPlayer info = players.get(p);
    				if(!info.seek) {
    					Main.theInstance().economy.depositPlayer(p, MinigameConfig.MONEY_HIDDEN);
    					p.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY_HIDDEN)));
    					PlayerManager.fromNick(p.getName()).winTotal++;
    				}
    				removePlayer(p);
    			}
    			state = RoundState.FINISHED;
    			return;
    		}
    	}
    	
        if (this.state == RoundState.IN_PROGRESS || this.state == RoundState.HIDDING) {
        	if(players.size() == 0) {
        		this.state = RoundState.FINISHED;
        		return;
        	}
        	
        	int playersTotal = this.totalPlayers();
    		String msg = this.counter.getMessageAndUpdate(playersTotal);
    		if(msg != null && this.state == RoundState.HIDDING) {
    			for (Player p2 : this.players.keySet()) {
                	p2.sendMessage(msg);
                }
    		}
    		updateScoreboard();
        }
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            Iterator<Player> iterator = players.keySet().iterator();
            while(iterator.hasNext()) {
            	Player p2 = iterator.next();
                ScoreBoard.updateScoreBoardLobby(p2, this, PlayerManager.fromNick(p2.getName()));
                if (this.counter.timer <= 5 && this.counter.starting) {
                    if (this.counter.timer == 5) {
                        TitleAPI.sendTitle(p2, 0, 19, 0, "§65", "");
                    }
                    if (this.counter.timer == 4) {
                        TitleAPI.sendTitle(p2, 0, 19, 0, "§64", "");
                    }
                    if (this.counter.timer == 3) {
                        TitleAPI.sendTitle(p2, 0, 19, 0, "§e3", "");
                    }
                    if (this.counter.timer == 2) {
                        TitleAPI.sendTitle(p2, 0, 19, 0, "§c2", "");
                    }
                    if (this.counter.timer == 1) {
                        TitleAPI.sendTitle(p2, 0, 19, 0, "§41", "");
                    }
                    p2.playSound(p2.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                }
            }
            if (this.counter.canStart) {
                this.start();
            }
        }
    }
    
    public void cleanUp() {
        HandlerList.unregisterAll((Listener)this);
        for(Player p : Bukkit.getOnlinePlayers()) {
        	if(p.getWorld() == level.world) {
        		ServerAPI.sendPlayer(p, "lobby");
        	}
        }
    }
    
    public void updateScoreboard() {
        for (Player p : this.players.keySet()) {
            ScoreBoard.updateScoreBoard(p, this);
        }
    }
    
    public void start() {
        if (this.state == RoundState.AVAILABLE) {
            this.counter.timer = 60;
            this.state = RoundState.HIDDING;
            this.timeStarted = System.currentTimeMillis();
            
            List<Player> list = new ArrayList<Player>(this.players.keySet());
            Player random = list.get(new Random().nextInt(list.size()));
            EscPlayer dp = this.players.get(random);
            dp.seek = true;
            
            Iterator<Player> iterator = players.keySet().iterator();
            while(iterator.hasNext()) {
            	Player p = iterator.next();
            	EscPlayer dp2 = this.players.get(p);
            	for (Player all : this.players.keySet()) {
            		p.hidePlayer(all);
            		all.hidePlayer(p);
            		EscPlayer dpall = this.players.get(all);
            		if(dpall.seek == dp2.seek) {
            			all.showPlayer(p);
            			p.showPlayer(all);
            		}
                }
            }
            counter.timer = 61;
            for (Player p : this.players.keySet()) {
                try {
                	ScoreBoard.createScoreBoard(players.get(p), this);
                    EscPlayer dp2 = this.players.get(p);
                    for (PotionEffect pe : p.getActivePotionEffects()) {
                        p.removePotionEffect(pe.getType());
                    }
                   
                    PlayerData fromNick = PlayerManager.fromNick(p.getName());
                    ++fromNick.partidas;
                    for (String n : MinigameConfig.GAME_START) {
                    	if(dp2.seek) {
                    		p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY_SEEK)));
                    	}else {
                    		p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY_HIDDEN)));
                    	}
                        
                    }
                    if (!dp2.seek) {
                    	p.teleport(level.startSpawn);
                       
                        TagAPI.apply("§6", "", p, true);
                        p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS)
                    			.setLeatherArmorColor(Color.ORANGE).build());
                    	p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS)
                    			.setLeatherArmorColor(Color.ORANGE).build());
                    	p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    			.setLeatherArmorColor(Color.ORANGE).build());
                    	p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
                    			.setLeatherArmorColor(Color.ORANGE).build());
                    	p.getInventory().setItem(0, new ItemBuilder(Material.STICK)
                    			.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1)
                    			.addUnsafeEnchantment(Enchantment.DURABILITY, 5).build());
                    	p.sendMessage("§a§lVOCÊ TEM 1 MINUTO PARA SE ESCONDER, ELES NÃO TEM ESTÃO TE VENDO.");
                    }else {
                    	p.teleport(level.seekSpawn);
                    	TagAPI.apply("§3", "", p, true);
                    	p.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS)
                    			.setLeatherArmorColor(Color.NAVY).build());
                    	p.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS)
                    			.setLeatherArmorColor(Color.NAVY).build());
                    	p.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                    			.setLeatherArmorColor(Color.NAVY).build());
                    	p.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
                    			.setLeatherArmorColor(Color.NAVY).build());
                    	p.getInventory().setItem(0, new ItemBuilder(Material.STONE_SWORD)
                    			.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
                    			.addUnsafeEnchantment(Enchantment.DURABILITY, 5).build());
                    	p.sendMessage("§e§lVOCÊ SERÁ LIBERADO EM 1 MINUTO, ENQUANTO ISSO, VOCÊ NÃO IRÁ VER ELES SE ESCONDEREM.");
                    }
                    ScoreBoard.update(p);
                }
                catch (Exception ex) {ex.printStackTrace();}
            }
            
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                	counter.timer = 601;
                    state = RoundState.IN_PROGRESS;
                    broadcast("\n§E§LOS SEEKERS FORAM LIBERADOS!!!\n");
                    
                    for (Player p : players.keySet()) {
                    	 EscPlayer dp2 = players.get(p);
                    	 if(dp2.seek) {
                    		 p.teleport(level.startSpawn);
                    	 }else {
                    		 for(Player pa : players.keySet()) {
                    				ScoreBoard.update(pa);
                    			}
                    	 }
                    	for (Player all : players.keySet()) {
                    		all.showPlayer(p);
                    		p.showPlayer(all);
                        }
                    }
                    
                }
            }, 20L*60L);
            
        }
    }
    
    public int getSeekCount() {
        int i = 0;
        for (EscPlayer dp : this.players.values()) {
            if (dp.seek) {
                ++i;
            }
        }
        return i;
    }
    
    public int getHiddenCount() {
        int i = 0;
        for (EscPlayer dp : this.players.values()) {
            if (!dp.seek) {
                ++i;
            }
        }
        return i;
    }
    
    public void joinPlayer(Player p) {
    	
        //TagAPI.apply("", "", p, true);
        PlayerAPI.resetPlayer(p);
        //NoNameTag.showNametag(p);
        this.players.put(p, new EscPlayer(p));
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(p);
            p.hidePlayer(all);
        }
        p.teleport(this.level.startSpawn);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	
            	if(!valid) {
            		valid = true;
            		validateWorld();
            	}
            	
                for (Player all : Round.this.players.keySet()) {
                    all.showPlayer(p);
                    p.showPlayer(all);
                }
            }
        }, 2L);
        PlayerData data = PlayerManager.fromNick(p.getName());
        p.getInventory().setItem(8, ShopMenu.shop);
        //p.getInventory().setItem(8, ShopMenu.vip);
        ScoreBoard.createScoreBoardLobby(players.get(p), this, data);
        ScoreBoard.updateScoreBoardLobby(p, this, data);
        if(delay.containsKey(p.getName())) {
        	if(System.currentTimeMillis() - delay.get(p.getName()) <= 3000) {
        		return;
        	}
        	
        }
        delay.put(p.getName(), System.currentTimeMillis());
        for (String d : MinigameConfig.WELCOME) {
            p.sendMessage(d.replace("{0}", String.valueOf(this.players.size())));
        }
    }
    
    public boolean removePlayer(Player p) {
        if (this.players.remove(p) != null) {
            PlayerData data = PlayerManager.fromNick(p.getName());
            data.save();
            this.removePlayerMD(p);
            return true;
        }
        return false;
    }
    
    private void removePlayerMD(Player p) {
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        p.setPlayerListName(p.getName());
        ServerAPI.sendPlayer(p, "lobby");
    }
    
    public int totalPlayers() {
        return this.players.size();
    }
    
    public boolean hasPlayer(Player p) {
        return this.players.containsKey(p);
    }
    
    public void broadcast(String message) {
        for (Player p : this.players.keySet()) {
            p.sendMessage(message);
        }
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
    	Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
        	if(state == RoundState.IN_PROGRESS) {
            	e.setRespawnLocation(level.startSpawn);
            	respawn(p);
            }else {
            	e.setRespawnLocation(level.seekSpawn);
            	respawn(p);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
            if (this.state == RoundState.IN_PROGRESS) {
                this.broadcast(MinigameConfig.SEEK_QUIT.replace("{0}", p.getName()));
            }
            removePlayer(p);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        if (this.hasPlayer(player)){
        	if(event.getSlotType() == SlotType.ARMOR) {
        		event.setCancelled(true);
        	}
        }
    }
    
    @EventHandler
    public void onKill(PlayerDeathEvent event) {
    	Player player = event.getEntity();
    	event.setDeathMessage(null);
    	if(hasPlayer(player)) {
    		event.setKeepInventory(true);
    		
    		respawn(player);
    		
    		EscPlayer dp = this.players.get(player);
    		if(event.getEntity().getKiller() == null) {
    			if(dp.seek) {
    				 Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
	    	                @Override
	    	                public void run() {
	    	                	if(state == RoundState.IN_PROGRESS) {
	    	                    	player.teleport(level.startSpawn);
	    	                    }else {
	    	                    	player.teleport(level.seekSpawn);
	    	                    }
	    	                }
	    	            }, 5L);
    			}else {
    				Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    	@Override
                    	public void run() {
                    		setSeek(player);
                    		ScoreBoard.update(player);
                    	}
    	            }, 5L);
    				return;
    			}
    		}
    		
    		if(event.getEntity().getKiller() != null) {
    			Player killer = event.getEntity().getKiller();
    			if(hasPlayer(killer)) {
    				EscPlayer ikiller = this.players.get(killer);
    				if(ikiller.seek) {
    					broadcast(MinigameConfig.death
    							.replace("{0}", killer.getName())
    							.replace("{1}", player.getName()));
    					Main.theInstance().economy.depositPlayer(killer, 1);
    					Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
        	                @Override
        	                public void run() {
        	                    setSeek(player);
        	                }
        	            }, 5L);
        				return;
    					
    				}
    			}
    		}
    		
    	}
    }
    
    public void respawn(Player player) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	 player.spigot().respawn();
            	 player.teleport(level.seekSpawn);
            }
		}, 1L);
    }
    
    public void setSeek(Player player) {
    	EscPlayer dp = this.players.get(player);
    	
        dp.seek = true;
        TagAPI.apply("§3", "", player, true);
        player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS)
    			.setLeatherArmorColor(Color.NAVY).build());
		player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS)
    			.setLeatherArmorColor(Color.NAVY).build());
		player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
    			.setLeatherArmorColor(Color.NAVY).build());
		player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET)
    			.setLeatherArmorColor(Color.NAVY).build());
		player.getInventory().setItem(0, new ItemBuilder(Material.STONE_SWORD)
    			.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2)
    			.addUnsafeEnchantment(Enchantment.DURABILITY, 5).build());
		player.getInventory().remove(Material.STICK);
		
		for(Player p : players.keySet()) {
			ScoreBoard.update(p);
		}
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
    	Player player = (Player)event.getPlayer();
    	if(hasPlayer(player) && state != RoundState.AVAILABLE) {
    		if(event.getTo().getWorld() != level.world) {
    			event.setTo(level.seekSpawn);
    		}
    	}
    }
    
    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
        	Player player = (Player)event.getEntity();
        	if(hasPlayer(player) && state != RoundState.IN_PROGRESS) {
        		 event.setCancelled(true);
        	}else if( hasPlayer(player) && state == RoundState.IN_PROGRESS && event.getCause() == DamageCause.FALL) {
        		event.setDamage(event.getDamage() / 3);
        	}
           
            return;
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player player = (Player)event.getEntity();
            if (this.hasPlayer(player) && this.hasPlayer(damager)) {
                EscPlayer dp = this.players.get(damager);
                EscPlayer dpDamaged = this.players.get(player);
                if (dpDamaged == null) {
                    event.setCancelled(true);
                    return;
                }
                if (dp.seek == dpDamaged.seek) {
                    event.setCancelled(true);
                    return;
                }
                
            }
        }
    }
}
