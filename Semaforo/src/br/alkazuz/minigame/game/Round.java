package br.alkazuz.minigame.game;

import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.material.*;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.*;

import br.alkazuz.minigame.api.*;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.main.Main;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopMenu;
import br.alkazuz.minigame.utils.*;
import net.milkbowl.vault.economy.*;

public class Round implements Listener
{
    public int id;
    public HashMap<UUID, Player> players;
    public Map<String, Integer> vipShop;
    public RoundState state;
    public long timeLoaded;
    public long timeStarted;
    public RoundLevel level;
    public RoundCounter counter;
    
    private LightColor lightColor = LightColor.Green;
    private long lightChangeTime;
    private long lastTimeoutMsg;
    
    private Random rng = new Random();
    private Set<Player> secondChanceUsers = Sets.newHashSet();
    
    
    @Override
    public String toString() {
        return String.format("Round@[ID=%s,State=%s,World=%s,PlayersSize=%s]", String.valueOf(this.id), this.state.toString(), this.level.world.getName(), String.valueOf(this.players.size()));
    }
    
    public Round(RoundLevel level) {
        this.players = new HashMap<UUID, Player>();
        this.vipShop = Collections.synchronizedMap(new HashMap<String, Integer>());
        this.state = RoundState.AVAILABLE;
        this.level = level;
        this.timeLoaded = System.currentTimeMillis();
        
        this.id = RoundLevel.worldCounter.get();
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)Main.theInstance());
    }
    
    public void update() {
        if (this.state == RoundState.AVAILABLE) {
            if (this.counter == null) {
                this.counter = new RoundCounter(this);
            }
            
            
            String msg = this.counter.getMessageAndUpdate(this.totalPlayers());
            for (Player p : this.players.values()) {
                ActionBarAPI.sendActionBar(p, msg);
                ScoreBoard.updateScoreBoardLobby(p, this, PlayerManager.fromNick(p.getName()));
                
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
        else if (state == RoundState.IN_PROGRESS) {
            updateLight();
            checkTimeout();
        }
    }
    
    private void updateLight()
    {
        long now = System.currentTimeMillis();
        
        if (now < lightChangeTime) {
            return;
        }

        lightColor = lightColor.next();
        lightChangeTime = now + Utils.random(rng, lightColor.timeMin, lightColor.timeMax);
        
        secondChanceUsers.clear();
        
        String lightAction = getLightAction(lightColor);
        ItemStack lightItem = createLightItem(lightColor);
        
        for (Player p : this.players.values()) {
            PlayerInventory inv = p.getInventory();
            
            for (int i = 0; i < 9; i++) {
                ItemStack currItem = inv.getItem(i);
                if (currItem == null || currItem.getType() != Material.SLIME_BALL) {
                    inv.setItem(i, lightItem);
                }
            }
            ActionBarAPI.sendActionBar(p, lightAction);
        }
    }
    
    private ItemStack createLightItem(LightColor col)
    {
        Wool wool = new Wool(Material.STAINED_CLAY);
        wool.setColor(col.dye);
        ItemStack item = wool.toItemStack(1);
        
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getLightAction(col));
        item.setItemMeta(meta);
        
        return item;
    }
    private String getLightAction(LightColor col)
    {
        switch (col) {
            case Green:  return MinigameConfig.LIGHT_ACTION_GREEN;
            case Yellow: return MinigameConfig.LIGHT_ACTION_YELLOW;
            case Red:    return MinigameConfig.LIGHT_ACTION_RED;
            default: throw new IllegalStateException();
        }
    }

    private void checkTimeout()
    {
        long now = System.currentTimeMillis();
        
        long maxDuration = 3 * 60 * 1000;
        long timeRemaining = (timeStarted + maxDuration) - now;
        
        if (timeRemaining <= 0) {
            for (Player p : players.values()) {
                p.sendMessage(MinigameConfig.LOSE_YOU);
                removePlayerMD(p);
            }
            players.clear();
            
            state = RoundState.FINISHED;
        } 
        else if (now - lastTimeoutMsg > 60 * 1000) {
            lastTimeoutMsg = System.currentTimeMillis();
            broadcast(String.format("§cFim da partida em %d minutos!", timeRemaining / 60_000));
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
            this.timeStarted = System.currentTimeMillis();
            for (Player p : this.players.values()) {
                PlayerData data = PlayerManager.fromNick(p.getName());
                data.partidas++;
                
                for (String n : MinigameConfig.GAME_START) {
                    p.sendMessage(n.replace("{0}", MinigameConfig.MONEY_WINNER + ""));
                }
                ScoreBoard.createScoreBoard(p, this);
            }
            this.level.startWall.fill(this.level.world, Material.AIR, null);
            this.level.endWall.fill(this.level.world, Material.AIR, null);

            this.lightChangeTime = 0;
            this.lightColor = LightColor.Red;
            updateLight();
            
            this.state = RoundState.IN_PROGRESS;
        }
    }
    
    public void joinPlayer(Player p) {
        p.teleport(this.level.spawnPos);
        TagAPI.apply("", "", p, true);
        PlayerAPI.resetPlayer(p);
        this.players.put(p.getUniqueId(), p);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.hidePlayer(p);
            p.hidePlayer(all);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                for (Player all : Round.this.players.values()) {
                    all.showPlayer(p);
                    p.showPlayer(all);
                }
            }
        }, 20L);
        PlayerData data = PlayerManager.fromNick(p.getName());
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
            this.removePlayerMD(p);
            return true;
        }
        return false;
    }
    
    private void removePlayerMD(Player p) {
        PlayerData data = PlayerManager.fromNick(p.getName());
        data.save();
    
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
        for (Player p : this.players.values()) {
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
            p.teleport(this.level.spawnPos);
        }
        if (this.hasPlayer(p) && this.state == RoundState.IN_PROGRESS) {
            p.sendMessage(MinigameConfig.LOSE_YOU);
            this.removePlayer(p);
            this.broadcast(MinigameConfig.DEATH.replace("{0}", p.getName()));
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (this.hasPlayer(p) && this.state == RoundState.AVAILABLE && p.getLocation().getBlockY() <= -1) {
            p.teleport(this.level.spawnPos);
        }
        if (this.hasPlayer(p) && this.state == RoundState.IN_PROGRESS) {
            final double DELTA_THRESHOLD = 0.1;
            if (this.lightColor == LightColor.Red && e.getFrom().distance(e.getTo()) > DELTA_THRESHOLD) {
                if (p.getInventory().contains(Material.SLIME_BALL)) {
                    secondChanceUsers.add(p);
                    p.getInventory().remove(Material.SLIME_BALL);
                    p.sendMessage(MinigameConfig.USE_SECOND_CHANCE);
                }
                if (!secondChanceUsers.contains(p)) {
                    this.broadcast(MinigameConfig.LOSE_OTHER.replace("{0}", p.getName()));
                    p.sendMessage(MinigameConfig.LOSE_YOU);
                    removePlayer(p);
                }
            }
            else if (this.level.finishRegion.contains(p.getLocation())) {
                int coins = MinigameConfig.MONEY_WINNER;

                PlayerData data = PlayerManager.fromNick(p.getName());
                this.broadcast(MinigameConfig.WIN_OTHER.replace("{0}", p.getName()));
                p.sendMessage(MinigameConfig.WIN_YOU.replace("{0}", coins + ""));
                
                Economy economy = Main.theInstance().economy;
                if (economy != null) {
                    economy.depositPlayer(p, coins);
                }
                data.winTotal++;
                this.state = RoundState.FINISHED;
            }
        }
    }
    
    private static enum LightColor 
    {
        Green(3.0f, 5.0f, DyeColor.GREEN), 
        Yellow(1.0f, 1.5f, DyeColor.YELLOW), 
        Red(1.2f, 1.5f, DyeColor.RED);

        public static final LightColor[] VALUES = values();
        
        public final int timeMin, timeMax;
        public final DyeColor dye;
        
        private LightColor(float timeMin, float timeMax, DyeColor dye)
        {
            this.timeMin = (int)(timeMin * 1000);
            this.timeMax = (int)(timeMax * 1000);
            this.dye = dye;
        }
        
        public LightColor next()
        {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }
}
