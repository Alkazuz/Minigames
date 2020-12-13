package br.alkazuz.minigame.game;

import org.bukkit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.*;

public class RoundLevel
{
	public Location feastSpawn;
    public World world;
    public List<Location> cages = new ArrayList<Location>();
    public List<Location> feastChests = new ArrayList<Location>();
    public List<Location> MFeastChests = new ArrayList<Location>();
    public static AtomicInteger worldCounter = new AtomicInteger(1);
    
    public RoundLevel(Location feastSpawn) {
    	this.feastSpawn = feastSpawn;
    }
    
    public static String nextLevelName() {
        return String.valueOf(MinigameConfig.LEVE_NAME_PREFIX) + RoundLevel.worldCounter.getAndIncrement();
    }
}
