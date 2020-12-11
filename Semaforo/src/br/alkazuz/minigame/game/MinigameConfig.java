package br.alkazuz.minigame.game;

import br.alkazuz.minigame.main.*;
import org.bukkit.configuration.file.*;
import java.util.*;

public class MinigameConfig
{
    public static String NAME = "Semaforo";
    public static String LEVEL_NAME_PREFIX = "semaforo_";
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
    public static int MONEY_WINNER;
    public static List<String> WELCOME;
    public static List<String> GAME_START;
    public static String DEATH;
    public static String LOSE_OTHER;
    public static String LOSE_YOU;
    public static String SHOP_VIP;
    public static String SHOP_NORMAL;
    public static String WIN_YOU;
    public static String WIN_OTHER;
    public static String STARTING_WITH_PLAYERS;
    public static String STARTING_WITHOUT_PLAYERS;
    public static String LIGHT_ACTION_GREEN;
    public static String LIGHT_ACTION_YELLOW;
    public static String LIGHT_ACTION_RED;
    public static String USE_SECOND_CHANCE;
    
    public static void load(Main instance) {
        FileConfiguration config = instance.config;
        START_TIME = config.getInt("configuration.start-time");
        MIN_PLAYERS = config.getInt("configuration.min-players");
        MONEY_WINNER = config.getInt("configuration.money");
        WELCOME = loadList("messages.welcome", config);
        GAME_START = loadList("messages.game-start.message", config);
        DEATH = config.getString("messages.death").replace("&", "§").replace("%n%", "\n");
        LOSE_YOU = config.getString("messages.lose.you").replace("&", "§").replace("%n%", "\n");
        LOSE_OTHER = config.getString("messages.lose.other").replace("&", "§").replace("%n%", "\n");
        SHOP_VIP = config.getString("messages.shop.vip").replace("&", "§").replace("%n%", "\n");
        SHOP_NORMAL = config.getString("messages.shop.normal").replace("&", "§").replace("%n%", "\n");
        WIN_YOU = config.getString("messages.win.you").replace("&", "§").replace("%n%", "\n");
        WIN_OTHER = config.getString("messages.win.other").replace("&", "§").replace("%n%", "\n");
        STARTING_WITH_PLAYERS = config.getString("messages.starting.with-players").replace("&", "§").replace("%n%", "\n");
        STARTING_WITHOUT_PLAYERS = config.getString("messages.starting.without-players").replace("&", "§").replace("%n%", "\n");

        LIGHT_ACTION_GREEN = config.getString("messages.light-green").replace("&", "§");
        LIGHT_ACTION_YELLOW = config.getString("messages.light-yellow").replace("&", "§");
        LIGHT_ACTION_RED = config.getString("messages.light-red").replace("&", "§");
        
        USE_SECOND_CHANCE = config.getString("messages.use-second-chance").replace("&", "§");
    }
    
    public static List<String> loadList(String key, FileConfiguration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "§"));
        }
        return list;
    }
}
