package br.alkazuz.arena.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import br.alkazuz.arena.main.Main;

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
    public static int MAX_PLAYERS = 30;
    public static int MONEY;
    public static List<String> WELCOME;
    public static List<String> GAME_START;
    public static String BOSSBAR;
    public static String KILL_ERROR;
    public static String KILLED;
    public static String ASSASSIN_QUIT;
    public static String VOTE;
    public static String ALREADY_VOTED;
    public static String KILLER_ROUND;
    public static String FATED_FAIL;
    public static String FATED;
    public static String NEXT_ROUND;
    public static String killer_next_round;
    public static String game_start_victim;
    public static String game_start_assassin;
    public static String lose;
    public static String win_you;
    public static String win_assassin;
    public static String win_victim;
    public static String SHOP_VIP;
    public static String SHOP_NORMAL;
    public static String STARTING_WITH_PLAYERS;
    public static String STARTING_WITHOUT_PLAYERS;
    
    static {
        MinigameConfig.NAME = "Arena";
        MinigameConfig.LEVE_NAME_PREFIX = "arena_";
        MinigameConfig.PVP = true;
        MinigameConfig.SPAWN_MOBS = false;
        MinigameConfig.KEEP_INVENTORY = true;
        MinigameConfig.FALL_DAMAGE = false;
        MinigameConfig.PLAYER_DAMAGE = false;
        MinigameConfig.BUILD = false;
        MinigameConfig.DROP = false;
        MinigameConfig.PICK_UP = false;
        MinigameConfig.WELCOME = null;
        MinigameConfig.GAME_START = null;
        MinigameConfig.BOSSBAR = null;
        MinigameConfig.KILL_ERROR = null;
        MinigameConfig.KILLED = null;
        MinigameConfig.ASSASSIN_QUIT = null;
        MinigameConfig.VOTE = null;
        MinigameConfig.ALREADY_VOTED = null;
        MinigameConfig.KILLER_ROUND = null;
        MinigameConfig.FATED_FAIL = null;
        MinigameConfig.FATED = null;
        MinigameConfig.NEXT_ROUND = null;
        MinigameConfig.killer_next_round = null;
        MinigameConfig.game_start_victim = null;
        MinigameConfig.game_start_assassin = null;
        MinigameConfig.lose = null;
        MinigameConfig.win_you = null;
        MinigameConfig.win_assassin = null;
        MinigameConfig.win_victim = null;
        MinigameConfig.SHOP_VIP = null;
        MinigameConfig.SHOP_NORMAL = null;
        MinigameConfig.STARTING_WITH_PLAYERS = null;
        MinigameConfig.STARTING_WITHOUT_PLAYERS = null;
    }
    
    public static void load(Main instance) {
        FileConfiguration config = instance.config;
        MinigameConfig.START_TIME = config.getInt("configuration.start-time");
        MinigameConfig.MIN_PLAYERS = config.getInt("configuration.min-players");
        MinigameConfig.MONEY = config.getInt("configuration.money-on-win");
        MinigameConfig.WELCOME = loadList("messages.welcome", config);
        MinigameConfig.BOSSBAR = config.getString("configuration.bossbar").replace("&", "§").replace("%n%", "\n");
    }
    
    public static List<String> loadList(String key, FileConfiguration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "§"));
        }
        return list;
    }
}
