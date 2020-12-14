package br.alkazuz.minigame.game;

import org.bukkit.*;
import java.util.*;

public class LevelInfo
{
    public String world;
    public Location lobbySpawn;
    public Location startSpawn;
    public Location dragonSpawn;
    
    public RoundLevel createLevel(World world) {
        RoundLevel rl = new RoundLevel();
        rl.world = world;
        (rl.lobbySpawn = this.lobbySpawn.clone()).setWorld(world);
        (rl.startSpawn = this.startSpawn.clone()).setWorld(world);
        (rl.dragonSpawn = this.dragonSpawn.clone()).setWorld(world);
        return rl;
    }
    
    public static LevelInfo deserialize(Map<String, Object> map) {
        LevelInfo li = new LevelInfo();
        li.world = (String) map.get("world");
        li.lobbySpawn = deserializePos(map.get("joinspawn"));
        li.startSpawn = deserializePos(map.get("startspawn"));
        li.dragonSpawn = deserializePos(map.get("dragonspawn"));
        return li;
    }
    
    private static Location deserializePos(Object p) {
        String[] coord = ((String)p).split(";");
        return new Location((World)null, (double)Integer.valueOf(coord[0]), (double)Integer.valueOf(coord[1]), (double)Integer.valueOf(coord[2]), (float)Float.valueOf(coord[3]), (float)Float.valueOf(coord[4]));
    }
}
