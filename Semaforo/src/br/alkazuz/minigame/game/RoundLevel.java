package br.alkazuz.minigame.game;

import org.bukkit.*;
import java.util.concurrent.atomic.*;

public class RoundLevel
{
    public Location spawnPos;
    public BoundingBox startWall;
    public BoundingBox endWall;
    public BoundingBox finishRegion;
    
    public World world;
    public static AtomicInteger worldCounter;
    
    static {
        RoundLevel.worldCounter = new AtomicInteger(1);
    }
    
    public static String nextLevelName() {
        return String.valueOf(MinigameConfig.LEVEL_NAME_PREFIX) + RoundLevel.worldCounter.getAndIncrement();
    }
}
