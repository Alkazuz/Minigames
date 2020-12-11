package br.alkazuz.minigame.game;

import org.bukkit.*;
import java.util.*;

public class LevelInfo
{
    public String worldName;
    public Location spawnPos;
    public BoundingBox startWall;
    public BoundingBox endWall;
    public BoundingBox finishRegion;
    
    public RoundLevel createLevel(World world) {
        RoundLevel rl = new RoundLevel();
        rl.world = world;
        rl.spawnPos = spawnPos.clone();
        rl.spawnPos.setWorld(world);
        
        rl.startWall = startWall;
        rl.endWall = endWall;
        rl.finishRegion = finishRegion;
        return rl;
    }
    
    public static LevelInfo deserialize(Map<String, Object> map) {
        LevelInfo li = new LevelInfo();
        li.worldName = (String)map.get("world_name");
        li.spawnPos = deserializePos(map.get("spawn_pos"));
        li.startWall = deserializeBB(map.get("start_wall_bb"));
        li.endWall = deserializeBB(map.get("end_wall_bb"));
        li.finishRegion = deserializeBB(map.get("finish_region_bb"));
        return li;
    }
    
    private static Location deserializePos(Object v) 
    {
        String[] s = ((String)v).split(";");
        return new Location(
            null, 
            Double.parseDouble(s[0]),
            Double.parseDouble(s[1]),
            Double.parseDouble(s[2]),
            s.length >= 5 ? Float.parseFloat(s[3]) : 0,
            s.length >= 5 ? Float.parseFloat(s[4]) : 0
        );
    }
    private static BoundingBox deserializeBB(Object v)
    {
        return BoundingBox.parse((String)v);
    }
}
