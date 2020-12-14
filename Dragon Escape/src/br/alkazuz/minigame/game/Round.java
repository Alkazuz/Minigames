package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import br.alkazuz.minigame.api.ActionBarAPI;
import br.alkazuz.minigame.api.PlayerAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TagAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.dragon.EntityDragonEscape;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;
import br.alkazuz.minigame.utils.Utils;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityItemFrame;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPainting;
import net.minecraft.server.v1_8_R3.WorldServer;

public class Round implements Listener
{
    public int id;
    public HashMap<UUID, Player> players;
    public Map<String, Integer> vipShop;
    public RoundState state;
    public long timeStarted;
    public long timeLoaded;
    public RoundLevel level;
    public RoundCounter counter;
    private int winner;
    public String first;
    public String second;
    public String three;
    public EnderDragon dragon;
    private boolean valid = false;
    
    @Override
    public String toString() {
        return String.format("Round@[ID=%s,State=%s,World=%s,PlayersSize=%s]", String.valueOf(this.id), this.state.toString(), this.level.world.getName(), String.valueOf(this.players.size()));
    }
    
    public Round(RoundLevel level) {
        this.players = new HashMap<UUID, Player>();
        this.vipShop = Collections.synchronizedMap(new HashMap<String, Integer>());
        this.state = RoundState.AVAILABLE;
        this.winner = 1;
        this.first = null;
        this.second = null;
        this.three = null;
        this.level = level;
        if (level == null) {
            this.state = RoundState.LOADING;
        }
        else {
            for (Entity e : level.dragonSpawn.getWorld().getEntities()) {
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
        for(Round round : Main.theInstance().rounds) {
        	if(round.level.startSpawn.getWorld().getName().equals(level.startSpawn.getWorld().getName())) {
        		state = RoundState.FINISHED;
    		}
        }
    }
    
    public boolean isValidWorld() {
    	
    	level.lobbySpawn.getWorld().setSpawnLocation(level.lobbySpawn.getBlockX(), level.lobbySpawn.getBlockY(), level.lobbySpawn.getBlockZ());
    	loadChunks();
    	
    	List<Material> list = Utils.getAmountMaterials(level.lobbySpawn);
    	System.out.println(list.size());
    	for(Material m : list) {
    		System.out.println(m.toString());
    	}
    	if(list.size() < 3 && !list.contains(Material.GLASS)) {
    		System.out.println("Mundo inválido");
    		return false;
    	}
    	return true;
    }
    
    public void loadChunks() {
    	int x = level.lobbySpawn.getBlockX() - 250;
        int i = x = 65036;
        System.out.println(level.lobbySpawn.getWorld().getBlockAt(level.startSpawn.clone()).toString());
    	while (i < 250) {
            int z = level.lobbySpawn.getBlockZ() - 250;
            int j = z = 65036;
            while (j < 250) {
                    final Chunk chunk = level.lobbySpawn.getWorld().getBlockAt(x, 64, z).getChunk();
                    System.out.println(level.lobbySpawn.getWorld().getBlockAt(x, 64, z).toString());
                    if (!chunk.isLoaded()) {
                    	System.out.println(chunk.toString() + " carregada.");
                        chunk.load(true);
                }
                j = ++z;
            }
            i = ++x;
        }
    }
    
    public void validateWorld() {
    	if(isValidWorld()) return;
    	this.state = RoundState.FINISHED;
		Main.theInstance().rounds.remove(this);
		Iterator<Player> iterator = players.values().iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
        	removePlayerMD(p);
        }
    }
    
    public void update() {
    	
    	if(counter !=null) {
    		this.counter.timer--;
    	}
    	
        if (this.state == RoundState.IN_PROGRESS) {
            if (this.players.size() == 0) {
                if (this.dragon != null) {
                    this.dragon.remove();
                }
                this.state = RoundState.FINISHED;
            }
            if (this.dragon != null) {
                for (int x = -5; x <= 5; ++x) {
                    for (int y = -5; y <= 5; ++y) {
                        for (int z = -5; z <= 5; ++z) {
                            Block block = this.level.world.getBlockAt(this.dragon.getLocation().getBlockX() + x, this.dragon.getLocation().getBlockY() + y, this.dragon.getLocation().getBlockZ() + z);
                            if (block != null && block.getType() != Material.AIR) {
                                block.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            String msg = this.counter.getMessageAndUpdate(this.totalPlayers());
            Iterator<Player> iterator = players.values().iterator();
            while(iterator.hasNext()) {
            	Player p = iterator.next();
            	 ActionBarAPI.sendActionBar(p, msg);
                
                if (this.counter.timer <= 5 && this.counter.starting) {
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
                    p.playSound(p.getLocation(), Sound.CLICK, 1.0f, 1.0f);
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
            this.state = RoundState.IN_PROGRESS;
            this.timeStarted = System.currentTimeMillis();
            Iterator<Player> iterator = players.values().iterator();
            while(iterator.hasNext()) {
            	Player p = iterator.next();
                try {
                    PlayerData fromNick;
                    PlayerData data = fromNick = PlayerManager.fromNick(p.getName());
                    ++fromNick.partidas;
                    p.teleport(this.level.startSpawn);
                    for (String n : MinigameConfig.GAME_START) {
                        p.sendMessage(n.replace("{0}", String.valueOf(MinigameConfig.MONEY_FIRST)));
                    }
                    ScoreBoard.createScoreBoard(p, this);
                    ScoreBoard.updateScoreBoard(p, this, data);
                }
                catch (Exception ex) {}
            }
            this.spawnDragon();
        }
    }
    
    public void spawnDragon() {
        WorldServer handle = ((CraftWorld)this.level.dragonSpawn.getWorld()).getHandle();
        EntityEnderDragon entity = new EntityDragonEscape((net.minecraft.server.v1_8_R3.World)handle);
        entity.setLocation(this.level.dragonSpawn.getX(), this.level.dragonSpawn.getY(), this.level.dragonSpawn.getZ(), this.level.dragonSpawn.getYaw(), this.level.dragonSpawn.getPitch());
        ((CraftLivingEntity)entity.getBukkitEntity()).setRemoveWhenFarAway(false);
        handle.addEntity((net.minecraft.server.v1_8_R3.Entity)entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.dragon = (EnderDragon)entity.getBukkitEntity();
        for (Player all : this.players.values()) {
            all.playSound(all.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0f, 1.0f);
        }
    }
    
    public void joinPlayer(Player p) {
        p.teleport(this.level.lobbySpawn);
        TagAPI.apply("", p);
        PlayerAPI.resetPlayer(p);
        this.players.put(p.getUniqueId(), p);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(p);
            p.hidePlayer(all);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
            	 p.teleport(level.lobbySpawn);
            	Iterator<Player> iterator = new ArrayList<>(players.values()).iterator();
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
        p.getInventory().setItem(7, ShopMenu.shop);
        p.getInventory().setItem(8, ShopMenu.vip);
        ScoreBoard.createScoreBoardLobby(p, this, data);
        ScoreBoard.updateScoreBoardLobby(p, this, data);
        for (String d : MinigameConfig.WELCOME) {
            p.sendMessage(d.replace("{0}", String.valueOf(this.players.size())));
        }
    }
    
    public boolean removePlayer(Player p) {
        if (this.players.remove(p.getUniqueId()) != null) {
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
        UUID uuid = p.getUniqueId();
        return this.players.containsKey(uuid);
    }
    
    public void broadcast(String message) {
    	Iterator<Player> iterator = players.values().iterator();
        while(iterator.hasNext()) {
        	Player p = iterator.next();
            p.sendMessage(message);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p)) {
            this.players.remove(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                p.spigot().respawn();
            }
        }, 2L);
        if (this.hasPlayer(p) && this.state == RoundState.AVAILABLE) {
            p.teleport(this.level.lobbySpawn);
        }
        if (this.hasPlayer(p) && this.state == RoundState.IN_PROGRESS) {
            p.sendMessage(MinigameConfig.LOSE);
            this.removePlayer(p);
            this.broadcast(MinigameConfig.DEATH.replace("{0}", p.getName()));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p) && this.state == RoundState.AVAILABLE && p.getLocation().getBlockY() <= -1) {
            p.teleport(this.level.lobbySpawn);
        }
        if (this.hasPlayer(p) && this.state == RoundState.IN_PROGRESS) {
            if (p.getLocation().getBlockY() <= -1) {
                p.sendMessage(MinigameConfig.LOSE);
                this.removePlayer(p);
                this.broadcast(MinigameConfig.DEATH.replace("{0}", p.getName()));
                return;
            }
            Block block = p.getWorld().getBlockAt(p.getLocation().clone().add(0.0, -1.0, 0.0));
            if (block != null && block.getType() == Material.BEACON) {
                String lugar = "";
                int coins = 15;
                if (this.winner == 1) {
                    this.winner = 2;
                    coins = MinigameConfig.MONEY_FIRST;
                    lugar = "primeiro lugar";
                    this.first = p.getName();
                }
                else if (this.winner == 2) {
                    this.winner = 3;
                    coins = MinigameConfig.MONEY_SECOND;
                    lugar = "segundo lugar";
                    this.second = p.getName();
                }
                else if (this.winner == 3) {
                    coins = MinigameConfig.MONEY_TREE;
                    lugar = "terceiro lugar";
                    this.three = p.getName();
                }
                PlayerData data = PlayerManager.fromNick(p.getName());
                this.broadcast(MinigameConfig.WIN_OTHER.replace("{1}", lugar).replace("{0}", p.getName()));
                p.sendMessage(MinigameConfig.WIN_YOU.replace("%n%", "\n").replace("&", "§").replace("{0}", String.valueOf(coins)));
                Main.theInstance().economy.depositPlayer((OfflinePlayer)p, (double)coins);
                PlayerData playerData = data;
                ++playerData.winTotal;
                Iterator<Player> iterator = players.values().iterator();
                while(iterator.hasNext()) {
                	Player all = iterator.next();
                    PlayerData data2 = PlayerManager.fromNick(all.getName());
                    ScoreBoard.updateScoreBoard(all, this, data2);
                }
                this.removePlayer(p);
                if (this.winner == 3) {
                    for (Player all : this.players.values()) {
                        all.sendMessage(MinigameConfig.LOSE);
                        this.removePlayer(all);
                    }
                    if (this.dragon != null) {
                        this.dragon.remove();
                    }
                    this.state = RoundState.FINISHED;
                }
                if (this.winner == 1) {
                    this.winner = 2;
                }
                if (this.winner == 2) {
                    this.winner = 3;
                }
            }
        }
    }
}
