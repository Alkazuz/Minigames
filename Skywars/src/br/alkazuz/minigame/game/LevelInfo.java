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
    public List<Location> cages = new ArrayList<Location>();
    
    public RoundLevel createLevel(World world, LevelInfo info) {
        RoundLevel rl = new RoundLevel();
        rl.world = world;
        rl.cages = info.cages;
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
        //System.out.println(Main.theInstance().games.get("level"));
        for(Object a : convertObjectToList(map.get("cages"))){
        	li.cages.add(deserializePos(a));
        }
        System.out.println(li.world + " possui "+li.cages.size()+" cages");
        return li;
    }
    
    private static Location deserializePos(Object p) {
        String[] coord = ((String)p).split(";");
        return new Location((World)null, (double)Integer.valueOf(coord[0]), (double)Integer.valueOf(coord[1]), (double)Integer.valueOf(coord[2]), (float)Float.valueOf(coord[3]), (float)Float.valueOf(coord[4]));
    }
}
