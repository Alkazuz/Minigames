package br.alkazuz.minigame.game;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;

public class LevelInfo
{
    public String world;
    public Location feastSpawn;
    public List<Location> cages = new ArrayList<Location>();
    public List<Location> feastChests = new ArrayList<Location>();
    public List<Location> MFeastChests = new ArrayList<Location>();
    
    public RoundLevel createLevel(World world, LevelInfo info) {
        RoundLevel rl = new RoundLevel(info.feastSpawn);
        rl.world = world;
        (rl.feastSpawn = this.feastSpawn.clone()).setWorld(world);
        rl.cages = info.cages;
        rl.feastChests = info.feastChests;
        rl.MFeastChests = info.MFeastChests;
        return rl;
    }
    
    private static List<?> convertObjectToList(Object obj) {
        List<?> list = new ArrayList<>();
        if (obj.getClass().isArray()) {
            list = Arrays.asList((Object[])obj);
        } else if (obj instanceof Collection) {
            list = new ArrayList<>((Collection<?>)obj);
        }
        return list;
    }
    
    public static LevelInfo deserialize(Map<String, Object> map) {
        LevelInfo li = new LevelInfo();
        li.world = (String) map.get("world");
        li.feastSpawn = deserializePos((String) map.get("feast"));
        for(Object a : convertObjectToList(map.get("mini-feast-chests"))){
        	li.MFeastChests.add(deserializePos(a));
        }
        for(Object a : convertObjectToList(map.get("feast-chests"))){
        	li.feastChests.add(deserializePos(a));
        }
        for(Object a : convertObjectToList(map.get("cages"))){
        	li.cages.add(deserializePos(a));
        }
        return li;
    }
    
    private static Location deserializePos(Object p) {
        String[] coord = ((String)p).split(";");
        return new Location((World)null, (double)Integer.valueOf(coord[0]), (double)Integer.valueOf(coord[1]), (double)Integer.valueOf(coord[2]), (float)Float.valueOf(coord[3]), (float)Float.valueOf(coord[4]));
    }
}
