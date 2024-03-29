package br.alkazuz.minigame.game;

import br.alkazuz.minigame.main.*;
import org.bukkit.configuration.file.*;
import java.util.*;

public class MinigameConfig
{
    public static String NAME;
    public static String LEVE_NAME_PREFIX;
    public static boolean PVP;
    public static boolean SPAWN_MOBS;
    public static boolean KEEP_INVENTORY;
    public static boolean FALL_DAMAGE;
    public static boolean PLAYER_DAMAGE;
    public static boolean BUILD;
    public static boolean DROP;
    public static boolean PICK_UP;
    public static Long STARTED;
    public static Long LAST_PLAYER_JOINED;
    public static int START_TIME;
    public static int MIN_PLAYERS;
    public static int MONEY_HIDDEN;
    public static int MONEY_SEEK;
    public static List<String> WELCOME;
    public static List<String> GAME_START;
    public static String SEEK_QUIT;
    public static String win_you;
    public static String lose;
    public static String death;
    public static String STARTING_WITH_PLAYERS;
    public static String STARTING_WITHOUT_PLAYERS;
    
    static {
        MinigameConfig.NAME = "Esconde-Esconde";
        MinigameConfig.LEVE_NAME_PREFIX = "esc_";
        MinigameConfig.PVP = true;
        MinigameConfig.SPAWN_MOBS = false;
        MinigameConfig.KEEP_INVENTORY = true;
        MinigameConfig.FALL_DAMAGE = true;
        MinigameConfig.PLAYER_DAMAGE = true;
        MinigameConfig.BUILD = false;
        MinigameConfig.DROP = false;
        MinigameConfig.PICK_UP = false;
        
        MinigameConfig.WELCOME = null;
        MinigameConfig.GAME_START = null;
        MinigameConfig.SEEK_QUIT = null;
        MinigameConfig.lose = null;
        MinigameConfig.win_you = null;
        MinigameConfig.STARTING_WITH_PLAYERS = null;
        MinigameConfig.STARTING_WITHOUT_PLAYERS = null;
    }
    
    public static void load(Main instance) {
        FileConfiguration config = instance.config;
        MinigameConfig.START_TIME = config.getInt("configuration.start-time");
        MinigameConfig.MIN_PLAYERS = config.getInt("configuration.min-players");
        MinigameConfig.MONEY_HIDDEN = config.getInt("configuration.money-hidden");
        MinigameConfig.MONEY_SEEK = config.getInt("configuration.money-seek");
        MinigameConfig.WELCOME = loadList("messages.welcome", config);
        MinigameConfig.GAME_START = loadList("messages.game-start.message", config);
        MinigameConfig.win_you = config.getString("messages.win.you").replace("&", "�").replace("%n%", "\n");
        
        MinigameConfig.death = config.getString("messages.death").replace("&", "�").replace("%n%", "\n");
        
        MinigameConfig.SEEK_QUIT = config.getString("messages.quit").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.lose = config.getString("messages.lose").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.STARTING_WITH_PLAYERS = config.getString("messages.starting.with-players").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.STARTING_WITHOUT_PLAYERS = config.getString("messages.starting.without-players").replace("&", "�").replace("%n%", "\n");
    }
    
    public static List<String> loadList(String key, FileConfiguration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "�"));
        }
        return list;
    }
}
