package br.alkazuz.minigame.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.boydti.fawe.bukkit.v0.BukkitQueue_0;
import com.boydti.fawe.util.SetQueue;
import com.huskehhh.mysql.mysql.MySQL;
import com.huskehhh.mysql.sqlite.SQLite;

import br.alkazuz.minigame.api.ActionBarAPI;
import br.alkazuz.minigame.api.ServerAPI;
import br.alkazuz.minigame.api.TitleAPI;
import br.alkazuz.minigame.commands.CommandAMinigame;
import br.alkazuz.minigame.data.PlayerData;
import br.alkazuz.minigame.data.PlayerManager;
import br.alkazuz.minigame.data.RankingUpdater;
import br.alkazuz.minigame.game.GameFinder;
import br.alkazuz.minigame.game.LevelInfo;
import br.alkazuz.minigame.game.MinigameConfig;
import br.alkazuz.minigame.game.Round;
import br.alkazuz.minigame.game.RoundLevel;
import br.alkazuz.minigame.game.RoundState;
import br.alkazuz.minigame.game.Themes;
import br.alkazuz.minigame.listener.MinigameListener;
import br.alkazuz.minigame.menu.UtilsListener;
import br.alkazuz.minigame.scoreboard.ScoreBoard;
import br.alkazuz.minigame.shop.ShopItemManager;
import br.alkazuz.minigame.shop.ShopListener;
import br.alkazuz.minigame.sql.SQLData;
import br.alkazuz.minigame.utils.ReflectionUtils;
import br.alkazuz.minigame.utils.Utils;
import br.alkazuz.minigame.utils.Version;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin implements Runnable
{
    private static Main instance;
    public FileConfiguration config;
    public FileConfiguration games;
    public Economy economy;
    private static Version version;
    public List<Round> rounds;
    private LevelInfo[] levels;
    private Random rng;
    Plugin plugin;
    
    public Main() {
        this.rounds = new CopyOnWriteArrayList<Round>();
        this.levels = new LevelInfo[0];
        this.rng = new Random();
    }
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public void writeMessage(String msg) {
        Bukkit.getConsoleSender().sendMessage("§e["+MinigameConfig.NAME+"] §7" + msg);
    }
    
    public void onLoad() {
        File[] listFiles;
        for (int length = (listFiles = this.getServer().getWorldContainer().listFiles()).length, i = 0; i < length; ++i) {
            File path = listFiles[i];
            if (path.getName().startsWith(MinigameConfig.LEVE_NAME_PREFIX)) {
                try {
                    FileUtils.deleteDirectory(path);
                }
                catch (IOException ex) {}
            }
            if (path.getName().startsWith("world")) {
                File[] listFiles2;
                for (int length2 = (listFiles2 = path.listFiles(File::isDirectory)).length, j = 0; j < length2; ++j) {
                    File all = listFiles2[j];
                    if (!all.getName().equals("stats")) {
                        if (!all.getName().equals("playerdata")) {
                            continue;
                        }
                    }
                    try {
                        FileUtils.deleteDirectory(all);
                    }
                    catch (IOException ex2) {}
                }
            }
        }
    }
    
    public void onEnable() {
        Main.instance = this;
        this.config = ConfigManager.getConfig("config");
        this.games = ConfigManager.getConfig("games");
        Main.version = Version.getServerVersion();
        MinigameConfig.LAST_PLAYER_JOINED = System.currentTimeMillis();
        MinigameConfig.STARTED = System.currentTimeMillis();
        List<LevelInfo> lvls = new ArrayList<LevelInfo>();
        for (Map<?, ?> level : this.games.getMapList("levels")) {
            lvls.add(LevelInfo.deserialize((Map<String, Object>)level));
        }
        lvls.toArray(this.levels = new LevelInfo[lvls.size()]);
        this.loadResources();
        this.loadAPIs();
        this.registerEvents();
        this.setupDatabase();
        this.setupEconomy();
        this.registerCommand();
        Themes.load(this);
        
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, new Runnable() {
            @Override
            public void run() {
            	//Main.this.createRound(null);
            	Main.this.getServer().getScheduler().scheduleSyncRepeatingTask(Main.this, Main.this, 20L, 20L);
            }
        });
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)theInstance(), new Runnable() {
            @Override
            public void run() {
                Bukkit.getScheduler().runTaskAsynchronously((Plugin)Main.theInstance(), new Runnable() {
                    @Override
                    public void run() {
                        List<PlayerData> all = new ArrayList<PlayerData>(PlayerManager.data.values());
                        Collections.sort(all, new Comparator<PlayerData>() {
                            @Override
                            public int compare(PlayerData c1, PlayerData c2) {
                                Float o1 = (float)c1.winTotal;
                                Float o2 = (float)c2.winTotal;
                                return o2.compareTo(o1);
                            }
                        });
                        RankingUpdater.ranking.clear();
                        int index = 1;
                        for (PlayerData a : all) {
                            RankingUpdater.ranking.put(a.nick, index);
                            ++index;
                        }
                    }
                });
            }
        }, 0L, 2400L);
        setupRound();
    }
    
    public GameFinder finder;
    public void setupRound() {
    	finder = new GameFinder(this);
    	 Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)theInstance(), new Runnable() {
             @Override
             public void run() {
            	 finder.update();
             }
         }, 0L, 20L);
    }
    
    public void run() {
    	for(Round round : rounds) {
    		for(Round roundd : rounds) {
        		if(roundd != round && (round.level.startSpawn.getWorld().getName().equals(roundd.level.startSpawn.getWorld().getName()))) {
        			if(roundd.state == RoundState.AVAILABLE && roundd.players.size() == 0) {
        				roundd.state = RoundState.FINISHED;
    	    			System.out.println("Mapas iguais");
        			}
        		}else if(roundd != round && roundd.id == round.id && roundd.state == RoundState.AVAILABLE) {
    				//roundd.state = RoundState.FINISHED;
	    			//Main.this.createRound(null);
	    			//System.out.println(roundd.id+" ID iguais");
	    		}
    		}
    	}
    	
    	Iterator<Round> iterator = rounds.iterator();
    	while(iterator.hasNext()) {
    		Round round = iterator.next();
    		round.update();
    	}
    	while(iterator.hasNext()) {
    		Round round = iterator.next();
    		if (round.state == RoundState.FINISHED) {
                round.cleanUp();
                this.createRound(round.level.world);
                this.rounds.remove(round);
            }
    	}
        if (System.currentTimeMillis() - MinigameConfig.STARTED >= TimeUnit.MINUTES.toMillis(30L)) {
            boolean canRestart = true;
            if (Bukkit.getOnlinePlayers().size() > 0) {
                canRestart = false;
            }
            if (canRestart) {
                Bukkit.shutdown();
                return;
            }
        }
        if (Bukkit.getOnlinePlayers().size() <= 0 && System.currentTimeMillis() - MinigameConfig.LAST_PLAYER_JOINED > 120000L) {
            Bukkit.shutdown();
        }
    }
    
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ServerAPI.sendPlayer(p, "lobby");
        }
    }
    
    public void createRound(World oldWorld) {
    	 Server sv = this.getServer();
         try{
         	if (oldWorld != null) {
         		sv.unloadWorld(oldWorld, false);
         	}
         }catch (Exception e) {
 			// TODO: handle exception
 		}
         BukkitScheduler scheduler = sv.getScheduler();
         File worldDir = sv.getWorldContainer();
         Logger logger = sv.getLogger();
         LevelInfo level = this.levels[this.rng.nextInt(this.levels.length)];
         String newName = RoundLevel.nextLevelName();
         File newWorld = new File(worldDir, newName);
         File dataFolder = this.getDataFolder();
        File oldWorldFile = (oldWorld == null) ? null : oldWorld.getWorldFolder();
        scheduler.runTaskAsynchronously((Plugin)this, new Runnable() {
            @Override
            public void run() {
                if (oldWorldFile != null) {
                    try {
                        FileUtils.deleteDirectory(oldWorldFile);
                    }
                    catch (Exception e) {
                        logger.log(Level.WARNING, "Failed to delete world folder", e);
                    }
                }
                try {
                    File folder = new File("plugins/" + Main.theInstance().getDescription().getName() + "/maps/" + level.world);
                    Utils.copyDirectory(folder, newWorld);
                }
                catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to copy level", e);
                }
                WorldCreator wc = new WorldCreator(newName);
                System.out.println(newName + " mundo sendo gerado.");
                wc.generateStructures(false);
                BukkitQueue_0 queue = (BukkitQueue_0)SetQueue.IMP.getNewQueue(newName, false, false);
                World world = queue.createWorld(wc);
                scheduler.runTask((Plugin)Main.this, new Runnable() {
                    @Override
                    public void run() {
                        world.setAnimalSpawnLimit(1);
                        world.setGameRuleValue("doMobSpawning", "false");
                        world.setGameRuleValue("doDaylightCycle", "false");
                        world.setGameRuleValue("mobGriefing", "false");
                        world.setAutoSave(false);
                        Main.this.rounds.add(new Round(RoundLevel.worldCounter.intValue(), level.createLevel(world)));
                    }
                });
            }
        });
    }
    
    public Round getRound() {
        return GameFinder.getRound();
    }
    
    public void registerCommand() {
        this.getCommand("abuildbattle").setExecutor((CommandExecutor)new CommandAMinigame());
    }
    
    public void loadResources() {
        MinigameConfig.load(this);
        Main.version = Version.getServerVersion();
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        new ShopItemManager();
    }
    
    public void loadAPIs() {
        ReflectionUtils.loadUtils();
        ActionBarAPI.load();
        TitleAPI.load();
    }
    
    public void registerEvents() {
        Bukkit.getPluginManager().registerEvents((Listener)new MinigameListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ScoreBoard(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ShopListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new UtilsListener(), (Plugin)this);
    }
    
    public void setupDatabase() {
        Label_0192: {
            if (this.config.getBoolean("SQL.MySQL")) {
                try {
                    MySQL msql = new MySQL(this.config.getString("SQL.host"), this.config.getString("SQL.port"), this.config.getString("SQL.database"), this.config.getString("SQL.user"), this.config.getString("SQL.pass"));
                    SQLData.con = msql.openConnection();
                    SQLData.statement = SQLData.con.createStatement();
                    this.writeMessage("§aO Servidor est\u00e1 usando MySQL");
                    break Label_0192;
                }
                catch (Exception e1) {
                    this.writeMessage("§cFalha ao conectar o MySQL, usando o SQLLite");
                    try {
                        SQLite sql = new SQLite("data.db", (Plugin)this);
                        SQLData.con = sql.openConnection();
                        SQLData.statement = SQLData.con.createStatement();
                    }
                    catch (Exception e2) {
                        this.writeMessage("§cFalha ao configurar o SQLite");
                    }
                    break Label_0192;
                }
            }
            try {
                SQLite sql2 = new SQLite("data.db", (Plugin)this);
                SQLData.con = sql2.openConnection();
                SQLData.statement = SQLData.con.createStatement();
            }
            catch (Exception e1) {
                this.writeMessage("§cFalha ao configurar o SQLite");
            }
        }
        SQLData.CriarTabela();
        new PlayerManager();
    }
    
    public static boolean isOldVersion() {
        return Main.version == Version.v1_7 || Main.version == Version.v1_6 || Main.version == Version.v1_5;
    }
    
    public static boolean isVeryOldVersion() {
        return Main.version == Version.v1_5 || Main.version == Version.v1_6;
    }
    
    public static boolean isNewVersion() {
        return Main.version == Version.v1_14 || Main.version == Version.v1_13 || Main.version == Version.v1_12 || Main.version == Version.v1_11;
    }
    
    public static boolean isVeryNewVersion() {
        return Main.version == Version.v1_14 || Main.version == Version.v1_13;
    }
    
    public static Version getVersion() {
        return Main.version;
    }
    
    private boolean setupEconomy() {
        RegisteredServiceProvider registration = Bukkit.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (registration != null) {
            this.economy = (Economy)registration.getProvider();
            return true;
        }
        return false;
    }
}
