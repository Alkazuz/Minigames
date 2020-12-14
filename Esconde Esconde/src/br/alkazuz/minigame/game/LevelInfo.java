package br.alkazuz.minigame.game;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

public class LevelInfo
{
    public String world;
    public Location startSpawn;
    public Location seekSpawn;
    
    public RoundLevel createLevel(World world) {
        RoundLevel rl = new RoundLevel();
        rl.world = world;
        (rl.startSpawn = this.startSpawn.clone()).setWorld(world);
        (rl.seekSpawn = this.seekSpawn.clone()).setWorld(world);
        return rl;
    }
    
    public static LevelInfo deserialize(Map<String, Object> map) {
        LevelInfo li = new LevelInfo();
        li.world = (String) map.get("world");
        li.startSpawn = deserializePos(map.get("startspawn"));
        li.seekSpawn = deserializePos(map.get("seekspawn"));
        return li;
    }
    
    private static Location deserializePos(Object p) {
        String[] coord = ((String)p).split(";");
        return new Location((World)null, (double)Integer.valueOf(coord[0]), (double)Integer.valueOf(coord[1]), (double)Integer.valueOf(coord[2]), (float)Float.valueOf(coord[3]), (float)Float.valueOf(coord[4]));
    }
}
