package br.alkazuz.minigame.game;

import br.alkazuz.minigame.main.*;
import org.bukkit.configuration.file.*;
import java.util.*;

public class MinigameConfig
{
    public static String NAME = "SkyWars";
    public static String LEVE_NAME_PREFIX = "sw_";
    public static boolean PVP = true;
    public static boolean SPAWN_MOBS = false;
    public static boolean KEEP_INVENTORY = false;
    public static boolean FALL_DAMAGE = false;
    public static boolean PLAYER_DAMAGE = false;
    public static boolean BUILD = true;
    public static boolean DROP = true;
    public static boolean PICK_UP= true;
    public static boolean VIP_ITEM = false;
    public static boolean SHOP_ITEM = false;
    public static boolean COUNTER = true;
    public static Long STARTED;
    public static Long LAST_PLAYER_JOINED;
    public static int START_TIME;
    public static int MIN_PLAYERS;
    public static int MAX_PLAYERS = 30;
    public static int MONEY;
    public static List<String> WELCOME;
    public static List<String> GAME_START;
    public static String lose;
    public static String win_you;
    public static String KILL;
    public static String KILL_VOID;
    public static String STREAK_DOUBLEKILL;
    public static String STREAK_TRIPLEKILL;
    public static String STREAK_QUADRAKILL;
    public static String STREAK_PENTAKILL;
    public static String STARTING_WITH_PLAYERS;
    public static String STARTING_WITHOUT_PLAYERS;
    
    public static void load(Main instance) {
        FileConfiguration config = instance.config;
        START_TIME = config.getInt("configuration.start-time");
        MIN_PLAYERS = config.getInt("configuration.min-players");
        MONEY = config.getInt("configuration.money-on-win");
        WELCOME = loadList("messages.welcome", config);
        GAME_START = loadList("messages.game-start.message", config);
        lose = config.getString("messages.lose").replace("&", "§").replace("%n%", "\n");
        win_you = config.getString("messages.win.you").replace("&", "§").replace("%n%", "\n");
        KILL = config.getString("messages.kill").replace("&", "§").replace("%n%", "\n");
        KILL_VOID = config.getString("messages.kill-void").replace("&", "§").replace("%n%", "\n");
        STREAK_DOUBLEKILL = config.getString("messages.streak.double-kill").replace("&", "§").replace("%n%", "\n");
        STREAK_TRIPLEKILL = config.getString("messages.streak.triple-kill").replace("&", "§").replace("%n%", "\n");
        STREAK_QUADRAKILL = config.getString("messages.streak.quadra-kill").replace("&", "§").replace("%n%", "\n");
        STREAK_PENTAKILL = config.getString("messages.streak.penta-kill").replace("&", "§").replace("%n%", "\n");
        STARTING_WITH_PLAYERS = config.getString("messages.starting.with-players").replace("&", "§").replace("%n%", "\n");
        STARTING_WITHOUT_PLAYERS = config.getString("messages.starting.without-players").replace("&", "§").replace("%n%", "\n");
    }
    
    public static List<String> loadList(String key, FileConfiguration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "§"));
        }
        return list;
    }
}
