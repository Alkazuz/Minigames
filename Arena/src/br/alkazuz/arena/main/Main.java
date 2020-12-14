package br.alkazuz.arena.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.boydti.fawe.bukkit.v0.BukkitQueue_0;
import com.boydti.fawe.util.SetQueue;
import com.huskehhh.mysql.mysql.MySQL;
import com.huskehhh.mysql.sqlite.SQLite;

import br.alkazuz.arena.api.ActionBarAPI;
import br.alkazuz.arena.api.InvencibleAPI;
import br.alkazuz.arena.data.PlayerData;
import br.alkazuz.arena.game.Arena;
import br.alkazuz.arena.game.ArenaListener;
import br.alkazuz.arena.game.GameManager;
import br.alkazuz.arena.game.LevelInfo;
import br.alkazuz.arena.game.MinigameConfig;
import br.alkazuz.arena.game.RoundLevel;
import br.alkazuz.arena.scoreboard.ScoreBoard;
import br.alkazuz.arena.shop.ShopItemManager;
import br.alkazuz.arena.shop.ShopListener;
import br.alkazuz.arena.sql.SQLData;
import br.alkazuz.arena.utils.ReflectionUtils;
import br.alkazuz.arena.utils.Utils;
import br.alkazuz.arena.utils.Version;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin
{
    private static Main instance;
    public FileConfiguration config;
    public FileConfiguration games;
    public Economy economy;
	public LevelInfo[] levels = new LevelInfo[0];
    private static Version version;
    
    public static Main theInstance() {
        return Main.instance;
    }
    
    public void writeMessage(final String msg) {
        Bukkit.getConsoleSender().sendMessage("§e[Arena] §7" + msg);
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
        Bukkit.getPluginManager().registerEvents((Listener)new ScoreBoard(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ShopListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new ArenaListener(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new InvencibleAPI(), (Plugin)this);
        Bukkit.getMessenger().registerOutgoingPluginChannel((Plugin)this, "BungeeCord");
        new ShopItemManager();
        this.setupEconomy();
        Main.version = Version.getServerVersion();
        ReflectionUtils.loadUtils();
        ActionBarAPI.load();
        MinigameConfig.load(this);
        Label_0316: {
            if (this.config.getBoolean("SQL.MySQL")) {
                try {
                    final MySQL msql = new MySQL(this.config.getString("SQL.host"), this.config.getString("SQL.port"), this.config.getString("SQL.database"), this.config.getString("SQL.user"), this.config.getString("SQL.pass"));
                    SQLData.con = msql.openConnection();
                    SQLData.statement = SQLData.con.createStatement();
                    this.writeMessage("§aO Servidor est\u00e1 usando MySQL");
                    break Label_0316;
                }
                catch (Exception e1) {
                    this.writeMessage("§cFalha ao conectar o MySQL, usando o SQLLite");
                    try {
                        final SQLite sql = new SQLite("data.db", (Plugin)this);
                        SQLData.con = sql.openConnection();
                        SQLData.statement = SQLData.con.createStatement();
                    }
                    catch (Exception e2) {
                        this.writeMessage("§cFalha ao configurar o SQLite");
                    }
                    break Label_0316;
                }
            }
            try {
                final SQLite sql2 = new SQLite("data.db", (Plugin)this);
                SQLData.con = sql2.openConnection();
                SQLData.statement = SQLData.con.createStatement();
            }
            catch (Exception e1) {
                this.writeMessage("§cFalha ao configurar o SQLite");
            }
        }
        SQLData.CriarTabela();
        new PlayerData();
        List<LevelInfo> lvls = new ArrayList<LevelInfo>();
        for (Map<?, ?> level : games.getMapList("levels")) {
            lvls.add(LevelInfo.deserialize((Map<String, Object>)level));
        }
        lvls.toArray(levels = new LevelInfo[lvls.size()]);
        createRound(null, this);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, (Runnable)new Runnable() {
            @Override
            public void run() {
            	Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Main.theInstance(), (Runnable)new Runnable() {
                    @Override
                    public void run() {
                    	GameManager.arena = new Arena( MinigameConfig.MONEY);
                    	System.out.println("arena carregada.");
                    }
                }, 20L * 5);
            	
            }
        });
    }
    
    public static LevelInfo level;
    public void createRound(World oldWorld, Main main) {
    	Random rng = new Random();
   	 	Server sv = main.getServer();
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
         level = main.levels[rng.nextInt(main.levels.length)];
        String newName = RoundLevel.nextLevelName();
        File newWorld = new File(worldDir, newName);
        File dataFolder = main.getDataFolder();
       File oldWorldFile = (oldWorld == null) ? null : oldWorld.getWorldFolder();
       scheduler.runTaskAsynchronously(main, new Runnable() {
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
               wc.generateStructures(false);
               BukkitQueue_0 queue = (BukkitQueue_0)SetQueue.IMP.getNewQueue(newName, false, false);
               World world = queue.createWorld(wc);
               System.out.println("mundo sendo gerado");
               scheduler.runTask(main, new Runnable() {
                   @Override
                   public void run() {
                       world.setAnimalSpawnLimit(1);
                       world.setGameRuleValue("doMobSpawning", "false");
                       world.setGameRuleValue("doDaylightCycle", "false");
                       world.setGameRuleValue("mobGriefing", "false");
                       world.setAutoSave(false);
                       
                   }
               });
           }
       });
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
        final RegisteredServiceProvider registration = Bukkit.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (registration != null) {
            this.economy = (Economy)registration.getProvider();
            return true;
        }
        return false;
    }
}
