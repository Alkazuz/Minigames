package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import br.alkazuz.minigame.api.ActionBarAPI;
import br.alkazuz.minigame.api.PlayerAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.game.SkyChest.ChestType;
import br.alkazuz.minigame.game.itens.ItemInfoSky;
import br.alkazuz.minigame.game.itens.SkywarsItem;
import br.alkazuz.minigame.game.itens.SkywarsItens;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;
import br.alkazuz.minigame.utils.Utils;
import me.arcaniax.hdb.libs.xseries.XMaterial;

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
    
    public List<SkyChest> chestsFeast = new ArrayList<SkyChest>();
    public List<SkyChest> chestsMiniFeast = new ArrayList<SkyChest>();
    
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
        	Arena arena = new Arena(null, location,this);
        	arenas.add(arena);
        }
        
        for(Location locs : level.feastChests) {
        	Location l = locs.clone();
        	l.setWorld(level.world);
        	chestsFeast.add(new SkyChest(null, l, ChestType.FEAST));
        }
        
        for(Location locs : level.MFeastChests) {
        	Location l = locs.clone();
        	l.setWorld(level.world);
        	chestsMiniFeast.add(new SkyChest(null, l, ChestType.MINI_FEAST));
        }
        refill_fest_minifeast();
    }

    public void refill_fest_minifeast() {
    	
    	for(String key : SkywarsItens.MINI_FEAST.keySet()) {
			ItemInfoSky info = SkywarsItens.MINI_FEAST.get(key);
			List<SkywarsItem> itens = new ArrayList<SkywarsItem>(info.itens);
			List<SkywarsItem> repetido = new ArrayList<SkywarsItem>();
			
			Iterator<SkywarsItem> iterator = itens.iterator();
			int count = Utils.randInt(info.min, info.max);
			int i = 0;
			while(iterator.hasNext() && i < count) {
				SkywarsItem skyItem = iterator.next();
				if(repetido.contains(skyItem) && !info.repeat)continue;
				if(Utils.percent(skyItem.chance) || skyItem.chance == 100) {
					
					SkyChest chest = chestsMiniFeast.get(new Random().nextInt(chestsMiniFeast.size()));
					chest.chest.getInventory().setItem(emptySlot(chest.chest.getInventory()), skyItem.item);
					//System.out.println("item "+ skyItem.item.getType().toString() + " adicionado em "+Methods.encodeLocation(chest.getLocation()));
					repetido.add(skyItem);
					if(skyItem.chance != 100) i++;
				}
			}
		}
    	
    	for(String key : SkywarsItens.FEAST.keySet()) {
			ItemInfoSky info = SkywarsItens.FEAST.get(key);
			List<SkywarsItem> itens = new ArrayList<SkywarsItem>(info.itens);
			List<SkywarsItem> repetido = new ArrayList<SkywarsItem>();
			
			Iterator<SkywarsItem> iterator = itens.iterator();
			int count = Utils.randInt(info.min, info.max);
			int i = 0;
			while(iterator.hasNext() && i < count) {
				SkywarsItem skyItem = iterator.next();
				if(repetido.contains(skyItem) && !info.repeat)continue;
				if(Utils.percent(skyItem.chance) || skyItem.chance == 100) {
					
					SkyChest chest = chestsFeast.get(new Random().nextInt(chestsFeast.size()));
					chest.chest.getInventory().setItem(emptySlot(chest.chest.getInventory()), skyItem.item);
					//System.out.println("item "+ skyItem.item.getType().toString() + " adicionado em "+Methods.encodeLocation(chest.getLocation()));
					repetido.add(skyItem);
					if(skyItem.chance != 100) i++;
				}
			}
		}
    }
    
    private int emptySlot(Inventory inv) {
		List<Integer> empts = new ArrayList<Integer>();
		for(int i = 0; i < inv.getSize(); i++) {
			if(inv.getItem(0) == null) empts.add(i);
		}
		if(empts.size() > 0) {
			return empts.get(new Random().nextInt(empts.size()));
		}
		return 0;
	}
    
    public void createWinner() {
    	List<SkywarsPlayer> playerList = new ArrayList<SkywarsPlayer>();
    	players.values().stream().filter(p -> !p.died).forEach(playerList::add);
    	if(playerList.size() == 0) {
    		this.state = RoundState.FINISHED;
    		return;
    	}
    	SkywarsPlayer winner = playerList.get(0);
    	Player p = winner.player;
    	
    	new EffectWin(winner);
    	TitleAPI.sendTitle(p, 10, 60, 10, "§a§lVITÓRIA", "§7Você venceu a partida.");
    	Main.theInstance().economy.depositPlayer((OfflinePlayer)p, (double)MinigameConfig.MONEY);
        p.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
        broadcast(p.getDisplayName() + " §evenceu a partida!");
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
                while(iterator.hasNext()) {
                	iterator.next();
                	removePlayer(p);
                }
                Round.this.state = RoundState.FINISHED;
            }
        }, 100L);
    	//
    }
    
    public void update() {
    	if(counter !=null) {
    		this.counter.timer--;
    	}
    	
        if (this.state == RoundState.IN_PROGRESS) {
        	if(playersAlive() == 0) {
        		this.state = RoundState.FINISHED;
        		return;
        	}
        	if(playersAlive() == 1) {
        		state = RoundState.FINISHING;
        		createWinner();
        	}
        	for(Player sp : players.keySet()) {
        		ScoreBoard.updateScoreBoard(sp, this, players.get(sp));
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
    		if(arena.player == null) return arena;
    	}
    	return null;
    }
    
    public int maxPlayers() {
    	return arenas.size();
    }
    
    public void joinPlayer(Player p) {
    	
    	p.setGameMode(GameMode.ADVENTURE);
        PlayerAPI.resetPlayer(p);
        this.players.put(p, new SkywarsPlayer(p, emptyArena()));
        players.get(p).arena.player = p;
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
        if (this.players.containsKey(p)) {
        	players.get(p).arena.player=null;
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
        return this.players.containsKey(p);
    }
    
    public void broadcast(String message) {
    	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
            p.sendMessage(message);
        }
    }
    
    public int playersAlive() {
    	return (int) players.keySet().stream().filter(p -> !players.get(p).died).count();
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
    	Player p = event.getPlayer();
    	if(hasPlayer(p) && state == RoundState.AVAILABLE) {
    		event.setCancelled(true);
    		return;
    	}
    	if(hasPlayer(p) && event.getBlock() != null) {
    		for(Arena arena : arenas) {
    			for(SkyChest chest : arena.chests) {
    				if(event.getBlock().getLocation() == chest.chest.getLocation()) {
    					event.setCancelled(true);
    					return;
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void Death(PlayerDeathEvent event) {
    	Player death = event.getEntity();
    	
    	event.setDeathMessage(null);
    	event.setKeepInventory(false);
    	if(!hasPlayer(death)) return;
    	SkywarsPlayer sp2 = players.get(death);
    	sp2.died = true;
    	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	death.spigot().respawn();
            	death.teleport(level.feastSpawn);
            	death.setGameMode(GameMode.SPECTATOR);
            }
        }, 2L);
    	Player killer = death.getKiller();
    	if(killer == null) {
    		event.setDeathMessage(death.getDisplayName() + " §emorreu sozinho!");
    	}else {
    		Location loc = death.getLocation();
    		for (double height = 0.0; height < 1.0; height += 0.8) {
                death.getWorld().playEffect(loc.clone().add((double)Utils.randomRange(-1.0f, 1.0f), height, (double)Utils.randomRange(-1.0f, 1.0f)), Effect.STEP_SOUND, (Object)XMaterial.REDSTONE_BLOCK.parseMaterial());
                death.getWorld().playEffect(loc.clone().add((double)Utils.randomRange(1.0f, -1.0f), height, (double)Utils.randomRange(-1.0f, 1.0f)), Effect.STEP_SOUND, (Object)XMaterial.REDSTONE_BLOCK.parseMaterial());
            }
    		
    		event.setDeathMessage(death.getDisplayName() + " §efoi eliminado por "+killer.getDisplayName());
    		SkywarsPlayer sp = players.get(killer);
    		sp.kills++;
    		if(System.currentTimeMillis() - sp.lastKill <= 10000) {
    			if(sp.streak == 2) {
    				broadcast(MinigameConfig.STREAK_DOUBLEKILL.replace("{0}", killer.getDisplayName()));
    			}else if(sp.streak == 3) {
    				broadcast(MinigameConfig.STREAK_TRIPLEKILL.replace("{0}", killer.getDisplayName()));
    			}else if(sp.streak == 4) {
    				broadcast(MinigameConfig.STREAK_QUADRAKILL.replace("{0}", killer.getDisplayName()));
    			}else if(sp.streak == 5) {
    				broadcast(MinigameConfig.STREAK_PENTAKILL.replace("{0}", killer.getDisplayName()));
    				sp.streak = 0;
    			}
    		}else {
    			sp.streak = 0;
    		}
    		sp.lastKill = System.currentTimeMillis();
    	}
    	broadcast(event.getDeathMessage());
    	event.setDeathMessage(null);
    	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
        	ActionBarAPI.sendActionBar(p, "§b"+playersAlive()+" §ejogadores vivos!");
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
