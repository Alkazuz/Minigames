package br.alkazuz.minigame.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import br.alkazuz.minigame.main.Main;

public class MinigameConfig
{
    public static String NAME = "BuildBattle";
    public static String LEVE_NAME_PREFIX = "bb_";
    public static boolean PVP = false;
    public static boolean SPAWN_MOBS = false;
    public static boolean KEEP_INVENTORY = true;
    public static boolean FALL_DAMAGE = false;
    public static boolean PLAYER_DAMAGE = false;
    public static boolean BUILD = false;
    public static boolean DROP = false;
    public static boolean PICK_UP= false;
    public static boolean VIP_ITEM = true;
    public static boolean SHOP_ITEM = false;
    public static boolean COUNTER = true;
    public static Long STARTED;
    public static Long LAST_PLAYER_JOINED;
    public static int START_TIME;
    public static int MIN_PLAYERS;
    public static int MAX_PLAYERS = 15;
    public static int MONEY_FIRST;
    public static int MONEY_SECOND;
    public static int MONEY_TREE;
    public static List<String> WELCOME;
    public static List<String> GAME_START;
    public static String boss;
    public static String lose;
    public static String win_you;
    public static String SHOP_VIP;
    public static String SHOP_NORMAL;
    public static String STARTING_WITH_PLAYERS;
    public static String STARTING_WITHOUT_PLAYERS;
    
    public static void load(Main instance) {
        FileConfiguration config = instance.config;
        MinigameConfig.START_TIME = config.getInt("configuration.start-time");
        MinigameConfig.MIN_PLAYERS = config.getInt("configuration.min-players");
        MinigameConfig.MONEY_FIRST = config.getInt("configuration.money-first");
        MinigameConfig.MONEY_SECOND = config.getInt("configuration.money-second");
        MinigameConfig.MONEY_TREE = config.getInt("configuration.money-three");
        MinigameConfig.WELCOME = loadList("messages.welcome", config);
        MinigameConfig.GAME_START = loadList("messages.game-start.message", config);
        MinigameConfig.lose = config.getString("messages.lose").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.boss = config.getString("messages.boss").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.win_you = config.getString("messages.win.you").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.STARTING_WITH_PLAYERS = config.getString("messages.starting.with-players").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.STARTING_WITHOUT_PLAYERS = config.getString("messages.starting.without-players").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.SHOP_VIP = config.getString("messages.shop.vip").replace("&", "�").replace("%n%", "\n");
        MinigameConfig.SHOP_NORMAL = config.getString("messages.shop.normal").replace("&", "�").replace("%n%", "\n");
    }
    
    public static List<String> loadList(String key, FileConfiguration config) {
        List<String> list = new ArrayList<String>();
        for (String d : config.getStringList(key)) {
            list.add(d.replace("&", "�"));
        }
        return list;
    }
}
