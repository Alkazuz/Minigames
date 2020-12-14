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
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPainting;

public class Round implements Listener
{
	public static HashMap<String, Long> delay = new HashMap<String, Long>();
    public AtomicInteger rodada;
    public int id;
    public Map<DetetivePlayer, Integer> votes;
    public Map<Player, DetetivePlayer> players;
    public Map<String, Integer> vipShop;
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
        this.votes = Collections.synchronizedMap(new HashMap<DetetivePlayer, Integer>());
        this.players = Collections.synchronizedMap(new HashMap<Player, DetetivePlayer>());
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
    
    public void update() {
    	if(counter !=null) {
    		this.counter.timer--;
    	}
    	
        if (this.state == RoundState.IN_PROGRESS) {
        	if(players.size() == 0) {
        		this.state = RoundState.FINISHED;
        		return;
        	}
            String msg = this.counter.getMessageAndUpdate(this.totalPlayers());
            synchronized (players) {
        		Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
            	while(iterator.hasNext()) {
            		Player p = iterator.next();
            		 ActionBarAPI.sendActionBar(p, msg);
                     if (this.counter.timer <= 5) {
                         if (this.counter.timer == 5) {
                             TitleAPI.sendTitle(p, 0, 19, 0, "§65", "");
                         }
                         if (this.counter.timer == 4) {
                             TitleAPI.sendTitle(p, 0, 19, 0, "§64", "");
                         }
                         if (this.counter.timer == 3) {
                             TitleAPI.sendTitle(p, 0, 19, 0, "§e3", "");
                         }
                         if (this.counter.timer == 2) {
                             TitleAPI.sendTitle(p, 0, 19, 0, "§c2", "");
                         }
                         if (this.counter.timer == 1) {
                             TitleAPI.sendTitle(p, 0, 19, 0, "§41", "");
                         }
                         if (this.counter.timer == 0) {
                             this.nextRound();
                         }
                         p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                     }
            	}
            }
        }
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            //int playersTotal = this.totalPlayers();
            //String msg2 = this.counter.getMessageAndUpdate(playersTotal);
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
            this.level.startSpawn.getWorld().setTime(20000L);
            int countAssassin = players.size() / 5;
            if (countAssassin <= 0) {
                countAssassin = 1;
            }
            Main.theInstance().finder.update();
            List<Player> list = new ArrayList<Player>(this.players.keySet());
            while (this.getAssassinCount() < countAssassin) {
                Player random = list.get(new Random().nextInt(list.size()));
                DetetivePlayer dp = this.players.get(random);
                if (dp.assassin) {
                    continue;
                }
                dp.assassin = true;
            }
            Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
            while(iterator.hasNext()) {
            	Player p = iterator.next();
                try {
                	 p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    DetetivePlayer dp2 = this.players.get(p);
                    for (PotionEffect pe : p.getActivePotionEffects()) {
                        p.removePotionEffect(pe.getType());
                    }
                    PlayerData fromNick = PlayerManager.fromNick(p.getName());
                    ++fromNick.partidas;
                    for (String n : MinigameConfig.GAME_START) {
                        p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
                    }
                    if (dp2.assassin) {
                        p.sendMessage(MinigameConfig.game_start_assassin);
                    }
                    else {
                        p.sendMessage(MinigameConfig.game_start_victim);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 5));
                    }
                }
                catch (Exception ex) {ex.printStackTrace();}
            }
            this.nextRound();
        }
    }
    
    public void nextRound() {
        List<DetetivePlayer> keys = new ArrayList<DetetivePlayer>(this.votes.keySet());
        Collections.sort(keys, new Comparator<DetetivePlayer>() {
            @Override
            public int compare(DetetivePlayer c1, DetetivePlayer c2) {
                Double o1 = (double)Round.this.votes.get(c1);
                Double o2 = (double)Round.this.votes.get(c2);
                return o2.compareTo(o1);
            }
        });
        this.counter.timer = 60;
        if (this.rodada.get() != 0) {
        	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
        	while(iterator.hasNext()) {
        		Player p = iterator.next();
        		DetetivePlayer dp = this.players.get(p);
                if (!dp.kill && dp.assassin) {
                    PlayerData fromNick = PlayerManager.fromNick(p.getName());
                    ++fromNick.losesAsAssassin;
                    this.broadcast(MinigameConfig.KILLER_ROUND.replace("{0}", p.getName()));
                    p.sendMessage(MinigameConfig.lose);
                    this.removePlayer(p);
                }
        	}
        }
        if (keys.size() > 0) {
            DetetivePlayer dp2 = keys.get(0);
            Player p2 = dp2.thePlayer();
            if (dp2.assassin) {
                PlayerData fromNick2 = PlayerManager.fromNick(dp2.nick);
                ++fromNick2.losesAsAssassin;
                this.broadcast(MinigameConfig.FATED.replace("{0}", dp2.nick));
                if (p2 != null) {
                    p2.sendMessage(MinigameConfig.lose);
                }
                this.removePlayer(p2);
            }
            else {
                this.broadcast(MinigameConfig.FATED_FAIL.replace("{0}", dp2.nick));
            }
        }
        int assassins = this.getAssassinCount();
        if (assassins <= 0) {
            this.broadcast(MinigameConfig.win_victim);
            Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
            while(iterator.hasNext()) {
            	Player p2 = iterator.next();
            	DetetivePlayer dp3 = this.players.get(p2);
                if (!dp3.assassin) {
                    PlayerData fromNick3 = PlayerManager.fromNick(dp3.nick);
                    ++fromNick3.winAsVictim;
                    PlayerData fromNick4 = PlayerManager.fromNick(dp3.nick);
                    ++fromNick4.winTotal;
                    p2.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
                    try {
                        Main.theInstance().economy.depositPlayer((OfflinePlayer)p2, (double)MinigameConfig.MONEY);
                    }
                    catch (Exception ex) {}
                }else {
                	p2.sendMessage(MinigameConfig.lose);
                }
                this.removePlayer(p2);
            }
            
            this.state = RoundState.FINISHED;
            return;
        }
        if (this.getVictimCount() <= assassins && rodada.get() > 0) {
            this.broadcast(MinigameConfig.win_assassin);
            Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
            while(iterator.hasNext()) {
            	Player p2 = iterator.next();
                DetetivePlayer dp3 = this.players.get(p2);
                if (dp3.assassin) {
                    PlayerData fromNick5 = PlayerManager.fromNick(p2.getName());
                    ++fromNick5.winAsAssassin;
                    PlayerData fromNick6 = PlayerManager.fromNick(p2.getName());
                    ++fromNick6.winTotal;
                    Main.theInstance().economy.depositPlayer((OfflinePlayer)p2, (double)MinigameConfig.MONEY);
                    p2.sendMessage(MinigameConfig.win_you.replace("{0}", String.valueOf(MinigameConfig.MONEY)));
                }else {
                	p2.sendMessage(MinigameConfig.lose);
                }
                this.removePlayer(p2);
            }
            this.state = RoundState.FINISHED;
            return;
        }
        this.votes.clear();
        this.rodada.getAndIncrement();
        Iterator<Player> iterator4 = new ArrayList<>(players.keySet()).iterator();
        while (iterator4.hasNext()) {
            Player p2 = iterator4.next();
            DetetivePlayer dp3 = this.players.get(p2);
            dp3.kill = false;
            dp3.voted = false;
        }
        updateScoreboard();
        this.broadcast(MinigameConfig.NEXT_ROUND.replace("{round}", String.valueOf(rodada.get())).replace("{1}", String.valueOf(this.getVictimCount())).replace("{0}", String.valueOf(this.getAssassinCount())));
        Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
        while(iterator.hasNext()) {
        	Player p2 = iterator.next();
            DetetivePlayer dp3 = this.players.get(p2);
            if (dp3.assassin) {
                p2.sendMessage(MinigameConfig.killer_next_round);
            }
        }
    }
    
    public int getAssassinCount() {
        int i = 0;
        for (DetetivePlayer dp : this.players.values()) {
            if (dp.assassin) {
                ++i;
            }
        }
        return i;
    }
    
    public int getVictimCount() {
        int i = 0;
        for (DetetivePlayer dp : this.players.values()) {
            if (!dp.assassin) {
                ++i;
            }
        }
        return i;
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
        this.players.put(p, new DetetivePlayer(p.getName()));
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
            	Iterator<Player> iterator = new ArrayList<>(players.keySet()).iterator();
                while(iterator.hasNext()) {
                	Player all = iterator.next();
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
        p.getInventory().setItem(7, ShopMenu.shop);
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
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
        	removePlayer(p);
            if (this.state == RoundState.IN_PROGRESS) {
                this.broadcast(MinigameConfig.ASSASSIN_QUIT.replace("{0}", p.getName()));
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	if (this.hasPlayer(player) && player.getItemInHand() != null && player.getItemInHand().getType() == Material.BUCKET) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player player = (Player)event.getEntity();
            
            if (this.hasPlayer(player) && this.hasPlayer(damager)) {
            	if(player.getLocation().distance(damager.getLocation()) > 1.5D) {
            		event.setCancelled(true);
                    return;
            	}
                DetetivePlayer dp = this.players.get(damager);
                DetetivePlayer dpDamaged = this.players.get(player);
                if (dpDamaged == null) {
                    event.setCancelled(true);
                    return;
                }
                if (!dp.assassin) {
                    event.setCancelled(true);
                    return;
                }
                if (dp.kill) {
                    event.setCancelled(true);
                    return;
                }
                if (dpDamaged.assassin) {
                    damager.sendMessage(MinigameConfig.KILL_ERROR);
                    event.setCancelled(true);
                    return;
                }
                dp.kill = true;
                this.broadcast(MinigameConfig.KILLED.replace("{0}", player.getName()));
                PlayerData fromNick = PlayerManager.fromNick(player.getName());
                ++fromNick.losesAsVictim;
                player.getLocation().getWorld().playSound(player.getLocation(), Sound.HURT_FLESH, 1.0f, 1.0f);
                player.sendMessage(MinigameConfig.lose);
                this.removePlayer(player);
            }
        }
    }
}
