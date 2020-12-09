package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import br.alkazuz.minigame.api.ActionBarAPI;
import br.alkazuz.minigame.api.PlayerAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;

public class Round implements Listener
{
	public static HashMap<String, Long> delay = new HashMap<String, Long>();
    public AtomicInteger rodada;
    public int id;
    public Map<Player, SkywarsPlayer> players = Collections.synchronizedMap(new HashMap<Player, SkywarsPlayer>());
    public Map<String, Integer> vipShop = Collections.synchronizedMap(new HashMap<String, Integer>());
    public List<Arena> arenas = new ArrayList<Arena>();
    public RoundState state;
    public long timeStarted;
    public long timeLoaded;
    public RoundLevel level;
    public RoundCounter counter;
    
    @Override
    public String toString() {
        return String.format("Round@[ID=%s,State=%s,World=%s,PlayersSize=%s]", String.valueOf(this.id), this.state.toString(), this.level.world.getName(), String.valueOf(this.players.size()));
    }
    
    public Round(int id, RoundLevel level) {
        this.rodada = new AtomicInteger(0);
        this.state = RoundState.AVAILABLE;
        this.level = level;
        if (level == null) {
            this.state = RoundState.LOADING;
        }
        this.id = Integer.valueOf(String.valueOf(id));
        this.timeLoaded = System.currentTimeMillis();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Main.theInstance());
        for(Location location : level.cages) {
        	Arena arena = new Arena(location,this);
        	arenas.add(arena);
        }
        for(Round round : Main.theInstance().rounds) {
        	if(round.level.startSpawn.getWorld().getName().equals(level.startSpawn.getWorld().getName())) {
        		state = RoundState.FINISHED;
    		}
        }
    }

    public void update() {
    	if(counter !=null) {
    		this.counter.timer--;
    	}
    	
        if (this.state == RoundState.IN_PROGRESS) {
        	if(players.size() == 0) {
        		this.state = RoundState.FINISHED;
        		return;
        	}
        	if(players.size() == 1) {
        		
        	}
        }
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
            while(iterator.hasNext()) {
            	Player p2 = iterator.next();
            	PlayerData data = PlayerManager.fromNick(p2.getName());
            	ScoreBoard.updateScoreBoardLobby(p2, this, data);
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
                    if (this.counter.timer <= 5 && counter.timer > 0) {
                    	p2.playSound(p2.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                    }
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
    	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
            ScoreBoard.createScoreBoard(p, this);
        }
    }
    
    public void start() {
        if (this.state == RoundState.AVAILABLE) {
            this.counter.timer = 60;
            this.state = RoundState.IN_PROGRESS;
            this.timeStarted = System.currentTimeMillis();
            Main.theInstance().finder.update();
            
            Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
            while(iterator.hasNext()) {
            	Player p = iterator.next();
            	ScoreBoard.createScoreBoard(p, this);
                PlayerData fromNick = PlayerManager.fromNick(p.getName());
                ++fromNick.partidas;
                for (String n : MinigameConfig.GAME_START) {
                    p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
                }
                p.setGameMode(GameMode.SURVIVAL);
                players.get(p).arena.cage.destroy();
            }
        }
    }
    
    public Arena emptyArena() {
    	for(Arena arena : arenas) {
    		if(players.size() == 0) return arena;
    		for(SkywarsPlayer skywarsP : players.values()) {
    			if(skywarsP.arena.spawn.distance(arena.spawn) >= 5.0D); {
    				return arena;
    			}
    		}
    	}
    	return null;
    }
    
    public int maxPlayers() {
    	return arenas.size();
    }
    
    public void joinPlayer(Player p) {
    	
    	p.setGameMode(GameMode.ADVENTURE);
        PlayerAPI.resetPlayer(p);
        this.players.put(p, new SkywarsPlayer(p.getName(), emptyArena()));
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(p);
            p.hidePlayer(all);
        }
        
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
        
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	p.teleport(players.get(p).arena.spawn.clone().add(0.5D, 0, 0.5D));
            	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
                while(iterator.hasNext()) {
                	Player all = iterator.next();
                    all.showPlayer(p);
                    p.showPlayer(all);
                }
            }
        }, 2L);
        if (!this.vipShop.containsKey(p.getName())) {
            this.vipShop.put(p.getName(), 0);
        }
        if(MinigameConfig.SHOP_ITEM) {
        	p.getInventory().setItem(7, ShopMenu.shop);
        }
        if(MinigameConfig.VIP_ITEM) {
        	p.getInventory().setItem(8, ShopMenu.vip);
        }
        ScoreBoard.createScoreBoardLobby(p, this, data);
        ScoreBoard.updateScoreBoardLobby(p, this, data);
        if(delay.containsKey(p.getName())) {
        	if(System.currentTimeMillis() - delay.get(p.getName()) <= 1000) {
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
    	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
            p.sendMessage(message);
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
    	Player p = event.getPlayer();
    	if(hasPlayer(p) && state == RoundState.AVAILABLE) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPvp(EntityDamageByEntityEvent event) {
    	if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
    		Player damager = (Player) event.getDamager();
    		Player p = (Player) event.getEntity();
    		if(hasPlayer(p) || hasPlayer(damager)) {
    			if(state == RoundState.AVAILABLE) {
    				event.setCancelled(true);
    				return;
    			}
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
        	broadcast("§c"+p.getName() + " abandonou a partida!");
        	removePlayer(p);
        }
    }
    
    
}
