package br.alkazuz.minigame.game;

import org.bukkit.*;
import java.util.concurrent.atomic.*;

public class RoundLevel
{
    public Location startSpawn;
    public World world;
    public Location seekSpawn;
    public static AtomicInteger worldCounter;
    
    static {
        RoundLevel.worldCounter = new AtomicInteger(1);
    }
    
    public static String nextLevelName() {
        return String.valueOf(MinigameConfig.LEVE_NAME_PREFIX) + RoundLevel.worldCounter.getAndIncrement();
    }
}
