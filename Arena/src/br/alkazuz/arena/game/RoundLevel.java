package br.alkazuz.arena.game;

import org.bukkit.*;
import java.util.concurrent.atomic.*;

public class RoundLevel
{
    public Location startSpawn;
    public World world;
    public static AtomicInteger worldCounter = new AtomicInteger(1);
    
    public static String nextLevelName() {
        return String.valueOf(MinigameConfig.LEVE_NAME_PREFIX) + RoundLevel.worldCounter.getAndIncrement();
    }
}
