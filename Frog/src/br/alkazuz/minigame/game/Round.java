package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.minigame.api.ActionBarAPI;
import br.alkazuz.minigame.api.PlayerAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TagAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityPainting;

public class Round implements Listener
{
	public static HashMap<String, Long> delay = new HashMap<String, Long>();
    public AtomicInteger rodada;
    public int id;
    public List<Player> players;
    public Map<String, Integer> vipShop;
    public RoundState state;
    public long timeStarted;
    public RoundLevel level;
    public RoundCounter counter;
    
    public Map<Location, Material> blocks;
    public List<Block> removed;
    public List<Material> materials;
    
    @Override
    public String toString() {
        return String.format("Round@[ID=%s,State=%s,World=%s,PlayersSize=%s]", String.valueOf(this.id), this.state.toString(), this.level.world.getName(), String.valueOf(this.players.size()));
    }
    
    public Round(RoundLevel level) {
        this.rodada = new AtomicInteger(0);
        this.blocks = Collections.synchronizedMap(new HashMap<Location, Material>());
        this.removed = Collections.synchronizedList(new ArrayList<Block>());
        this.materials = Collections.synchronizedList(new ArrayList<Material>());
        this.players = new ArrayList<Player>();
        this.vipShop = Collections.synchronizedMap(new HashMap<String, Integer>());
        this.state = RoundState.AVAILABLE;
        this.level = level;
        if (level == null) {
            this.state = RoundState.LOADING;
        }
        else {
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
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Main.theInstance());
        for(Round round : Main.theInstance().rounds) {
        	if(round.level.startSpawn.getWorld().getName().equals(level.startSpawn.getWorld().getName())) {
        		state = RoundState.FINISHED;
    		}
        }
    }
    
    public boolean isValidWorld() {
    	for (int x = -3; x <= 3; ++x) {
            for (int y = -3; y <= 3; ++y) {
                for (int z = -3; z <= 3; ++z) {
                    Block block = this.level.world.getBlockAt(this.level.startSpawn.getBlockX() + x, this.level.startSpawn.getBlockY() + y, this.level.startSpawn.getBlockZ() + z);
                    if (block != null && block.getType() != Material.AIR) {
                    	
                        return true;
                    }
                }
            }
        }
    	System.out.println("Mundo inválido");
    	return false;
    }
    
    public void validateWorld() {
    	for(Player all : Bukkit.getOnlinePlayers()) {
    		if(all.getWorld().getName().equalsIgnoreCase(level.world.getName())) {
    			Main.theInstance().rounds.remove(this);
    			this.state = RoundState.FINISHED;
        		return;
    		}
    	}
    	for(Entity e : level.world.getNearbyEntities(level.startSpawn, 100, 100, 100)) {
    		if(e instanceof Player) {
    			Main.theInstance().rounds.remove(this);
    			this.state = RoundState.FINISHED;
        		return;
    		}
    	}
    	for (int y = -1; y <= 1; ++y) {
    		for (int x = -3; x <= 3; ++x) {
    			for (int z= -3; z <= 3; ++z) {
    				
    				Block block = this.level.world.getBlockAt(this.level.startSpawn.getBlockX() + x, this.level.startSpawn.getBlockY() + y, this.level.startSpawn.getBlockZ() + z);
    				if (block != null && block.getType() != Material.AIR) {
    					
    					return;
    				}
    			}
    		}
    	}
    	
    	this.state = RoundState.FINISHED;
		Main.theInstance().rounds.remove(this);
    }
    
    public void broadcastAlive(String str) {
    	Iterator<Player> iterator = new ArrayList<>(players).iterator();
    	while(iterator.hasNext()) {
    		Player p = iterator.next();
            if (p.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }
            p.sendMessage(str);
        }
    }
    
    public int timer = 10;
    public void update() {
    	
    	if(counter !=null) {
    		this.counter.timer--;
    	}
    	
    	if (this.state != RoundState.AVAILABLE) {
    		timer--;
    		int playersTotal = this.totalPlayers();
    		String msg = this.counter.getMessageAndUpdate(playersTotal);
    		if(players.size() <= 0) {
    			state = RoundState.FINISHED;
    		}
    		if(msg != null && !msg.equals("")) {
    			for (Player p2 : this.players) {
                	p2.sendMessage(msg);
                }
    		}
            if(counter.timer <= 0) {
            	for (Player p2 : this.players) {
            		broadcast(MinigameConfig.lose);
            		removePlayer(p2);
            	}
            	state = RoundState.FINISHED;
            }
    	}
    	
    	if (state == RoundState.IN_PROGRESS && timer <= 0) {
            timer = 10;
            state = RoundState.SNOW;
            Location spawn = level.startSpawn;
            if (materials.size() <= 1) {
                state = RoundState.FINAL;
                broadcastAlive("\n§cPise na l\u00e3 vermelha para vencer!\n");
                broadcastAlive("\n§cPise na l\u00e3 vermelha para vencer!\n");
                for (Location loc : blocks.keySet()) {
                    Material type = blocks.get(loc);
                    if (type == Material.WOOL) {
                        continue;
                    }
                    if (type == Material.AIR) {
                        continue;
                    }
                    Block block = spawn.getWorld().getBlockAt(loc);
                    block.setType(Material.SNOW_BLOCK);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        List<Location> d = new ArrayList<Location>(blocks.keySet());
                        Block random = spawn.getWorld().getBlockAt((Location)d.get(new Random().nextInt(d.size())));
                        random.setType(Material.WOOL);
                        random.setData((byte)14);
                    }
                }, 60L);
            }
            else {
                Material material = materials.get(new Random().nextInt(materials.size()));
                materials.remove(material);
                state = RoundState.LOCK;
                for (Location loc2 : blocks.keySet()) {
                    Block block = spawn.getWorld().getBlockAt(loc2);
                    if (material != block.getType() && !removed.contains(block)) {
                        continue;
                    }
                    removed.add(block);
                    block.setType(Material.SNOW_BLOCK);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        for (Location loc : blocks.keySet()) {
                            Block block = spawn.getWorld().getBlockAt(loc);
                            if (!removed.contains(block)) {
                                continue;
                            }
                            block.setType(Material.AIR);
                        }
                    }
                }, 200L);
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                        state = RoundState.IN_PROGRESS;
                        for (Location loc : blocks.keySet()) {
                            Block block = spawn.getWorld().getBlockAt(loc);
                            if (!removed.contains(block)) {
                                continue;
                            }
                            block.setType((Material)blocks.get(loc));
                        }
                    }
                }, 400L);
            }
            
    	}
    	
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            for (Player p2 : this.players) {
            	ActionBarAPI.sendActionBar(p2, counter.getMessageAndUpdate(players.size()));
                
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
    
    public void start() {
        if (this.state == RoundState.AVAILABLE) {
            this.counter.timer = 600;
            timer = 40;
            this.state = RoundState.WOOL_WAIT;
            this.timeStarted = System.currentTimeMillis();
            
            for (Player p : this.players) {
                try {
                	 p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    for (PotionEffect pe : p.getActivePotionEffects()) {
                        p.removePotionEffect(pe.getType());
                    }
                    PlayerData fromNick = PlayerManager.fromNick(p.getName());
                    ++fromNick.partidas;
                    for (String n : MinigameConfig.GAME_START) {
                        p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
                    }
                }
                catch (Exception ex) {}
            }
            
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                	counter.timer = 600;
                    state = RoundState.IN_PROGRESS;
                    broadcast("§3L\u00e3 removida, tome cuidado!");
                    Location spawn = level.startSpawn;
                    for (int x = -50; x <= 50; ++x) {
                        for (int y = -2; y <= 2; ++y) {
                            for (int z = -50; z <= 50; ++z) {
                            	
                                Location loc = new Location(level.startSpawn.getWorld(), (double)(level.startSpawn.getBlockX() + x), (double)(level.startSpawn.getBlockY() + y), (double)(level.startSpawn.getBlockZ() + z));
                                Chunk chunk = spawn.getWorld().getChunkAt(loc);
                                if (!chunk.isLoaded()) {
                                    chunk.load(true);
                                }
                                Block block = spawn.getWorld().getBlockAt(loc);
                                if (block.getType() != Material.GLASS) {
                                    if (block.getType() == Material.WOOL) {
                                        block.setType(Material.AIR);
                                    }
                                    else if (block.getType() != Material.AIR) {
                                        blocks.put(loc, block.getType());
                                        if (!materials.contains(block.getType())) {
                                            materials.add(block.getType());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }, 600L);
            
        }
    }
    
    public void joinPlayer(Player p) {
        TagAPI.apply("", p);
        PlayerAPI.resetPlayer(p);
        if(players.contains(p)) {
        	players.remove(p);
        }
        this.players.add(p);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(p);
            p.hidePlayer(all);
        }
        p.teleport(this.level.startSpawn);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (Player all : Round.this.players) {
                    all.showPlayer(p);
                    p.showPlayer(all);
                }
            }
        }, 2L);
        PlayerData data= PlayerManager.fromNick(p.getName());
    	if(data.getRank() == 1) {
    		broadcast(" ");
    		broadcast("§6§l[RANKING] §6"+p.getName()+" §6é TOP 1 neste minigame e está nessa sala!!");
    		broadcast(" ");
    	}else if(data.getRank() == 2){
    		broadcast(" ");
    		broadcast("§b§l[RANKING] §b"+p.getName()+" §bé TOP 2 neste minigame e está nessa sala!!");
    		broadcast(" ");
    	}else if(data.getRank() == 3){
    		broadcast(" ");
    		broadcast("§e§l[RANKING] §e"+p.getName()+" §eé TOP 3 neste minigame e está nessa sala!!");
    		broadcast(" ");
    	}
        if (!this.vipShop.containsKey(p.getName())) {
            this.vipShop.put(p.getName(), 0);
        }
        //p.getInventory().setItem(7, ShopMenu.shop);
        p.getInventory().setItem(8, ShopMenu.vip);
        ScoreBoard.createScoreBoardLobby(p, this, data);
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
        if (this.players.contains(p)) {
        	players.remove(p);
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
        return this.players.contains(p);
    }
    
    public void broadcast(String message) {
        for (Player p : this.players) {
            p.sendMessage(message);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
            this.players.remove(p);

            PlayerData data = PlayerManager.fromNick(p.getName());
            data.save();
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onWalk(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(players.contains(player)) {
        	if (player.getGameMode() == GameMode.SURVIVAL) {
        		if (state == RoundState.FINAL) {
        			Block block = player.getWorld().getBlockAt(player.getLocation().clone().add(0.0, -1.0, 0.0));
                    if (block != null && block.getType() == Material.WOOL && block.getData() == 14) {
                    	state = RoundState.STOP;
                    	broadcast(MinigameConfig.win_other.replace("{0}", player.getName()));
                    	PlayerManager.fromNick(player.getName()).winTotal++;
                    	player.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
                    	Main.theInstance().economy.depositPlayer(player, MinigameConfig.MONEY);
                    	Iterator<Player> iterator = new ArrayList<>(players).iterator();
                    	while(iterator.hasNext()) {
                    		Player p = iterator.next();
                             if (p == player) {
                            	 removePlayer(p);
                                 continue;
                             }
                             removePlayer(p);
                             p.sendMessage(MinigameConfig.lose);
                         }
                    	 state = RoundState.FINISHED;
                    }
        		}
        	}
        }
    }
    
    @EventHandler
    public void onD(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if(players.contains(player)) {
            	Location spawn = level.startSpawn;
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    event.setCancelled(true);
                    if (state == RoundState.AVAILABLE) {
                        player.teleport(spawn);
                    }
                    else if (state != RoundState.AVAILABLE) {
                        if (player.getGameMode() == GameMode.SPECTATOR) {
                            player.teleport(spawn.clone().add(0.0, 20.0, 0.0));
                            return;
                        }
                        broadcast(MinigameConfig.death.replace("{0}", player.getName()).replace("&", "§"));
                        player.sendMessage(MinigameConfig.lose.replace("&", "§"));
                        player.setGameMode(GameMode.SPECTATOR);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 3, 2));
                        player.teleport(spawn.clone().add(0.0, 20.0, 0.0));
                        removePlayer(player);
                    }
                }
            }
        }
    }
}
