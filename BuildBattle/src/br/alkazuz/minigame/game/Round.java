package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import br.alkazuz.minigame.api.ActionBarAPI;
import br.alkazuz.minigame.api.PlayerAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TagAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.menu.UtilsMenu;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;
import br.alkazuz.minigame.utils.ItemBuilder;
import br.alkazuz.minigame.utils.Methods;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPainting;

public class Round implements Listener
{
	public static HashMap<String, Long> delay = new HashMap<String, Long>();
    public AtomicInteger rodada;
    public int id;
    public Map<Player, Arena> players = Collections.synchronizedMap(new HashMap<Player, Arena>());
    public Map<Player, String> votes = Collections.synchronizedMap(new HashMap<Player, String>());
    public Map<Player, Vote> voteArena = Collections.synchronizedMap(new HashMap<Player, Vote>());
    public Map<String, Integer> vipShop;
    public RoundState state;
    public long timeStarted;
    public long timeLoaded;
    public RoundLevel level;
    public RoundCounter counter;
    public String themeCategory;
    public String theme;
    public Arena arenaVote;
    
    @Override
    public String toString() {
        return String.format("Round@[ID=%s,State=%s,World=%s,PlayersSize=%s]", String.valueOf(this.id), this.state.toString(), this.level.world.getName(), String.valueOf(this.players.size()));
    }
    
    public Round(int id, RoundLevel level) {
        this.rodada = new AtomicInteger(0);
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
        this.id = Integer.valueOf(String.valueOf(id));
        this.timeLoaded = System.currentTimeMillis();
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
                    //System.out.println( block.getType());
                    if (block != null && block.getType() != Material.AIR && block.getType() != Material.DIRT) {
                    	
                        return true;
                    }
                }
            }
        }
    	//System.out.println("Mundo inválido");
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
    		}else {
    			if(e instanceof EntityLiving) {
    				e.remove();
    			}
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
    
    public Arena next() {
    	Iterator<Arena> iterator = players.values().iterator();
    	while(iterator.hasNext()) {
    		Arena arena = iterator.next();
    		if(!arena.voted) return arena;
    	}
    	return null;
    }
    public void nextArena() {
    	state = RoundState.VOTING;
    	if(arenaVote != null) {
    		for(Player p : players.keySet()) {
    			arenaVote.points += voteArena.get(p).points;
    			arenaVote.voted = true;
    		}
    	}
    	voteArena.clear();
    	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	Arena next = next();
            	if(next == null) {
            		state = RoundState.ENDING;
            		return;
            	}
            	
            	for(Player p : players.keySet()) {
        			int slot = 0;
        			for(Vote vote : Vote.values()) {
        				if(vote == Vote.NENHUM) continue;
        				p.getInventory().setItem(slot, new ItemBuilder(Material.STAINED_CLAY, 1, vote.data).name(vote.nome).build());
        				slot++;
        			}
        			
        		}
            	counter.timer = 25;
            	arenaVote = next;
            	for(Player p : players.keySet()) {
            		p.teleport(arenaVote.randomLocationSpawn());
            		p.sendMessage("§eConstrução de §e"+arenaVote.owner.getName()+"§e. O que você achou desta construção?");
            		voteArena.put(p, Vote.NENHUM);
            	}
            	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                    	state = RoundState.VOTING_NEXT;
                    }
                }, 20L * 20L);
            	
            }
        }, 100L);
    }
    Player winner1 = null;
    Player winner2 = null;
    Player winner3 = null;
    int delayd = 0 ;
    public void update() {
    	if(counter !=null) {
    		this.counter.timer--;
    	}
    	if (this.state == RoundState.VOTING) {
    		for(Player p : players.keySet()) {
    			if (this.counter.timer == 5) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§65", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 4) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§64", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 3) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§e3", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 2) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§c2", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 1) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§41", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
    		}
    		return;
    	}
    	
    	if (this.state == RoundState.ENDING) {
    		state = RoundState.END;
    		List<Player> sort = new ArrayList<Player>(players.keySet());
            Collections.sort(sort, new Comparator<Player>() {
                @Override
                public int compare(Player c1, Player c2) {
                    Float o1 = (float)players.get(c1).points;
                    Float o2 = (float)players.get(c2).points;
                    return o2.compareTo(o1);
                }
            });
            winner1 = sort.get(0);
            if(sort.size() > 1) {
            	winner2 = sort.get(1);
            }
            if(sort.size() > 2) {
            	winner3 = sort.get(2);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append(" §6§lBUILDBATTLE §f- Vencedor(es)\n");
            sb.append("\n");
            sb.append(" §b§l1° lugar §f- "+winner1.getName()+" §e"+players.get(winner1).points+" pts.\n");
            if(winner2 != null) {
            	sb.append(" §6§l2° lugar §f- "+winner2.getName()+" §e"+players.get(winner2).points+" pts.\n");
            	Main.theInstance().economy.depositPlayer(winner2, MinigameConfig.MONEY_SECOND);
            	
            }
            if(winner3 != null) {
            	sb.append(" §e§l3° lugar §f- "+winner3.getName()+" §e"+players.get(winner3).points+" pts.\n");
            	Main.theInstance().economy.depositPlayer(winner3, MinigameConfig.MONEY_TREE);
            }
            sb.append("\n");
            Main.theInstance().economy.depositPlayer(winner1, MinigameConfig.MONEY_FIRST);
            broadcast(sb.toString());
            broadcast(" ");
             delayd = 0;
            Arena winnerArena = players.get(winner1);
            for(Player p : players.keySet()) {
            	p.teleport(winnerArena.randomLocationSpawn());
            }
            new BukkitRunnable() {
                public void run() {
                    if(delayd >= 5) {
                    	this.cancel();
                    	Iterator<Player> iterator = new ArrayList<Player>(players.keySet()).iterator();
                    	while(iterator.hasNext()) {
                    		try {
                    			Player p = iterator.next();
                    			if(winner2 != null && winner2 == p) {
                        			p.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY_SECOND)));
                        		}else if(winner3 != null && winner3 == p) {
                        			p.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY_TREE)));
                        		}else if(winner1 != null && winner1 == p) {
                        			p.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY_FIRST)));
                        		}else {
                        			p.sendMessage(MinigameConfig.lose);
                        		}
                    			removePlayer(p);
                    		}catch (Exception e) {}
                    		
                    	}
                    	state = RoundState.FINISHED;
                    	return;
                    }
                    if(winner1.isOnline()) {
                    	Entity e = winner1.getLocation().getWorld().spawnEntity(winner1.getLocation(), EntityType.FIREWORK);
                    	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                            @Override
                            public void run() {
                            	e.remove();
                            }
                        }, 20L);
                    }
                    delayd++;
                }
            }.runTaskTimer((Plugin)Main.theInstance(), 0L, 20L);
            
    	}
    	
    	if (this.state == RoundState.VOTING_NEXT) {
    		nextArena();
    	}
    	
    	if (this.state == RoundState.IN_PROGRESS && this.counter !=null) {
    		String msg = this.counter.getMessageAndUpdate(this.totalPlayers());
    		
    		if (this.counter.timer <= 0) {
    			state = RoundState.VOTING;
    			for(Player p : players.keySet()) {
    				
    				TitleAPI.sendTitle(p, 0, 20, 0, "", "§cTEMPO ESGOTADO!");
    				p.playSound(p.getLocation(), Sound.ANVIL_LAND, 5.0f, 5.0f);
    				p.setGameMode(GameMode.SURVIVAL);
    				p.setAllowFlight(true);
    				p.setFlying(true);
    				p.getInventory().clear();
    				p.getInventory().setHelmet(null);
    				p.getInventory().setChestplate(null);
    				p.getInventory().setLeggings(null);
    				p.getInventory().setBoots(null);
    				p.getInventory().setItem(8, ShopMenu.vip);
    				p.sendMessage("§eVocê será teleportado para votação em breve.");
    			}
    			nextArena();
    			return;
   		 	}
    		
            for(Player p : players.keySet()) {
       		 	ActionBarAPI.sendActionBar(p, msg);
       		 	if (this.counter.timer % 60 == 0) {
       		 		TitleAPI.sendTitle(p, 20, 10, 20, "§bRestam", "§e"+Methods.getRemainingTime2("", counter.timer));
       		 		p.playSound(p.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
       		 	}
       		 	
       		 	if (this.counter.timer == 30) {
    		 		TitleAPI.sendTitle(p, 20, 10, 20, "§bRestam", "§e30s");
    		 		p.playSound(p.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
    		 	}
       		 	if (this.counter.timer == 15) {
       		 		TitleAPI.sendTitle(p, 20, 10, 20, "§bRestam", "§e15s");
       		 		p.playSound(p.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
       		 	}
       		 		
       		 	if (this.counter.timer == 5) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§65", "");
       		 	p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 4) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§64", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 3) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§e3", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 2) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§c2", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
       		 	if (this.counter.timer == 1) {
       		 		TitleAPI.sendTitle(p, 0, 19, 0, "§41", "");
       		 		p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
       		 	}
           }
    	}
    	
        if (this.state != RoundState.AVAILABLE && this.state != RoundState.LOADING) {
        	if(players.size() == 0) {
        		this.state = RoundState.FINISHED;
        		return;
        	}
            String msg = this.counter.getMessageAndUpdate(this.totalPlayers());
            if(msg != null) {
            	for(Player p : players.keySet()) {
              		 ActionBarAPI.sendActionBar(p, msg);
                       if (this.counter.timer <= 5 && MinigameConfig.COUNTER) {
                           if (this.counter.timer == 5) {
                               TitleAPI.sendTitle(p, 0, 19, 0, "§65", "");
                               p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                           }
                           if (this.counter.timer == 4) {
                               TitleAPI.sendTitle(p, 0, 19, 0, "§64", "");
                               p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                           }
                           if (this.counter.timer == 3) {
                               TitleAPI.sendTitle(p, 0, 19, 0, "§e3", "");
                               p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                           }
                           if (this.counter.timer == 2) {
                               TitleAPI.sendTitle(p, 0, 19, 0, "§c2", "");
                               p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                           }
                           if (this.counter.timer == 1) {
                               TitleAPI.sendTitle(p, 0, 19, 0, "§41", "");
                               p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                           }
                           
                       }
                   }
            }
            
                
        }
        
        if(state == RoundState.IN_PROGRESS) {
        	for (Player all : players.keySet()) {
        		PlayerData data = PlayerManager.fromNick(all.getName());
        		ScoreBoard.updateScoreBoard(all, this, data);
        	}
        }
        
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            for(Player p2 : players.keySet()) {
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
    
    public String best = null;
    public void start() {
        if (this.state == RoundState.AVAILABLE) {
        	
        	Location base = new Location(level.world, 0, 30, 0);
        	for (Player all : players.keySet()) {
        		Arena arena = new Arena(all, base.clone());
        		
        		players.put(all, arena);
        		all.teleport(arena.spawn);
        		base.add(0, 0, 100); 
        		arena.load();
        		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                    	arena.load();
                    }
                }, 5L);
        	}
        	
            this.counter.timer = 60;
            this.state = RoundState.CHOOSING;
            this.timeStarted = System.currentTimeMillis();
            Main.theInstance().finder.update();
            String categoryWin = (new ArrayList<String>(Themes.themes.keySet())).get(new Random().nextInt(Themes.themes.keySet().size()));
            if(votes.size() > 0) {
            	
            	for(String n : Themes.themes.keySet()) {
            		if( best == null) {
            			best = n;
            		}
            		int countA = (int) votes.values().stream().filter(t -> t.equalsIgnoreCase(n)).count();
            		int countB = (int) votes.values().stream().filter(t -> t.equalsIgnoreCase(best)).count();
            		if(countA > countB) {
            			best = n;
            		}
            	}
            	if(best != null) {
            		categoryWin = best;
            	}
            }
            themeCategory = categoryWin;
            for(Player p : players.keySet()) {
            	p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                PlayerData fromNick = PlayerManager.fromNick(p.getName());
                p.setGameMode(GameMode.SURVIVAL);
                p.setAllowFlight(true);
                p.setFlying(true);
                ++fromNick.partidas;
                for (String n : MinigameConfig.GAME_START) {
                    p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY_FIRST)));
                }
                p.playSound(p.getLocation(), Sound.NOTE_BASS_DRUM, 1f, 1f);
                TitleAPI.sendTitle(p, 0, 40, 0, "§eEscolhendo tema...", "§b§kabcdefghi");
            }
            theme = Themes.themes.get(themeCategory).get(new Random().nextInt(Themes.themes.get(themeCategory).size()));
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                @Override
                public void run() {
                	state = RoundState.IN_PROGRESS;
                	counter.timer = 300;
                    for (Player all : players.keySet()) {
                        all.setGameMode(GameMode.CREATIVE);
                        all.getInventory().setItem(7, UtilsMenu.util);
                        TitleAPI.sendTitle(all, 0, 40, 0, "§eTema escolhido", "§b"+theme);
                        ScoreBoard.createScoreBoard(all, Round.this);
                        PlayerData data = PlayerManager.fromNick(all.getName());
                        ScoreBoard.updateScoreBoard(all, Round.this, data);
                        all.playSound(all.getLocation(), Sound.ANVIL_USE, 5f, 5f);
                    }
                }
            }, 40L);
        }
    }
    
    public void joinPlayer(Player p) {
    	
    	p.teleport(level.startSpawn);
    	for (Entity e : level.startSpawn.getWorld().getEntities()) {
            if (e instanceof EntityLiving && !(e instanceof Player)) {
            	e.remove();
            }
        }
    	
        TagAPI.apply("", p);
        PlayerAPI.resetPlayer(p);
        players.put(p, null);
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
            	 p.teleport(level.startSpawn);
            	 for(Player all : players.keySet()) {
                    all.showPlayer(p);
                    p.showPlayer(all);
                }
                for (Entity e : level.startSpawn.getWorld().getEntities()) {
                    if (e instanceof EntityLiving && !(e instanceof Player)) {
                    	e.remove();
                    }
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
            PlayerData data = PlayerManager.fromNick(p.getName());
            data.save();
            this.removePlayerMD(p);
            players.remove(p);
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
    	for(Player p : players.keySet()) {
            p.sendMessage(message);
        }
    }
    
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
    	Player p = event.getPlayer();
    	if(hasPlayer(p)) {
    		if(state != RoundState.IN_PROGRESS){
    			event.setCancelled( true);
    			return;
    		}
    		Arena arena = players.get(p);
    		if(!arena.canInteractLocation(event.getBlock().getLocation())){
    			event.setCancelled( true);
    			return;
    		}
    	}
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player p = event.getPlayer();
    	if(hasPlayer(p)) {
    		if(state == RoundState.VOTING) {
    			ItemStack item = p.getItemInHand();
    			if( item != null && item.getType() == Material.STAINED_CLAY) {
    				Vote vote = Vote.byData(item.getDurability());
    				if(vote != null) {
    					if(arenaVote == players.get(p)) {
    						p.sendMessage("§cVocê não pode votar na sua própria construção.");
    						event.setCancelled(true);
    						return;
    					}
    					p.sendMessage("§aVocê votou "+vote.nome + " §anesta construção.");
    					voteArena.put(p, vote);
    					event.setCancelled(true);
    				}
    			}
    		}
    	}
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
    	Player p = event.getPlayer();
    	if(hasPlayer(p)) {
    		if(state != RoundState.AVAILABLE && players.get(p) != null){
    			if(event.getTo().getY() >= 58D) {
    				Arena arena = players.get(p);
    				p.teleport(arena.spawn);
    				p.sendMessage("§cVocê não pode sair da sua arena.");
    			}
    		}
    		
    		else if(state != RoundState.AVAILABLE && arenaVote != null){
    			if(event.getTo().getY() >= 58D) {
    				p.teleport(arenaVote.randomLocationSpawn());
    				p.sendMessage("§cVocê não pode sair da arena que está sendo votada!");
    			}
    		}
    		
    	}
    }
    
    @EventHandler
    public void onBuild(BlockPlaceEvent event) {
    	Player p = event.getPlayer();
    	if(hasPlayer(p)) {
    		if(state != RoundState.IN_PROGRESS){
    			event.setCancelled( true);
    			return;
    		}
    		if(event.getBlock().getType() == Material.TNT) {
    			event.setCancelled(true);
    			return;
    		}
    		Arena arena = players.get(p);
    		if(!arena.canInteractLocation(event.getBlock().getLocation())){
    			event.setCancelled( true);
    			return;
    		}
    	}
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
        	removePlayer(p);
        }
    }
    
    public enum Vote {
    	NENHUM("", (short)0, 0),
    	PESSIMO("§4Péssimo", (short)15, 1),
    	RUIM("§cRuim", (short)14, 2),
    	OK("§eOK", (short)4, 3),
    	BOM("§aBom", (short)5, 4),
    	LENDARIO("§6Lendário", (short)1, 5);
    	public String nome;		
    	public short data;
    	public int points;
	    Vote(String nome, short data, int points) {
			this.nome = nome;
			this.data = data;
			this.points = points;
		}
	    public static Vote byData(short s) {
	    	for(Vote vote : values()) {
	    		if(vote.data == s)return vote;
	    	}
	    	return null;
	    }
	}
    
}
