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
    public static int MAX_PLAYERS = 25;
    public static int MONEY_FIRST;
    public static int MONEY_SECOND;
    public static int MONEY_TREE;
    public static List<String> WELCOME;
    public static List<String> GAME_START;
    public static String DEATH;
    public static String LOSE;
    public static String SHOP_VIP;
    public static String SHOP_NORMAL;
    public static String WIN_YOU;
    public static String WIN_OTHER;
    public static String STARTING_WITH_PLAYERS;
    public static String STARTING_WITHOUT_PLAYERS;
    
    static {
        MinigameConfig.NAME = "Dragon Escape";
        MinigameConfig.LEVE_NAME_PREFIX = "dragon_";
        MinigameConfig.PVP = false;
        MinigameConfig.SPAWN_MOBS = false;
        MinigameConfig.KEEP_INVENTORY = false;
        MinigameConfig.FALL_DAMAGE = false;
        MinigameConfig.PLAYER_DAMAGE = true;
        MinigameConfig.BUILD = false;
        MinigameConfig.DROP = false;
        MinigameConfig.PICK_UP = false;
        MinigameConfig.WELCOME = null;
        MinigameConfig.GAME_START = null;
        MinigameConfig.DEATH = null;
        MinigameConfig.LOSE = null;
        MinigameConfig.SHOP_VIP = null;
        MinigameConfig.SHOP_NORMAL = null;
        MinigameConfig.WIN_YOU = null;
        MinigameConfig.WIN_OTHER = null;
        MinigameConfig.STARTING_WITH_PLAYERS = null;
        MinigameConfig.STARTING_WITHOUT_PLAYERS = null;
    }
    
    public static void load(Main instance) {
        FileConfiguration config = instance.config;
        MinigameConfig.START_TIME = config.getInt("configuration.start-time");
        MinigameConfig.MIN_PLAYERS = config.getInt("configuration.min-players");
        MinigameConfig.MONEY_FIRST = config.getInt("configuration.money-first");
        MinigameConfig.MONEY_SECOND = config.getInt("configuration.money-second");
        MinigameConfig.MONEY_TREE = config.getInt("configuration.money-three");
        MinigameConfig.WELCOME = loadList("messages.welcome", config);
        MinigameConfig.GAME_START = loadList("messages.game-start.message", config);
        MinigameConfig.DEATH = config.getString("messages.death").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.LOSE = config.getString("messages.lose").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.SHOP_VIP = config.getString("messages.shop.vip").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.SHOP_NORMAL = config.getString("messages.shop.normal").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.WIN_YOU = config.getString("messages.win.you").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.WIN_OTHER = config.getString("messages.win.other").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.STARTING_WITH_PLAYERS = config.getString("messages.starting.with-players").replace("&", "§").replace("%n%", "\n");
        MinigameConfig.STARTING_WITHOUT_PLAYERS = config.getString("messages.starting.without-players").replace("&", "§").replace("%n%", "\n");
    }
    
    public static List<String> loadList(String key, FileConfiguration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "§"));
        }
        return list;
    }
}
