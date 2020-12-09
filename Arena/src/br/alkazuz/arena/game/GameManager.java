package br.alkazuz.arena.game;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.boydti.fawe.bukkit.v0.BukkitQueue_0;
import com.boydti.fawe.util.SetQueue;

import br.alkazuz.arena.api.ServerAPI;
import br.alkazuz.arena.main.Main;
import br.alkazuz.arena.utils.Utils;

public class GameManager
{
    public static Location lobby;
    public static List<Arena> games;
    public static HashMap<String, Arena> playing;
    public static Arena arena;
    
    static {
        GameManager.lobby = null;
        GameManager.games = new ArrayList<Arena>();
        GameManager.playing = new HashMap<String, Arena>();
    }
    
    public static void sendToLobby(final Player p) {
        ServerAPI.sendPlayer(p, "lobby");
    }
    
    public static Arena searchMatch() {
        return GameManager.arena;
    }
    
   
    
}
